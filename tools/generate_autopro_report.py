# -*- coding: utf-8 -*-
from __future__ import annotations

import math
import re
from datetime import date
from pathlib import Path
from typing import Iterable

from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_CELL_VERTICAL_ALIGNMENT, WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Inches, Pt, RGBColor
from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "reports"
DIAGRAM_DIR = OUT_DIR / "diagrams"
DOCX_PATH = OUT_DIR / "Bao_cao_chi_tiet_du_an_AutoPro.docx"


def ensure_dirs() -> None:
    OUT_DIR.mkdir(exist_ok=True)
    DIAGRAM_DIR.mkdir(exist_ok=True)


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    names = [
        "arialbd.ttf" if bold else "arial.ttf",
        "calibrib.ttf" if bold else "calibri.ttf",
        "segoeuib.ttf" if bold else "segoeui.ttf",
        "C:/Windows/Fonts/arialbd.ttf" if bold else "C:/Windows/Fonts/arial.ttf",
        "C:/Windows/Fonts/calibrib.ttf" if bold else "C:/Windows/Fonts/calibri.ttf",
    ]
    for name in names:
        try:
            return ImageFont.truetype(name, size)
        except OSError:
            pass
    return ImageFont.load_default()


FONT_TITLE = load_font(40, True)
FONT_H2 = load_font(30, True)
FONT_BOX = load_font(23, True)
FONT_TEXT = load_font(20)
FONT_SMALL = load_font(17)


def text_size(draw: ImageDraw.ImageDraw, text: str, font: ImageFont.ImageFont) -> tuple[int, int]:
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[2] - bbox[0], bbox[3] - bbox[1]


def wrap_text(draw: ImageDraw.ImageDraw, text: str, font: ImageFont.ImageFont, max_width: int) -> list[str]:
    lines: list[str] = []
    for raw in str(text).splitlines() or [""]:
        words = raw.split()
        if not words:
            lines.append("")
            continue
        line = words[0]
        for word in words[1:]:
            candidate = f"{line} {word}"
            if text_size(draw, candidate, font)[0] <= max_width:
                line = candidate
            else:
                lines.append(line)
                line = word
        lines.append(line)
    return lines


def draw_centered_text(
    draw: ImageDraw.ImageDraw,
    bbox: tuple[int, int, int, int],
    text: str,
    font: ImageFont.ImageFont,
    fill: str = "#111827",
    max_width_pad: int = 24,
) -> None:
    x1, y1, x2, y2 = bbox
    max_width = max(20, x2 - x1 - max_width_pad)
    lines = wrap_text(draw, text, font, max_width)
    heights = [text_size(draw, line, font)[1] for line in lines]
    total_h = sum(heights) + max(0, len(lines) - 1) * 7
    y = y1 + (y2 - y1 - total_h) / 2
    for line, h in zip(lines, heights):
        w = text_size(draw, line, font)[0]
        draw.text((x1 + (x2 - x1 - w) / 2, y), line, font=font, fill=fill)
        y += h + 7


def rounded_box(
    draw: ImageDraw.ImageDraw,
    bbox: tuple[int, int, int, int],
    text: str,
    fill: str = "#F8FAFC",
    outline: str = "#334155",
    font: ImageFont.ImageFont = FONT_BOX,
    radius: int = 22,
) -> None:
    draw.rounded_rectangle(bbox, radius=radius, fill=fill, outline=outline, width=3)
    draw_centered_text(draw, bbox, text, font)


def ellipse(
    draw: ImageDraw.ImageDraw,
    bbox: tuple[int, int, int, int],
    text: str,
    fill: str = "#EFF6FF",
    outline: str = "#2563EB",
    font: ImageFont.ImageFont = FONT_SMALL,
) -> None:
    draw.ellipse(bbox, fill=fill, outline=outline, width=3)
    draw_centered_text(draw, bbox, text, font, max_width_pad=36)


def arrow(
    draw: ImageDraw.ImageDraw,
    start: tuple[int, int],
    end: tuple[int, int],
    fill: str = "#334155",
    width: int = 4,
    label: str | None = None,
    label_offset: tuple[int, int] = (0, -24),
) -> None:
    draw.line([start, end], fill=fill, width=width)
    angle = math.atan2(end[1] - start[1], end[0] - start[0])
    size = 16
    points = [
        end,
        (end[0] - size * math.cos(angle - math.pi / 6), end[1] - size * math.sin(angle - math.pi / 6)),
        (end[0] - size * math.cos(angle + math.pi / 6), end[1] - size * math.sin(angle + math.pi / 6)),
    ]
    draw.polygon(points, fill=fill)
    if label:
        mid = ((start[0] + end[0]) // 2 + label_offset[0], (start[1] + end[1]) // 2 + label_offset[1])
        tw, th = text_size(draw, label, FONT_SMALL)
        draw.rounded_rectangle((mid[0] - 8, mid[1] - 5, mid[0] + tw + 8, mid[1] + th + 7), radius=8, fill="#FFFFFF")
        draw.text(mid, label, font=FONT_SMALL, fill=fill)


def canvas(title: str, size: tuple[int, int] = (1800, 1200)) -> tuple[Image.Image, ImageDraw.ImageDraw]:
    img = Image.new("RGB", size, "white")
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, size[0], 86), fill="#0F172A")
    draw.text((40, 22), title, font=FONT_TITLE, fill="#FFFFFF")
    return img, draw


def save_image(img: Image.Image, filename: str) -> Path:
    path = DIAGRAM_DIR / filename
    img.save(path, quality=95)
    return path


def draw_architecture() -> Path:
    img, draw = canvas("Sơ đồ kiến trúc tổng thể")
    rounded_box(draw, (70, 170, 360, 290), "Người dùng\nBrowser", "#ECFEFF", "#0891B2")
    rounded_box(draw, (500, 135, 850, 225), "Tomcat 10\nJakarta Servlet Runtime", "#F0FDF4", "#16A34A")
    rounded_box(draw, (500, 285, 850, 385), "JSP + JSTL\nGiao diện server-rendered", "#F0FDF4", "#16A34A")
    rounded_box(draw, (500, 450, 850, 560), "Servlet Controller\nĐiều phối request/session", "#F0FDF4", "#16A34A")
    rounded_box(draw, (980, 450, 1280, 560), "DAO Layer\nSQL + PreparedStatement", "#FEFCE8", "#CA8A04")
    rounded_box(draw, (1410, 430, 1700, 580), "MySQL\ncar_market", "#FFF7ED", "#EA580C")
    rounded_box(draw, (980, 180, 1280, 300), "Model/POJO\nXe, BaiDang, NguoiDung...", "#EEF2FF", "#4F46E5")
    rounded_box(draw, (980, 690, 1280, 805), "Util\nPassword, OTP, URL, Email", "#FDF2F8", "#DB2777")
    rounded_box(draw, (1410, 680, 1700, 790), "SMTP Gmail\nJakarta Mail", "#FDF2F8", "#DB2777")
    rounded_box(draw, (1410, 840, 1700, 950), "Twilio SMS\nJava HttpClient", "#FDF2F8", "#DB2777")
    rounded_box(draw, (500, 720, 850, 840), "Uploads/static assets\nảnh xe, CCCD, avatar", "#F8FAFC", "#64748B")

    arrow(draw, (360, 230), (500, 180), label="HTTP")
    arrow(draw, (850, 180), (980, 240), label="object")
    arrow(draw, (670, 225), (670, 285), label="forward")
    arrow(draw, (670, 385), (670, 450), label="form/action")
    arrow(draw, (850, 505), (980, 505), label="CRUD")
    arrow(draw, (1280, 505), (1410, 505), label="JDBC")
    arrow(draw, (650, 560), (650, 720), label="upload")
    arrow(draw, (850, 720), (980, 745), label="path")
    arrow(draw, (1280, 745), (1410, 735), label="email")
    arrow(draw, (1280, 805), (1410, 895), label="SMS")
    arrow(draw, (500, 320), (360, 250), label="HTML/CSS/JS", label_offset=(-80, 12))
    return save_image(img, "01_architecture.png")


def draw_mvc() -> Path:
    img, draw = canvas("Sơ đồ mô hình MVC/package")
    rounded_box(draw, (80, 190, 430, 360), "View\nweb/*.jsp\nJSTL, Bootstrap, JS", "#EFF6FF", "#2563EB")
    rounded_box(draw, (590, 180, 940, 370), "Controller\n49 Servlet\n@WebServlet", "#F0FDF4", "#16A34A")
    rounded_box(draw, (1100, 180, 1450, 370), "Model\n13 POJO\nGetter/Setter", "#EEF2FF", "#4F46E5")
    rounded_box(draw, (590, 570, 940, 760), "DAO\n15 lớp DAO\nPreparedStatement", "#FEFCE8", "#CA8A04")
    rounded_box(draw, (1100, 570, 1450, 760), "DBContext\nDriverManager\nAUTOPRO_DB_*", "#FFF7ED", "#EA580C")
    rounded_box(draw, (80, 570, 430, 760), "Util\nPasswordUtil\nOtpDeliveryService\nWebAppUrlUtil", "#FDF2F8", "#DB2777")
    rounded_box(draw, (590, 900, 1450, 1030), "MySQL database: nguoidung, xe, baidang, hoithoai, yeucaumuaxe, giaodichxe...", "#F8FAFC", "#334155")

    arrow(draw, (430, 275), (590, 275), label="submit/link")
    arrow(draw, (590, 330), (430, 330), label="forward", label_offset=(-60, 18))
    arrow(draw, (940, 275), (1100, 275), label="setAttribute")
    arrow(draw, (765, 370), (765, 570), label="gọi nghiệp vụ")
    arrow(draw, (940, 665), (1100, 665), label="getConnection")
    arrow(draw, (1275, 760), (1275, 900), label="SQL")
    arrow(draw, (590, 665), (430, 665), label="hash/OTP/url", label_offset=(-80, -26))
    return save_image(img, "02_mvc.png")


def draw_use_case() -> Path:
    img, draw = canvas("Sơ đồ Use Case tổng quan", (2200, 1450))
    actors = {
        "Khách xem": (70, 220, 270, 310),
        "Thành viên": (70, 610, 270, 700),
        "Seller": (1930, 420, 2130, 510),
        "Admin": (1930, 900, 2130, 990),
    }
    for name, box in actors.items():
        rounded_box(draw, box, name, "#F8FAFC", "#0F172A", FONT_BOX)

    cases = [
        ("Xem danh sách xe", 500, 170),
        ("Tìm kiếm/lọc xe", 840, 170),
        ("Xem chi tiết xe", 1180, 170),
        ("Đăng ký/đăng nhập", 500, 380),
        ("Quên mật khẩu OTP", 840, 380),
        ("Lưu bài đăng", 1180, 380),
        ("Gửi yêu cầu kết nối", 500, 590),
        ("Chat với người bán", 840, 590),
        ("Theo dõi lịch hẹn", 1180, 590),
        ("Tạo/xác nhận giao dịch", 840, 800),
        ("Đăng ký seller", 500, 1010),
        ("Đăng/sửa/xóa bài xe", 840, 1010),
        ("Xem seller dashboard", 1180, 1010),
        ("Duyệt bài đăng", 500, 1220),
        ("Duyệt hồ sơ seller", 840, 1220),
        ("Quản lý user/danh mục", 1180, 1220),
        ("Xử lý báo cáo/giao dịch", 1520, 1220),
        ("Xem dashboard admin", 1520, 1010),
    ]
    case_centers: dict[str, tuple[int, int]] = {}
    for text, x, y in cases:
        bbox = (x, y, x + 250, y + 110)
        ellipse(draw, bbox, text, "#EFF6FF", "#2563EB")
        case_centers[text] = (x + 125, y + 55)

    links = {
        "Khách xem": ["Xem danh sách xe", "Tìm kiếm/lọc xe", "Xem chi tiết xe", "Đăng ký/đăng nhập"],
        "Thành viên": ["Quên mật khẩu OTP", "Lưu bài đăng", "Gửi yêu cầu kết nối", "Chat với người bán", "Theo dõi lịch hẹn", "Tạo/xác nhận giao dịch", "Đăng ký seller"],
        "Seller": ["Đăng/sửa/xóa bài xe", "Xem seller dashboard", "Chat với người bán", "Tạo/xác nhận giao dịch"],
        "Admin": ["Duyệt bài đăng", "Duyệt hồ sơ seller", "Quản lý user/danh mục", "Xử lý báo cáo/giao dịch", "Xem dashboard admin"],
    }
    for actor, linked in links.items():
        ax1, ay1, ax2, ay2 = actors[actor]
        start = (ax2, (ay1 + ay2) // 2) if ax1 < 1000 else (ax1, (ay1 + ay2) // 2)
        for case in linked:
            cx, cy = case_centers[case]
            target = (cx - 125, cy) if start[0] < cx else (cx + 125, cy)
            draw.line([start, target], fill="#64748B", width=2)
    return save_image(img, "03_use_case.png")


def table_box(draw: ImageDraw.ImageDraw, x: int, y: int, w: int, title: str, fields: Iterable[str]) -> tuple[int, int, int, int]:
    field_list = list(fields)
    h = 54 + 32 * len(field_list)
    draw.rounded_rectangle((x, y, x + w, y + h), radius=14, fill="#FFFFFF", outline="#334155", width=3)
    draw.rectangle((x, y, x + w, y + 54), fill="#E0F2FE", outline="#334155", width=3)
    draw.text((x + 16, y + 14), title, font=FONT_BOX, fill="#0F172A")
    yy = y + 67
    for f in field_list:
        draw.text((x + 18, yy), f, font=FONT_SMALL, fill="#334155")
        yy += 32
    return x, y, x + w, y + h


def draw_erd() -> Path:
    img, draw = canvas("Sơ đồ ERD logic", (2400, 1700))
    boxes = {}
    boxes["nguoidung"] = table_box(draw, 80, 150, 330, "nguoidung", ["PK maNguoiDung", "hoTen, email, phone", "matKhau, vaiTro", "trangThai, avatar", "otpReset, otpHetHan"])
    boxes["dangkyseller"] = table_box(draw, 80, 520, 330, "dangkyseller", ["PK maDangKySeller", "FK maNguoiDung", "FK maTinh", "CCCD + ảnh xác minh", "trangThai, lyDoTuChoi"])
    boxes["thongbao"] = table_box(draw, 80, 940, 330, "thongbao", ["PK maThongBao", "FK maNguoiDung", "loai, tieuDe", "noiDung, urlDich", "daDoc, ngayTao"])
    boxes["hangxe"] = table_box(draw, 520, 150, 300, "hangxe", ["PK maHang", "tenHang, slug", "logoUrl", "thuTuSapXep"])
    boxes["tinhthanh"] = table_box(draw, 520, 500, 300, "tinhthanh", ["PK maTinh", "tenTinh", "mien"])
    boxes["xe"] = table_box(draw, 960, 150, 360, "xe", ["PK maXe", "FK maHang", "tenXe, gia", "kieuDang, tinhTrang", "năm SX, nhiên liệu", "maTinh, maNguoiBan"])
    boxes["xe_hinhanh"] = table_box(draw, 1450, 150, 330, "xe_hinhanh", ["PK maHinhAnh", "FK maXe", "duongDan", "thuTu"])
    boxes["baidang"] = table_box(draw, 960, 560, 360, "baidang", ["PK maBaiDang", "FK maNguoiDung", "FK maXe, maTinh", "tieuDe, moTa", "trangThai, ngayDang", "luotXem"])
    boxes["savedpost"] = table_box(draw, 1450, 560, 330, "savedpost", ["FK maNguoiDung", "FK maBaiDang", "ngayLuu"])
    boxes["baocao"] = table_box(draw, 1900, 560, 330, "baocao", ["PK maBaoCao", "FK maBaiDang", "FK maNguoiBaoCao", "lyDo, chiTiet", "trangThai, admin xử lý"])
    boxes["hoithoai"] = table_box(draw, 960, 1030, 360, "hoithoai", ["PK maHoiThoai", "FK maNguoiMua", "FK maNguoiBan", "FK maXe"])
    boxes["tinnhan"] = table_box(draw, 1450, 1030, 330, "tinnhan", ["PK maTinNhan", "FK maHoiThoai", "FK maNguoiGui", "noiDung, thoiGian", "trangThai"])
    boxes["yeucau"] = table_box(draw, 520, 1030, 340, "yeucaumuaxe", ["PK maYeuCau", "FK maNguoiMua", "FK maNguoiBan", "FK maXe, maHoiThoai", "giaThamKhao", "trangThai"])
    boxes["lichhen"] = table_box(draw, 520, 1390, 340, "lichhenxemxe", ["PK maLichHen", "FK maYeuCau", "thoiGianHen", "diaDiemHen", "trangThai"])
    boxes["giaodich"] = table_box(draw, 1450, 1390, 420, "giaodichxe", ["PK maGiaoDich", "FK maYeuCau, maXe", "FK maNguoiMua/maNguoiBan", "giaChot, ngayGiaoXe", "xacNhan 2 bên, trangThai"])

    def mid_right(name: str) -> tuple[int, int]:
        x1, y1, x2, y2 = boxes[name]
        return x2, (y1 + y2) // 2

    def mid_left(name: str) -> tuple[int, int]:
        x1, y1, x2, y2 = boxes[name]
        return x1, (y1 + y2) // 2

    def mid_bottom(name: str) -> tuple[int, int]:
        x1, y1, x2, y2 = boxes[name]
        return (x1 + x2) // 2, y2

    def mid_top(name: str) -> tuple[int, int]:
        x1, y1, x2, y2 = boxes[name]
        return (x1 + x2) // 2, y1

    arrow(draw, mid_right("nguoidung"), mid_left("dangkyseller"), label="1-n")
    arrow(draw, mid_right("nguoidung"), mid_left("thongbao"), label="1-n", label_offset=(-20, 20))
    arrow(draw, mid_right("hangxe"), mid_left("xe"), label="1-n")
    arrow(draw, mid_right("tinhthanh"), mid_left("xe"), label="1-n", label_offset=(-20, 18))
    arrow(draw, mid_right("tinhthanh"), mid_left("dangkyseller"), label="1-n", label_offset=(-10, 18))
    arrow(draw, mid_right("xe"), mid_left("xe_hinhanh"), label="1-n")
    arrow(draw, mid_bottom("xe"), mid_top("baidang"), label="1-n")
    arrow(draw, mid_right("baidang"), mid_left("savedpost"), label="1-n")
    arrow(draw, mid_right("baidang"), mid_left("baocao"), label="1-n")
    arrow(draw, mid_bottom("baidang"), mid_top("hoithoai"), label="xe")
    arrow(draw, mid_right("hoithoai"), mid_left("tinnhan"), label="1-n")
    arrow(draw, mid_left("hoithoai"), mid_right("yeucau"), label="0..1")
    arrow(draw, mid_bottom("yeucau"), mid_top("lichhen"), label="1-n")
    arrow(draw, (860, 1180), (1450, 1460), label="1-1 giao dịch")
    arrow(draw, mid_right("yeucau"), mid_left("giaodich"), label="1-1", label_offset=(30, -40))
    return save_image(img, "04_erd.png")


def draw_sequence(title: str, filename: str, actors: list[str], messages: list[tuple[int, int, str]]) -> Path:
    width = max(1600, 260 + 280 * len(actors))
    height = 220 + 95 * len(messages)
    img, draw = canvas(title, (width, height))
    xs = [160 + i * ((width - 320) // max(1, len(actors) - 1)) for i in range(len(actors))]
    top = 155
    bottom = height - 70
    for x, actor in zip(xs, actors):
        rounded_box(draw, (x - 105, top, x + 105, top + 70), actor, "#EFF6FF", "#2563EB", FONT_SMALL, radius=14)
        draw.line((x, top + 70, x, bottom), fill="#94A3B8", width=3)
    y = top + 125
    for idx, (src, dst, label) in enumerate(messages, 1):
        sx, dx = xs[src], xs[dst]
        y_line = y + (idx - 1) * 82
        arrow(draw, (sx, y_line), (dx, y_line), "#334155", 3)
        lx = min(sx, dx) + 18
        ly = y_line - 35
        draw.rounded_rectangle((lx - 10, ly - 6, lx + min(720, text_size(draw, label, FONT_SMALL)[0]) + 18, ly + 28), radius=8, fill="#FFFFFF")
        draw.text((lx, ly), label, font=FONT_SMALL, fill="#111827")
    return save_image(img, filename)


def draw_status_diagram() -> Path:
    img, draw = canvas("Sơ đồ trạng thái giao dịch xe", (1900, 1150))
    states = {
        "MOI_TAO": (110, 210, 390, 310),
        "CHO_XAC_NHAN_HAI_BEN": (560, 200, 910, 320),
        "CHO_XAC_NHAN_CON_LAI": (1080, 200, 1430, 320),
        "HOAN_TAT": (1560, 210, 1810, 310),
        "HUY": (560, 650, 850, 750),
        "TRANH_CHAP": (1080, 650, 1430, 750),
        "DA_BAN": (1560, 650, 1810, 750),
    }
    for label, box in states.items():
        fill = "#DCFCE7" if label in {"HOAN_TAT", "DA_BAN"} else "#FEF3C7" if label == "HUY" else "#EFF6FF"
        rounded_box(draw, box, label, fill, "#334155", FONT_BOX)
    arrow(draw, (390, 260), (560, 260), label="tạo từ yêu cầu")
    arrow(draw, (910, 260), (1080, 260), label="một bên xác nhận")
    arrow(draw, (1430, 260), (1560, 260), label="hai bên xác nhận")
    arrow(draw, (710, 320), (710, 650), label="hủy")
    arrow(draw, (1255, 320), (1255, 650), label="tranh chấp/admin")
    arrow(draw, (1685, 310), (1685, 650), label="đánh dấu bài đăng")
    draw.text((110, 910), "Ý nghĩa: giao dịch khởi tạo từ yeucaumuaxe, người mua và người bán cùng xác nhận để hoàn tất. Khi HOAN_TAT, bài đăng được chuyển sang trạng thái da_ban và các yêu cầu khác của xe bị đóng/hủy.", font=FONT_TEXT, fill="#111827")
    return save_image(img, "05_transaction_status.png")


def draw_deployment() -> Path:
    img, draw = canvas("Sơ đồ triển khai", (1800, 1150))
    rounded_box(draw, (100, 220, 420, 380), "Client\nChrome/Edge\nHTML, CSS, JS", "#EFF6FF", "#2563EB")
    rounded_box(draw, (620, 160, 1080, 440), "Application Server\nApache Tomcat\n/xetot\nxetot.war", "#F0FDF4", "#16A34A")
    rounded_box(draw, (1290, 180, 1680, 360), "MySQL Server\nDatabase car_market", "#FFF7ED", "#EA580C")
    rounded_box(draw, (620, 560, 1080, 710), "Filesystem\nweb/uploads\nảnh xe, CCCD, avatar", "#F8FAFC", "#64748B")
    rounded_box(draw, (1290, 540, 1680, 670), "SMTP Gmail\nAUTOPRO_SMTP_*", "#FDF2F8", "#DB2777")
    rounded_box(draw, (1290, 760, 1680, 890), "Twilio API\nAUTOPRO_TWILIO_*", "#FDF2F8", "#DB2777")
    arrow(draw, (420, 300), (620, 300), label="HTTP request")
    arrow(draw, (1080, 270), (1290, 270), label="JDBC 3306")
    arrow(draw, (850, 440), (850, 560), label="save/read")
    arrow(draw, (1080, 620), (1290, 605), label="email OTP")
    arrow(draw, (1080, 665), (1290, 820), label="SMS OTP")
    arrow(draw, (620, 360), (420, 340), label="HTML response", label_offset=(-95, 18))
    return save_image(img, "06_deployment.png")


def draw_seller_flow() -> Path:
    return draw_sequence(
        "Sequence: đăng ký và phê duyệt seller",
        "07_seller_sequence.png",
        ["User", "DangKySellerServlet", "DangKySellerDAO", "MySQL", "Admin"],
        [
            (0, 1, "Gửi hồ sơ: thông tin cá nhân + ảnh CCCD"),
            (1, 2, "validate, lưu ảnh, createRegistration()"),
            (2, 3, "INSERT dangkyseller, INSERT thongbao admin"),
            (4, 1, "Mở danh sách/hồ sơ chờ duyệt"),
            (1, 2, "approveSeller()/rejectSeller()"),
            (2, 3, "UPDATE trạng thái, cập nhật vaiTro seller nếu duyệt"),
            (2, 3, "INSERT thông báo cho người đăng ký"),
        ],
    )


def draw_post_flow() -> Path:
    return draw_sequence(
        "Sequence: seller đăng bài bán xe",
        "08_post_sequence.png",
        ["Seller", "AddPostServlet", "XeDAO", "BaiDangDAO", "MySQL", "Admin"],
        [
            (0, 1, "GET form: hãng xe, tỉnh thành"),
            (0, 1, "POST thông tin xe + nhiều ảnh"),
            (1, 2, "insertXe(), lưu xe_hinhanh"),
            (2, 4, "INSERT xe, INSERT xe_hinhanh"),
            (1, 3, "insertBaiDang(trangThai=cho_duyet)"),
            (3, 4, "INSERT baidang"),
            (5, 3, "Duyệt/từ chối/cảnh báo bài đăng"),
            (3, 4, "UPDATE baidang.trangThai"),
        ],
    )


def draw_connection_flow() -> Path:
    return draw_sequence(
        "Sequence: yêu cầu kết nối, chat và lịch hẹn",
        "09_connection_sequence.png",
        ["Buyer", "Detail/Chat Servlet", "YeuCauKetNoiDAO", "HoiThoaiDAO", "MySQL", "Seller"],
        [
            (0, 1, "Xem chi tiết xe và gửi yêu cầu kết nối"),
            (1, 2, "kiểm tra quyền, trùng yêu cầu, xe đã bán"),
            (2, 3, "tìm/tạo hội thoại cho buyer-seller-xe"),
            (3, 4, "SELECT/INSERT hoithoai"),
            (2, 4, "INSERT yeucaumuaxe + lichhenxemxe"),
            (5, 1, "Mở chat-list/chat"),
            (1, 3, "isParticipant(), getThongTinHoiThoai()"),
            (0, 1, "fetch chat-messages / POST tin nhắn"),
            (1, 4, "SELECT/INSERT tinnhan"),
        ],
    )


def draw_login_flow() -> Path:
    return draw_sequence(
        "Sequence: đăng nhập và khôi phục mật khẩu OTP",
        "10_login_otp_sequence.png",
        ["User", "Login/Forgot Servlet", "NguoiDungDAO", "Password/OTP Util", "SMTP/Twilio", "MySQL"],
        [
            (0, 1, "Gửi identifier + mật khẩu"),
            (1, 2, "login(identifier, matKhau)"),
            (2, 3, "verifyPassword PBKDF2"),
            (2, 5, "SELECT nguoidung, kiểm tra trạng thái"),
            (1, 0, "Tạo session user hoặc báo lỗi"),
            (0, 1, "Quên mật khẩu: gửi email/phone"),
            (1, 2, "findByIdentifier(), saveResetOtp()"),
            (1, 3, "Sinh OTP + thời hạn"),
            (3, 4, "Gửi OTP qua SMTP/Twilio hoặc debug fallback"),
            (0, 1, "Nhập OTP và mật khẩu mới"),
            (1, 2, "verifyResetOtp(), updatePassword()"),
        ],
    )


def draw_transaction_flow() -> Path:
    return draw_sequence(
        "Sequence: tạo và xác nhận giao dịch",
        "11_transaction_sequence.png",
        ["Buyer/Seller", "Tao/XacNhan Servlet", "GiaoDichXeDAO", "ThongBaoDAO", "MySQL", "Admin"],
        [
            (0, 1, "Tạo giao dịch từ yêu cầu đã chốt"),
            (1, 2, "createFromYeuCau(maYeuCau)"),
            (2, 4, "INSERT giaodichxe, cập nhật yeucaumuaxe"),
            (2, 3, "Thông báo buyer, seller, admin"),
            (0, 1, "Buyer xác nhận đã nhận xe"),
            (1, 2, "buyerConfirmReceived()"),
            (0, 1, "Seller xác nhận đã giao xe"),
            (1, 2, "sellerConfirmDelivered()"),
            (2, 4, "Nếu đủ 2 bên: HOAN_TAT, baidang=da_ban"),
            (5, 1, "Admin xử lý tranh chấp/hủy/cập nhật trạng thái"),
        ],
    )


def generate_diagrams() -> dict[str, Path]:
    return {
        "architecture": draw_architecture(),
        "mvc": draw_mvc(),
        "use_case": draw_use_case(),
        "erd": draw_erd(),
        "transaction_status": draw_status_diagram(),
        "deployment": draw_deployment(),
        "seller_sequence": draw_seller_flow(),
        "post_sequence": draw_post_flow(),
        "connection_sequence": draw_connection_flow(),
        "login_otp_sequence": draw_login_flow(),
        "transaction_sequence": draw_transaction_flow(),
    }


def parse_routes() -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    for file in sorted((ROOT / "src/java/controller").glob("*.java")):
        text = file.read_text(encoding="utf-8", errors="ignore")
        annotation_match = re.search(r"@WebServlet\((.*?)\)", text, flags=re.S)
        routes = []
        if annotation_match:
            routes = re.findall(r'"(/[^"]*)"', annotation_match.group(1))
        dispatchers = re.findall(r'getRequestDispatcher\("([^"]+)"\)', text)
        redirects = re.findall(r'sendRedirect\((.*?)\);', text)
        rows.append(
            {
                "class": file.stem,
                "routes": ", ".join(routes) if routes else "(không bắt được)",
                "views": ", ".join(sorted(set(dispatchers))) if dispatchers else "-",
                "redirects": str(len(redirects)),
            }
        )
    return rows


def parse_model_fields() -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    for file in sorted((ROOT / "src/java/model").glob("*.java")):
        text = file.read_text(encoding="utf-8", errors="ignore")
        fields = re.findall(r"private\s+([^;=]+?)\s+([A-Za-z_][A-Za-z0-9_]*)\s*;", text)
        brief = ", ".join([name for _, name in fields[:8]])
        if len(fields) > 8:
            brief += f", ... ({len(fields)} fields)"
        else:
            brief += f" ({len(fields)} fields)"
        rows.append({"model": file.stem, "fields": brief})
    return rows


def parse_dao_methods() -> list[dict[str, str]]:
    rows: list[dict[str, str]] = []
    for file in sorted((ROOT / "src/java/dao").glob("*.java")):
        text = file.read_text(encoding="utf-8", errors="ignore")
        methods = re.findall(r"public\s+[\w<>, ?\[\]]+\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(", text)
        brief = ", ".join(methods[:8])
        if len(methods) > 8:
            brief += f", ... ({len(methods)} methods)"
        else:
            brief += f" ({len(methods)} methods)"
        rows.append({"dao": file.stem, "methods": brief})
    return rows


def collect_project_facts() -> dict[str, object]:
    controllers = list((ROOT / "src/java/controller").glob("*.java"))
    daos = list((ROOT / "src/java/dao").glob("*.java"))
    models = list((ROOT / "src/java/model").glob("*.java"))
    jsps = list((ROOT / "web").glob("*.jsp"))
    libs = sorted([p.name for p in (ROOT / "lib").glob("*.jar")])
    migrations = sorted([p.name for p in (ROOT / "database").glob("*.sql")])
    return {
        "controllers": len(controllers),
        "daos": len(daos),
        "models": len(models),
        "jsps": len(jsps),
        "libs": libs,
        "migrations": migrations,
        "routes": parse_routes(),
        "model_fields": parse_model_fields(),
        "dao_methods": parse_dao_methods(),
    }


def set_cell_shading(cell, fill: str) -> None:
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def set_cell_text(cell, text: str, bold: bool = False) -> None:
    cell.text = ""
    p = cell.paragraphs[0]
    run = p.add_run(str(text))
    run.bold = bold
    run.font.name = "Times New Roman"
    run.font.size = Pt(10.5)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.TOP


def add_table(doc: Document, headers: list[str], rows: Iterable[Iterable[object]], widths: list[float] | None = None) -> None:
    table = doc.add_table(rows=1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    hdr = table.rows[0].cells
    for i, header in enumerate(headers):
        set_cell_text(hdr[i], header, True)
        set_cell_shading(hdr[i], "D9EAF7")
    for row in rows:
        cells = table.add_row().cells
        for i, value in enumerate(row):
            set_cell_text(cells[i], value)
    if widths:
        for row in table.rows:
            for idx, width in enumerate(widths):
                row.cells[idx].width = Inches(width)
    doc.add_paragraph()


def set_document_styles(doc: Document) -> None:
    styles = doc.styles
    styles["Normal"].font.name = "Times New Roman"
    styles["Normal"].font.size = Pt(12)
    for style_name, size, color in [
        ("Title", 26, "0F172A"),
        ("Heading 1", 18, "0F172A"),
        ("Heading 2", 15, "1E3A8A"),
        ("Heading 3", 13, "334155"),
    ]:
        style = styles[style_name]
        style.font.name = "Times New Roman"
        style.font.size = Pt(size)
        style.font.color.rgb = RGBColor.from_string(color)


def add_footer(doc: Document) -> None:
    for section in doc.sections:
        section.footer.paragraphs[0].text = "AutoPro - Báo cáo phân tích dự án JSP/Servlet"
        section.footer.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.CENTER


def add_caption(doc: Document, text: str) -> None:
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run(text)
    run.italic = True
    run.font.size = Pt(10)


def add_picture(doc: Document, path: Path, caption: str, width: float = 6.6) -> None:
    doc.add_picture(str(path), width=Inches(width))
    doc.paragraphs[-1].alignment = WD_ALIGN_PARAGRAPH.CENTER
    add_caption(doc, caption)


def add_bullets(doc: Document, items: Iterable[str]) -> None:
    for item in items:
        doc.add_paragraph(item, style="List Bullet")


def add_numbered(doc: Document, items: Iterable[str]) -> None:
    for item in items:
        doc.add_paragraph(item, style="List Number")


def add_paragraphs(doc: Document, paragraphs: Iterable[str]) -> None:
    for text in paragraphs:
        doc.add_paragraph(text)


def build_report(diagrams: dict[str, Path], facts: dict[str, object]) -> None:
    doc = Document()
    set_document_styles(doc)
    section = doc.sections[0]
    section.top_margin = Inches(0.7)
    section.bottom_margin = Inches(0.65)
    section.left_margin = Inches(0.75)
    section.right_margin = Inches(0.75)
    section.page_width = Inches(8.27)
    section.page_height = Inches(11.69)

    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    title_run = title.add_run("BÁO CÁO CHI TIẾT DỰ ÁN AUTOPRO")
    title_run.bold = True
    title_run.font.size = Pt(25)
    title_run.font.name = "Times New Roman"
    doc.add_paragraph()
    subtitle = doc.add_paragraph()
    subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
    subtitle.add_run("Ứng dụng web mua bán xe dùng Java JSP/Servlet, Tomcat và MySQL").bold = True
    doc.add_paragraph()
    info = [
        ("Tên dự án", "xetot / AutoPro"),
        ("Đường dẫn phân tích", str(ROOT)),
        ("Ngày lập báo cáo", date.today().strftime("%d/%m/%Y")),
        ("Hình thức báo cáo", "Phân tích từ mã nguồn, cấu trúc thư mục, cấu hình build và các migration SQL"),
    ]
    add_table(doc, ["Mục", "Thông tin"], info, [2.0, 4.5])
    doc.add_paragraph("Báo cáo này mô tả kiến trúc, công nghệ, chức năng nghiệp vụ, cơ sở dữ liệu, các sơ đồ UML/logic và đề xuất hoàn thiện cho dự án AutoPro. Nội dung được tổng hợp trực tiếp từ mã nguồn hiện có trong các thư mục src/java, web, lib, database và nbproject.")
    doc.add_page_break()

    doc.add_heading("Mục lục tóm tắt", level=1)
    toc_items = [
        "1. Tổng quan dự án và phạm vi nghiệp vụ",
        "2. Công nghệ sử dụng và tác dụng từng thành phần",
        "3. Kiến trúc tổng thể, MVC và triển khai",
        "4. Tác nhân, Use Case và đặc tả chức năng",
        "5. Cơ sở dữ liệu và ERD logic",
        "6. Luồng xử lý chính: đăng nhập, đăng bài, seller, chat, giao dịch",
        "7. Module admin, bảo mật, cấu hình, build/deploy",
        "8. Kiểm thử, rủi ro và hướng phát triển",
        "9. Phụ lục route/controller, DAO và model",
    ]
    add_numbered(doc, toc_items)
    doc.add_paragraph(
        f"Quy mô mã nguồn hiện tại: {facts['controllers']} servlet controller, {facts['daos']} DAO, "
        f"{facts['models']} model, {facts['jsps']} trang JSP, thư viện JAR nội bộ gồm {', '.join(facts['libs'])}."
    )
    doc.add_page_break()

    doc.add_heading("1. Tổng quan dự án", level=1)
    add_paragraphs(
        doc,
        [
            "AutoPro là hệ thống web hỗ trợ đăng bán, tìm kiếm, trao đổi và quản lý giao dịch xe. Dự án được tổ chức theo phong cách Java Web truyền thống: trình duyệt gửi request đến Servlet, Servlet gọi DAO để truy vấn MySQL, sau đó forward dữ liệu sang JSP để render giao diện.",
            "Ứng dụng hướng đến ba nhóm người dùng chính: khách xem xe, thành viên/người mua, người bán đã được duyệt và quản trị viên. Các nghiệp vụ nổi bật gồm tìm kiếm xe, xem chi tiết xe, lưu bài, gửi yêu cầu kết nối, chat, quản lý lịch hẹn, đăng ký seller, đăng bài xe, duyệt bài, xử lý báo cáo và xác nhận giao dịch mua bán.",
            "Dự án không dùng framework backend lớn như Spring Boot, cũng không dùng frontend framework như React/Vue. Điều này làm cấu trúc dễ triển khai trên Tomcat, nhưng cũng khiến trách nhiệm xử lý request, validation, bảo mật và phân quyền nằm khá trực tiếp trong từng Servlet/DAO.",
        ],
    )
    add_table(
        doc,
        ["Nhóm chức năng", "Mô tả", "File/thành phần tiêu biểu"],
        [
            ("Trang chủ và tìm kiếm", "Hiển thị danh sách xe, lọc theo hãng, tỉnh, năm, giá, nhiên liệu, hộp số, kiểu dáng.", "HomeServlet, SearchServlet, FilterServlet, index.jsp, XeDAO"),
            ("Tài khoản", "Đăng ký, đăng nhập, đăng xuất, cập nhật hồ sơ, avatar, đổi/khôi phục mật khẩu bằng OTP.", "RegisterServlet, LoginServlet, ProfileServlet, ForgotPasswordServlet, VerifyOtpServlet, ResetPasswordServlet"),
            ("Bài đăng xe", "Seller đăng bài, upload nhiều ảnh, sửa/xóa bài, admin duyệt/từ chối/cảnh báo.", "AddPostServlet, EditPostServlet, MyPostServlet, AdminPostServlet, BaiDangDAO, XeDAO"),
            ("Seller", "Người dùng gửi hồ sơ seller, upload ảnh CCCD, admin duyệt hoặc từ chối.", "DangKySellerServlet, AdminDanhSachSellerServlet, AdminPheDuyetSellerServlet, DangKySellerDAO"),
            ("Tương tác mua bán", "Người mua gửi yêu cầu kết nối, tạo hội thoại, chat, lịch hẹn và giao dịch.", "YeuCauKetNoiServlet, ChatServlet, LichHenCuaToiServlet, TaoGiaoDichServlet, XacNhanGiaoDichServlet"),
            ("Quản trị", "Dashboard, quản lý user, xe, danh mục, báo cáo, lịch hẹn, seller và giao dịch.", "AdminDashboardServlet, AdminUserServlet, AdminCarServlet, AdminCategoryServlet, AdminReportServlet, AdminQuanLyGiaoDichServlet"),
        ],
        [1.55, 3.4, 2.2],
    )
    doc.add_page_break()

    doc.add_heading("2. Công nghệ sử dụng", level=1)
    add_table(
        doc,
        ["Công nghệ", "Vai trò trong dự án", "Ghi chú từ mã nguồn"],
        [
            ("Java 17", "Ngôn ngữ backend chính, viết Servlet, DAO, Model, Util.", "javac.source=17, javac.target=17 trong nbproject/project.properties."),
            ("Jakarta Servlet", "Nhận request, đọc tham số form, quản lý session, forward/redirect, upload file.", "Các controller dùng @WebServlet, HttpServlet, @MultipartConfig."),
            ("JSP", "Render giao diện HTML phía server.", "Các trang nằm trong web/*.jsp."),
            ("JSTL 2.0", "Lặp danh sách, điều kiện, format số/ngày trong JSP.", "jakarta.servlet.jsp.jstl-api-2.0.0.jar và jakarta.servlet.jsp.jstl-2.0.0.jar."),
            ("Apache Tomcat", "Application server chạy WAR tại context /xetot.", "j2ee.server.type=Tomcat, context.xml."),
            ("Ant/NetBeans", "Build, copy thư viện, đóng gói xetot.war.", "build.xml import nbproject/build-impl.xml."),
            ("MySQL", "Lưu dữ liệu nghiệp vụ: user, xe, bài đăng, chat, giao dịch.", "DBContext trỏ tới jdbc:mysql://localhost:3306/car_market."),
            ("JDBC", "Truy vấn CSDL trực tiếp bằng SQL và PreparedStatement.", "DAO extends DBContext, không dùng ORM."),
            ("Gson", "Chuyển dữ liệu Java sang JSON cho chat/dashboard.", "gson-2.10.1.jar; AdminDashboardServlet, ChatMessagesServlet."),
            ("Jakarta Mail", "Gửi email OTP/thông báo qua SMTP.", "EmailDeliveryService, OtpDeliveryService."),
            ("Java HttpClient + Twilio", "Gửi OTP SMS khi cấu hình Twilio.", "OtpDeliveryService gọi Twilio REST API."),
            ("PBKDF2WithHmacSHA256", "Hash và verify mật khẩu.", "PasswordUtil dùng salt 16 bytes, hash 32 bytes, 120000 iterations."),
            ("Bootstrap 5", "Layout, form, button, grid responsive.", "CDN trong nhiều JSP."),
            ("Font Awesome", "Icon cho giao diện.", "CDN trong nhiều JSP."),
            ("Chart.js", "Vẽ biểu đồ dashboard admin.", "admin_dashboard.jsp."),
            ("JavaScript Fetch API", "Gọi API chat-messages/chat không reload trang.", "chat.jsp dùng fetch()."),
        ],
        [1.75, 3.25, 2.2],
    )
    add_paragraphs(
        doc,
        [
            "Điểm mạnh của stack hiện tại là dễ triển khai trên môi trường học tập hoặc server Tomcat đơn giản: chỉ cần JDK, Tomcat, MySQL và thư viện JAR trong WEB-INF/lib. Điểm cần chú ý là dự án chưa có dependency manager như Maven/Gradle, nên việc nâng cấp thư viện phải thao tác thủ công trong thư mục lib.",
            "Phần frontend chủ yếu là JSP kèm CSS/JS inline trong từng trang. Cách này phù hợp cho ứng dụng server-rendered, nhưng về lâu dài nên gom CSS/JS dùng chung để giảm trùng lặp và dễ bảo trì.",
        ],
    )
    doc.add_page_break()

    doc.add_heading("3. Sơ đồ kiến trúc tổng thể", level=1)
    add_picture(doc, diagrams["architecture"], "Hình 1. Kiến trúc tổng thể AutoPro", 6.7)
    add_table(
        doc,
        ["Thành phần", "Chức năng", "Chi tiết"],
        [
            ("Browser", "Gửi HTTP request, nhận HTML/CSS/JS.", "Người dùng thao tác qua các JSP đã render."),
            ("Tomcat", "Chạy servlet container.", "Nhận request tại context /xetot và nạp xetot.war."),
            ("JSP/JSTL", "View layer.", "Nhận attribute từ request/session và hiển thị giao diện."),
            ("Servlet Controller", "Controller layer.", "Xử lý route, validation, phân quyền, forward/redirect."),
            ("DAO", "Data access layer.", "Gom SQL, mapping ResultSet thành Model."),
            ("MySQL", "Database.", "Dữ liệu nghiệp vụ và trạng thái giao dịch."),
            ("Util", "Dịch vụ dùng chung.", "Hash mật khẩu, OTP, email, URL helper, chuẩn hóa field xe."),
            ("External services", "SMTP/Twilio.", "Gửi OTP email/SMS nếu biến môi trường đã cấu hình."),
            ("Filesystem", "Lưu file upload.", "Ảnh xe, avatar, CCCD seller trong web/uploads."),
        ],
        [1.6, 2.35, 3.25],
    )
    doc.add_paragraph("Sơ đồ cho thấy ứng dụng có kiến trúc ba lớp rõ ràng: trình bày, xử lý nghiệp vụ và truy cập dữ liệu. Các Servlet là điểm vào chính của request; DAO là nơi tập trung SQL; JSP chỉ nên giữ nhiệm vụ hiển thị. Những kết nối ra ngoài như SMTP/Twilio được gom vào lớp util để giảm phụ thuộc trực tiếp trong controller.")
    doc.add_page_break()

    doc.add_heading("4. Sơ đồ MVC và tổ chức package", level=1)
    add_picture(doc, diagrams["mvc"], "Hình 2. Mô hình MVC/package", 6.7)
    add_table(
        doc,
        ["Package/thư mục", "Vai trò", "Ví dụ"],
        [
            ("src/java/controller", "Controller nhận request và điều phối luồng.", "LoginServlet, AddPostServlet, AdminDashboardServlet."),
            ("src/java/dao", "Truy cập CSDL, chứa SQL và mapping.", "NguoiDungDAO, XeDAO, BaiDangDAO, GiaoDichXeDAO."),
            ("src/java/model", "Đối tượng dữ liệu dùng giữa DAO, Servlet, JSP.", "NguoiDung, Xe, BaiDang, DangKySeller."),
            ("src/java/context", "Cấu hình kết nối CSDL.", "DBContext."),
            ("src/java/util", "Hàm/dịch vụ dùng chung.", "PasswordUtil, OtpDeliveryService, EmailDeliveryService."),
            ("web", "View JSP, asset, upload, WEB-INF/lib.", "index.jsp, detail.jsp, admin_dashboard.jsp."),
            ("database", "Migration SQL bổ sung schema.", "dangkyseller_migration.sql, giaodichxe_migration.sql."),
            ("nbproject", "Cấu hình NetBeans/Ant.", "project.properties, project.xml."),
        ],
        [1.7, 3.0, 2.4],
    )
    doc.add_paragraph("Với mô hình này, khi thêm một nghiệp vụ mới nên đi theo chuỗi: tạo/cập nhật Model nếu cần, thêm DAO cho truy vấn, thêm Servlet để điều phối, rồi tạo JSP để hiển thị. Tránh đặt SQL trong JSP hoặc đặt logic render quá nhiều trong Servlet.")
    doc.add_page_break()

    doc.add_heading("5. Tác nhân và Use Case", level=1)
    add_picture(doc, diagrams["use_case"], "Hình 3. Use Case tổng quan", 6.7)
    add_table(
        doc,
        ["Tác nhân", "Mục tiêu", "Use case chính"],
        [
            ("Khách xem", "Tìm hiểu xe trước khi có tài khoản.", "Xem danh sách xe, tìm kiếm/lọc xe, xem chi tiết xe, đăng ký/đăng nhập."),
            ("Thành viên/người mua", "Tương tác mua xe và quản lý hồ sơ cá nhân.", "Lưu bài, gửi yêu cầu kết nối, chat, lịch hẹn, xác nhận giao dịch, quên mật khẩu OTP."),
            ("Seller", "Đăng và quản lý xe bán.", "Đăng ký seller, đăng/sửa/xóa bài xe, xem seller dashboard, chat, xác nhận giao dịch."),
            ("Admin", "Kiểm duyệt và vận hành hệ thống.", "Duyệt seller, duyệt bài, quản lý user/danh mục, xử lý báo cáo, quản lý giao dịch, xem dashboard."),
        ],
        [1.55, 2.5, 3.2],
    )
    add_paragraphs(
        doc,
        [
            "Use Case tổng quan thể hiện biên giới chức năng của hệ thống. Các chức năng công khai như xem xe/tìm kiếm không bắt buộc đăng nhập, còn các chức năng tạo dữ liệu như lưu bài, yêu cầu kết nối, chat, đăng bài và xác nhận giao dịch đều cần session user.",
            "Seller là vai trò nâng cấp từ thành viên. Người dùng phải gửi hồ sơ seller, admin duyệt hồ sơ thì hệ thống mới cập nhật vaiTro thành seller. Admin có quyền quản trị rộng hơn, nhưng từng servlet vẫn cần kiểm tra vaiTro để tránh truy cập trái phép.",
        ],
    )
    doc.add_page_break()

    doc.add_heading("6. Đặc tả Use Case tiêu biểu", level=1)
    add_table(
        doc,
        ["Use Case", "Tiền điều kiện", "Luồng chính", "Kết quả"],
        [
            ("Đăng nhập", "Người dùng đã có tài khoản.", "Nhập email/số điện thoại và mật khẩu; DAO lấy user; PasswordUtil verify; tạo session.", "Vào trang home hoặc admin dashboard tùy vai trò."),
            ("Tìm kiếm/lọc xe", "Không bắt buộc đăng nhập.", "Nhập keyword/bộ lọc; Servlet đọc tham số; XeDAO tạo SQL filter; forward index.jsp.", "Danh sách xe phù hợp được hiển thị."),
            ("Đăng bài xe", "User là seller hợp lệ.", "Mở form; nhập thông tin xe; upload ảnh; Insert xe và baidang trạng thái chờ duyệt.", "Bài đăng chờ admin duyệt."),
            ("Gửi yêu cầu kết nối", "User đăng nhập, không phải chủ bài.", "Kiểm tra xe, yêu cầu trùng, xe đã bán; tạo yêu cầu, lịch hẹn và hội thoại.", "Buyer và seller có luồng tương tác mới."),
            ("Chat", "User là participant của hội thoại.", "Mở chat; fetch tin nhắn; gửi tin nhắn POST; DAO insert tinnhan.", "Tin nhắn được lưu và trả về qua JSON."),
            ("Đăng ký seller", "User đăng nhập.", "Nhập hồ sơ, upload CCCD; DAO tạo dangkyseller; thông báo admin.", "Hồ sơ ở trạng thái CHO_DUYET."),
            ("Duyệt seller", "Admin đăng nhập.", "Admin xem hồ sơ; approve/reject; DAO update trạng thái và thông báo user.", "User có thể trở thành seller nếu duyệt."),
            ("Xác nhận giao dịch", "Có hồ sơ giao dịch từ yêu cầu.", "Buyer xác nhận nhận xe; seller xác nhận giao xe; DAO đồng bộ trạng thái.", "HOAN_TAT khi đủ hai bên, bài đăng chuyển da_ban."),
        ],
        [1.55, 1.65, 3.1, 1.85],
    )
    doc.add_paragraph("Các use case trên là các điểm giao thoa quan trọng giữa giao diện, controller, DAO và database. Khi kiểm thử, nên ưu tiên các luồng này vì chúng ảnh hưởng trực tiếp tới dữ liệu và trạng thái nghiệp vụ.")
    doc.add_page_break()

    doc.add_heading("7. Cơ sở dữ liệu và ERD", level=1)
    add_picture(doc, diagrams["erd"], "Hình 4. ERD logic từ mã nguồn và migration", 6.7)
    doc.add_paragraph("Repository hiện có hai migration chính cho dangkyseller và giaodichxe. Các bảng còn lại được suy luận từ DAO và câu SQL trong mã nguồn. Vì không thấy file schema đầy đủ cho toàn bộ database, ERD trong báo cáo là ERD logic phục vụ phân tích và tài liệu hóa.")
    add_table(
        doc,
        ["Bảng", "Vai trò nghiệp vụ", "Quan hệ chính"],
        [
            ("nguoidung", "Tài khoản, vai trò, trạng thái, OTP, avatar.", "1-n với baidang, dangkyseller, thongbao, hoithoai, tinnhan, giaodichxe."),
            ("xe", "Thông tin chi tiết xe.", "n-1 hangxe/tinhthanh; 1-n baidang, xe_hinhanh, hoithoai, yeucaumuaxe, giaodichxe."),
            ("baidang", "Bài đăng bán xe.", "n-1 nguoidung/xe/tinhthanh; 1-n baocao/savedpost."),
            ("dangkyseller", "Hồ sơ xét duyệt seller.", "n-1 nguoidung, tinhthanh, admin duyệt."),
            ("yeucaumuaxe", "Yêu cầu kết nối buyer-seller-xe.", "Liên kết buyer, seller, xe, hội thoại; nguồn tạo lịch hẹn/giao dịch."),
            ("hoithoai/tinnhan", "Chat giữa buyer và seller.", "Hội thoại gắn buyer, seller, xe; tin nhắn gắn hội thoại."),
            ("lichhenxemxe", "Lịch hẹn xem xe phát sinh từ yêu cầu.", "n-1 yeucaumuaxe."),
            ("giaodichxe", "Hồ sơ giao dịch và xác nhận hai bên.", "1-1 yeucaumuaxe; n-1 buyer/seller/xe/admin."),
            ("baocao", "Báo cáo bài đăng vi phạm.", "n-1 baidang, người báo cáo, admin xử lý."),
            ("thongbao", "Thông báo trong hệ thống.", "n-1 nguoidung."),
        ],
        [1.45, 3.0, 2.7],
    )
    doc.add_page_break()

    doc.add_heading("8. Data Dictionary rút gọn", level=1)
    add_table(
        doc,
        ["Entity/Model", "Trường quan trọng", "Ý nghĩa"],
        [
            ("NguoiDung", "maNguoiDung, hoTen, email, soDienThoai, matKhau, vaiTro, trangThai, otpReset, otpHetHan", "Định danh và phân quyền user; hỗ trợ khôi phục mật khẩu."),
            ("Xe", "maXe, maHang, tenXe, gia, kieuDang, tinhTrang, nhienLieu, soCho, hopSo, namSanXuat, maTinh", "Thông tin kỹ thuật và thương mại của xe."),
            ("BaiDang", "maBaiDang, maNguoiDung, maXe, maTinh, tieuDe, moTa, trangThai, ghiChuAdmin, luotXem", "Nội dung bài bán xe và trạng thái kiểm duyệt."),
            ("DangKySeller", "maDangKySeller, maNguoiDung, soCCCD, anhCCCDMatTruoc/Sau, trangThai, lyDoTuChoi", "Hồ sơ xét duyệt người bán."),
            ("YeuCauKetNoi", "maYeuCau, maNguoiMua, maNguoiBan, maXe, maHoiThoai, giaThamKhao, trangThai", "Yêu cầu mua/kết nối giữa buyer và seller."),
            ("TinNhan", "maTinNhan, maHoiThoai, maNguoiGui, noiDung, thoiGian, trangThai", "Tin nhắn trong hội thoại."),
            ("GiaoDichXe", "maGiaoDich, maYeuCau, maNguoiMua, maNguoiBan, maXe, xacNhanNguoiMua, xacNhanNguoiBan, trangThai", "Giao dịch chính thức và xác nhận giao nhận."),
            ("ThongBao", "maThongBao, maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc", "Thông báo định tuyến người dùng tới trang xử lý."),
        ],
        [1.45, 3.5, 2.3],
    )
    add_table(
        doc,
        ["Trạng thái", "Đối tượng", "Ý nghĩa"],
        [
            ("cho_duyet", "baidang", "Bài mới tạo, đang chờ admin duyệt."),
            ("da_duyet", "baidang", "Bài hợp lệ và được hiển thị công khai."),
            ("tu_choi/canh_bao", "baidang", "Bài không đạt yêu cầu hoặc cần seller chỉnh sửa."),
            ("da_ban", "baidang", "Xe đã hoàn tất giao dịch."),
            ("CHO_DUYET/DA_DUYET/TU_CHOI/DA_XOA", "dangkyseller", "Vòng đời hồ sơ seller."),
            ("MOI_TAO/CHO_XAC_NHAN_HAI_BEN/CHO_XAC_NHAN_CON_LAI/HOAN_TAT/HUY/TRANH_CHAP", "giaodichxe", "Vòng đời giao dịch xe."),
        ],
        [2.0, 1.6, 3.6],
    )
    doc.add_page_break()

    doc.add_heading("9. Luồng đăng nhập và OTP", level=1)
    add_picture(doc, diagrams["login_otp_sequence"], "Hình 5. Sequence đăng nhập và khôi phục mật khẩu", 6.7)
    doc.add_paragraph("Luồng đăng nhập dùng NguoiDungDAO để tìm tài khoản theo email hoặc số điện thoại, sau đó PasswordUtil kiểm tra mật khẩu. PasswordUtil có cơ chế tương thích: nếu mật khẩu cũ chưa hash thì so sánh trực tiếp bằng MessageDigest.isEqual, còn mật khẩu mới dùng PBKDF2WithHmacSHA256.")
    add_table(
        doc,
        ["Bước", "Xử lý", "Lưu ý kỹ thuật"],
        [
            ("1", "User gửi identifier và mật khẩu tới LoginServlet.", "identifier có thể là email hoặc số điện thoại."),
            ("2", "NguoiDungDAO.login đọc record trong nguoidung.", "Cần kiểm tra trạng thái tài khoản."),
            ("3", "PasswordUtil.verifyPassword xác thực.", "PBKDF2 + salt giúp giảm rủi ro lộ mật khẩu."),
            ("4", "Servlet tạo session user.", "Admin được redirect tới /admin/dashboard."),
            ("5", "Quên mật khẩu sinh OTP và thời hạn.", "OTP lưu trong nguoidung.otpReset/otpHetHan."),
            ("6", "OtpDeliveryService gửi OTP.", "SMTP/Twilio nếu cấu hình; fallback debug khi chưa cấu hình."),
        ],
        [0.7, 3.25, 3.15],
    )
    doc.add_page_break()

    doc.add_heading("10. Luồng đăng bài bán xe", level=1)
    add_picture(doc, diagrams["post_sequence"], "Hình 6. Sequence seller đăng bài", 6.7)
    doc.add_paragraph("Seller đăng bài qua AddPostServlet/EditPostServlet. Các servlet này dùng @MultipartConfig để nhận nhiều ảnh xe, lưu file vào thư mục uploads và ghi đường dẫn ảnh vào bảng xe_hinhanh. Dữ liệu xe được lưu trước, sau đó bài đăng được tạo với trạng thái chờ duyệt.")
    add_table(
        doc,
        ["Khối xử lý", "Trách nhiệm", "Rủi ro/cần kiểm soát"],
        [
            ("Form JSP", "Thu thập thông tin xe, ảnh, tỉnh, hãng xe.", "Cần giới hạn kích thước ảnh và định dạng file."),
            ("AddPostServlet", "Validate session seller, parse form, lưu ảnh.", "Tránh tin tưởng dữ liệu client; kiểm tra quyền seller."),
            ("XeDAO", "Insert/update bảng xe và xe_hinhanh.", "Dùng transaction khi insert xe + nhiều ảnh."),
            ("BaiDangDAO", "Tạo/cập nhật bài đăng.", "Trạng thái ban đầu nên là cho_duyet."),
            ("AdminPostServlet", "Kiểm duyệt bài.", "Cần ghi chú admin rõ khi từ chối/cảnh báo."),
        ],
        [1.55, 3.0, 2.6],
    )
    doc.add_page_break()

    doc.add_heading("11. Luồng đăng ký và duyệt seller", level=1)
    add_picture(doc, diagrams["seller_sequence"], "Hình 7. Sequence đăng ký seller", 6.7)
    doc.add_paragraph("Seller registration là nghiệp vụ quan trọng vì thay đổi quyền của user. Người dùng gửi thông tin định danh và ảnh CCCD; hệ thống lưu hồ sơ ở trạng thái CHO_DUYET và thông báo admin. Khi admin duyệt, DAO cập nhật dangkyseller và đổi vaiTro của nguoidung thành seller.")
    add_table(
        doc,
        ["Quy tắc", "Mô tả"],
        [
            ("Không tạo seller tự động", "Vai trò seller chỉ được cập nhật sau khi admin duyệt."),
            ("Không trùng CCCD/email/phone", "DangKySellerDAO kiểm tra trùng dữ liệu với người khác."),
            ("Xóa mềm hồ sơ", "Migration ghi chú dùng trangThai = DA_XOA thay vì xóa cứng."),
            ("Thông báo", "Khi tạo/duyệt/từ chối hồ sơ, hệ thống tạo thông báo cho admin hoặc user."),
            ("Ảnh xác minh", "Ảnh CCCD mặt trước/mặt sau được upload vào web/uploads/seller-verify."),
        ],
        [2.0, 5.2],
    )
    doc.add_page_break()

    doc.add_heading("12. Luồng kết nối, chat và lịch hẹn", level=1)
    add_picture(doc, diagrams["connection_sequence"], "Hình 8. Sequence yêu cầu kết nối, chat và lịch hẹn", 6.7)
    doc.add_paragraph("Khi buyer quan tâm một xe, YeuCauKetNoiServlet kiểm tra điều kiện trước khi tạo yêu cầu: phải đăng nhập, không phải chủ bài, tài khoản không bị hạn chế, chưa có yêu cầu mở cho xe đó và xe chưa có giao dịch hoàn tất. DAO tạo yeucaumuaxe, đảm bảo có lichhenxemxe và liên kết hội thoại nếu có.")
    add_table(
        doc,
        ["Thành phần", "Chi tiết xử lý"],
        [
            ("YeuCauKetNoiServlet", "Đọc maXe, giá tham khảo, ghi chú; kiểm tra quyền và nghiệp vụ."),
            ("YeuCauKetNoiDAO", "Xác định seller từ bài đăng, tạo yêu cầu mua xe, lịch hẹn và trạng thái ban đầu."),
            ("HoiThoaiDAO", "Tìm hoặc tạo hội thoại giữa buyer, seller và xe."),
            ("ChatServlet/ChatMessagesServlet", "Kiểm tra participant trước khi trả JSON tin nhắn."),
            ("TinNhanDAO", "Lưu và đọc tin nhắn theo maHoiThoai."),
            ("LichHenCuaToiServlet", "Hiển thị danh sách yêu cầu/lịch hẹn theo vai trò buyer/seller."),
        ],
        [1.95, 5.2],
    )
    doc.add_page_break()

    doc.add_heading("13. Luồng giao dịch và trạng thái", level=1)
    add_picture(doc, diagrams["transaction_sequence"], "Hình 9. Sequence tạo/xác nhận giao dịch", 6.7)
    add_picture(doc, diagrams["transaction_status"], "Hình 10. Sơ đồ trạng thái giao dịch", 6.7)
    doc.add_paragraph("Giao dịch được tạo từ yêu cầu kết nối đã đủ điều kiện. GiaoDichXeDAO xử lý nhiều bước trong transaction database: tạo record giao dịch, cập nhật trạng thái yêu cầu, gửi thông báo và đồng bộ trạng thái khi buyer/seller xác nhận.")
    add_table(
        doc,
        ["Trạng thái", "Khi nào xảy ra", "Hệ quả"],
        [
            ("MOI_TAO", "Hồ sơ giao dịch vừa được tạo.", "Buyer/seller bắt đầu kiểm tra thông tin."),
            ("CHO_XAC_NHAN_HAI_BEN", "Đang chờ cả buyer và seller xác nhận.", "Chưa thể đánh dấu hoàn tất."),
            ("CHO_XAC_NHAN_CON_LAI", "Một bên đã xác nhận.", "Thông báo bên còn lại vào xác nhận."),
            ("HOAN_TAT", "Cả hai bên xác nhận hoặc admin cập nhật hoàn tất.", "Bài đăng chuyển da_ban, yêu cầu khác bị đóng/hủy."),
            ("HUY", "Một bên hoặc admin hủy trước khi hoàn tất.", "Yêu cầu mua liên quan chuyển trạng thái hủy."),
            ("TRANH_CHAP", "Admin đánh dấu có tranh chấp.", "Cần xử lý thủ công."),
        ],
        [1.75, 3.1, 2.35],
    )
    doc.add_page_break()

    doc.add_heading("14. Module Admin", level=1)
    add_table(
        doc,
        ["Module", "Servlet/JSP", "Chức năng chính"],
        [
            ("Dashboard", "AdminDashboardServlet, admin_dashboard.jsp", "Thống kê user, xe, bài chờ duyệt, báo cáo, yêu cầu, giao dịch; xuất JSON cho Chart.js."),
            ("Quản lý bài đăng", "AdminPostServlet, AdminPostDetailServlet", "Lọc bài, duyệt, từ chối, cảnh báo, xem báo cáo liên quan."),
            ("Quản lý user", "AdminUserServlet, AdminUserActionServlet", "Xem danh sách user, cập nhật trạng thái/vai trò, xóa user."),
            ("Quản lý seller", "AdminDanhSachSellerServlet, AdminChiTietSellerServlet", "Xem hồ sơ seller, duyệt, từ chối, xóa mềm."),
            ("Quản lý xe", "AdminCarServlet, AdminCarActionServlet", "Xem/xóa xe ở góc admin."),
            ("Danh mục", "AdminCategoryServlet", "Thêm/sửa/xóa hãng xe và tỉnh thành."),
            ("Báo cáo", "AdminReportServlet", "Xử lý báo cáo bài đăng, cập nhật trạng thái báo cáo."),
            ("Lịch hẹn", "AdminLichHenServlet, AdminCapNhatLichHenServlet", "Theo dõi và cập nhật lịch hẹn xem xe."),
            ("Giao dịch", "AdminQuanLyGiaoDichServlet", "Lọc giao dịch, xem chi tiết, cập nhật trạng thái, xử lý tranh chấp."),
        ],
        [1.5, 2.6, 3.2],
    )
    doc.add_paragraph("Module admin có phạm vi rộng, vì vậy kiểm tra phân quyền phải được đặt ở đầu mỗi servlet. Một số servlet redirect về /home khi không đủ quyền, một số redirect về /login; nên thống nhất trải nghiệm và cách xử lý lỗi 403 để hệ thống dễ bảo trì hơn.")
    doc.add_page_break()

    doc.add_heading("15. Bảo mật và kiểm soát dữ liệu", level=1)
    add_table(
        doc,
        ["Hạng mục", "Hiện trạng", "Khuyến nghị"],
        [
            ("Mật khẩu", "Đã có PBKDF2WithHmacSHA256, salt, constant-time compare.", "Khi user login bằng mật khẩu cũ dạng plain text, nên rehash tự động sau khi xác thực thành công."),
            ("Session", "Dùng session attribute user để phân quyền.", "Tách helper kiểm tra vai trò để tránh lặp và sót check."),
            ("SQL Injection", "DAO dùng PreparedStatement ở phần lớn truy vấn.", "Duy trì nguyên tắc không nối trực tiếp input vào SQL."),
            ("Upload file", "Có @MultipartConfig và lưu ảnh.", "Kiểm tra MIME thật, extension whitelist, đổi tên file, hạn chế thực thi file upload."),
            ("OTP", "Có thời hạn otpHetHan, gửi SMTP/Twilio hoặc debug fallback.", "Tắt debug OTP ở production, giới hạn số lần nhập sai."),
            ("Phân quyền admin/seller", "Kiểm tra rải rác trong servlet.", "Thêm Filter hoặc base helper để chuẩn hóa kiểm tra quyền."),
            ("Thông báo", "Thông báo có urlDich để điều hướng.", "Validate URL nội bộ để tránh open redirect."),
            ("Dữ liệu nhạy cảm", "Ảnh CCCD lưu trong web/uploads.", "Cân nhắc lưu ngoài webroot hoặc kiểm soát truy cập ảnh xác minh."),
        ],
        [1.6, 2.8, 2.8],
    )
    doc.add_paragraph("Nhìn chung, dự án đã có nền tảng bảo mật cơ bản tốt hơn nhiều ứng dụng JSP/Servlet học thuật: dùng PreparedStatement, hash mật khẩu mạnh và session-based authorization. Phần cần gia cố nhiều nhất là upload file, phân quyền tập trung và bảo vệ dữ liệu định danh seller.")
    doc.add_page_break()

    doc.add_heading("16. Sơ đồ triển khai và cấu hình", level=1)
    add_picture(doc, diagrams["deployment"], "Hình 11. Sơ đồ triển khai AutoPro", 6.7)
    add_table(
        doc,
        ["Cấu hình", "Biến/tệp", "Tác dụng"],
        [
            ("Database URL", "AUTOPRO_DB_URL", "Ghi đè URL MySQL mặc định jdbc:mysql://localhost:3306/car_market."),
            ("Database user/password", "AUTOPRO_DB_USER, AUTOPRO_DB_PASSWORD", "Tài khoản kết nối MySQL."),
            ("SMTP", "AUTOPRO_SMTP_HOST, PORT, USERNAME, PASSWORD, FROM", "Gửi OTP/email bằng Jakarta Mail."),
            ("Twilio", "AUTOPRO_SMS_PROVIDER, AUTOPRO_TWILIO_*", "Gửi OTP SMS qua Twilio."),
            ("Context", "web/META-INF/context.xml", "Định nghĩa context path /xetot."),
            ("WAR", "dist/xetot.war", "Gói deploy lên Tomcat."),
            ("Build", "build.xml, nbproject/project.properties", "Ant/NetBeans build, copy thư viện vào WEB-INF/lib."),
        ],
        [1.5, 2.7, 3.0],
    )
    add_numbered(
        doc,
        [
            "Cài JDK 17, Apache Tomcat tương thích Jakarta và MySQL.",
            "Tạo database car_market và chạy schema/migration cần thiết.",
            "Cấu hình biến môi trường AUTOPRO_DB_* và các biến SMTP/Twilio nếu dùng OTP thật.",
            "Build bằng NetBeans/Ant để tạo dist/xetot.war.",
            "Deploy WAR vào Tomcat, truy cập context /xetot.",
        ],
    )
    doc.add_page_break()

    doc.add_heading("17. Kiểm thử đề xuất", level=1)
    add_table(
        doc,
        ["Nhóm test", "Kịch bản cần kiểm thử", "Mức ưu tiên"],
        [
            ("Auth", "Đăng ký, login đúng/sai, tài khoản bị khóa, logout, reset password OTP.", "Cao"),
            ("Search", "Filter theo keyword, hãng, tỉnh, giá, năm, nhiên liệu, hộp số, số chỗ.", "Trung bình"),
            ("Seller", "Gửi hồ sơ thiếu ảnh, trùng CCCD, admin duyệt/từ chối/xóa mềm.", "Cao"),
            ("Post", "Đăng bài với nhiều ảnh, edit bài, xóa bài, admin duyệt/từ chối.", "Cao"),
            ("Chat", "Chỉ participant đọc/gửi tin; user ngoài hội thoại bị chặn.", "Cao"),
            ("Appointment", "Buyer/seller/admin xem và cập nhật lịch hẹn đúng quyền.", "Trung bình"),
            ("Transaction", "Tạo giao dịch, buyer xác nhận, seller xác nhận, hủy, tranh chấp, hoàn tất.", "Rất cao"),
            ("Admin", "Quản lý user, báo cáo, danh mục, dashboard với dữ liệu rỗng và dữ liệu lớn.", "Cao"),
            ("Security", "SQL injection input, upload file sai định dạng, truy cập URL admin khi không đăng nhập.", "Rất cao"),
        ],
        [1.55, 4.4, 1.2],
    )
    doc.add_paragraph("Hiện repo có thư mục test nhưng không thấy test source đáng kể. Nếu tiếp tục phát triển, nên ưu tiên test DAO với database test và test tích hợp cho các luồng auth, đăng bài, seller approval và transaction vì đây là nơi dễ phát sinh lỗi dữ liệu.")
    doc.add_page_break()

    doc.add_heading("18. Rủi ro và hướng phát triển", level=1)
    add_table(
        doc,
        ["Rủi ro", "Tác động", "Hướng xử lý"],
        [
            ("Thiếu schema tổng đầy đủ", "Khó dựng môi trường mới và kiểm thử tự động.", "Tạo file schema.sql đầy đủ và seed dữ liệu mẫu."),
            ("SQL nằm rải rác trong nhiều DAO", "Khó refactor và kiểm soát transaction.", "Chuẩn hóa naming, gom transaction cho luồng nhiều bảng."),
            ("Phân quyền phân tán", "Dễ sót check ở servlet mới.", "Thêm Filter hoặc AuthGuard utility."),
            ("CSS/JS inline nhiều trong JSP", "Khó bảo trì giao diện.", "Tách asset dùng chung, chuẩn hóa layout include."),
            ("Upload trong webroot", "Có thể lộ file nhạy cảm.", "Lưu ngoài webroot hoặc có servlet phục vụ file theo quyền."),
            ("Thiếu log/audit admin", "Khó truy vết khi admin thay đổi dữ liệu.", "Thêm bảng audit_log cho thao tác quan trọng."),
            ("Không dùng dependency manager", "Nâng cấp thư viện thủ công.", "Cân nhắc chuyển sang Maven/Gradle nếu dự án mở rộng."),
            ("Không có CI/test", "Dễ regression khi chỉnh nghiệp vụ.", "Thêm test tự động và pipeline build WAR."),
        ],
        [2.0, 2.4, 2.8],
    )
    add_bullets(
        doc,
        [
            "Ngắn hạn: bổ sung README triển khai, schema đầy đủ, danh sách account mẫu và checklist test thủ công.",
            "Trung hạn: tách layout JSP chung, chuẩn hóa AuthGuard, gom CSS/JS, thêm unit/integration test cho DAO.",
            "Dài hạn: cân nhắc migration sang Maven/Gradle, thêm connection pool, audit log và phân quyền theo policy rõ ràng.",
        ],
    )
    doc.add_page_break()

    doc.add_heading("19. Phụ lục A - Route và Controller", level=1)
    route_rows = [
        (row["class"], row["routes"], row["views"], row["redirects"]) for row in facts["routes"]  # type: ignore[index]
    ]
    add_table(doc, ["Controller", "Route", "Forward JSP", "Số redirect"], route_rows, [2.0, 2.0, 2.4, 0.8])
    doc.add_paragraph("Bảng route giúp truy vết từ URL vào servlet và JSP. Một số annotation có nhiều route trong cùng servlet, ví dụ LichHenCuaToiServlet và XacNhanGiaoDichServlet.")
    doc.add_page_break()

    doc.add_heading("20. Phụ lục B - DAO và Model", level=1)
    dao_rows = [(row["dao"], row["methods"]) for row in facts["dao_methods"]]  # type: ignore[index]
    add_table(doc, ["DAO", "Phương thức public tiêu biểu"], dao_rows, [1.8, 5.3])
    model_rows = [(row["model"], row["fields"]) for row in facts["model_fields"]]  # type: ignore[index]
    add_table(doc, ["Model", "Field tiêu biểu"], model_rows, [1.8, 5.3])
    doc.add_paragraph("DAO hiện đang chịu trách nhiệm lớn: vừa truy vấn, vừa mapping, vừa có một số side effect như tạo thông báo, cập nhật trạng thái liên quan. Với các nghiệp vụ phức tạp như giao dịch, nên giữ transaction rõ ràng và bổ sung comment ngắn cho các side effect quan trọng.")
    doc.add_page_break()

    doc.add_heading("21. Kết luận", level=1)
    add_paragraphs(
        doc,
        [
            "AutoPro là một dự án Java Web JSP/Servlet có phạm vi nghiệp vụ khá đầy đủ cho bài toán marketplace xe: có tìm kiếm, tài khoản, seller onboarding, đăng bài, kiểm duyệt, chat, lịch hẹn, báo cáo và giao dịch hai bên. Kiến trúc hiện tại dễ hiểu, phù hợp môi trường Tomcat và thuận tiện để demo hoặc triển khai nội bộ.",
            "Điểm đáng giá nhất trong mã nguồn là đã có tách lớp Controller/DAO/Model, dùng PreparedStatement, có hash mật khẩu PBKDF2 và đã bổ sung các luồng nghiệp vụ nâng cao như seller approval, notification và transaction confirmation. Các điểm nên cải thiện nằm ở tài liệu database, test tự động, phân quyền tập trung và quản lý upload file.",
            "Báo cáo này có thể dùng làm tài liệu nộp môn, tài liệu bảo vệ dự án hoặc tài liệu bàn giao kỹ thuật. Nếu cần mở rộng thành bản chuẩn đồ án, nên bổ sung ảnh chụp màn hình giao diện thật, kế hoạch kiểm thử có kết quả pass/fail và phần đánh giá hiệu năng khi dữ liệu lớn.",
        ],
    )
    add_footer(doc)
    doc.save(DOCX_PATH)


def main() -> None:
    ensure_dirs()
    facts = collect_project_facts()
    diagrams = generate_diagrams()
    build_report(diagrams, facts)
    print(DOCX_PATH)


if __name__ == "__main__":
    main()
