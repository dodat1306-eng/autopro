package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import dao.HoiThoaiDAO;
import dao.ThongBaoDAO;
import dao.XeDAO;
import dao.YeuCauKetNoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import model.GiaoDichXe;
import model.NguoiDung;
import model.Xe;
import util.WebAppUrlUtil;

@WebServlet("/yeu-cau-ket-noi")
public class YeuCauKetNoiServlet extends HttpServlet {

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void putFlash(HttpSession session, String key, String message) {
        session.setAttribute(key, message);
    }

    private Timestamp parseDateTimeLocal(String value) {
        String raw = trimToEmpty(value);
        if (raw.isEmpty()) {
            return null;
        }
        try {
            return Timestamp.valueOf(LocalDateTime.parse(raw));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }

        if (!user.isThanhVien()) {
            putFlash(request.getSession(), "detailError", "Chỉ tài khoản thành viên mới có thể gửi yêu cầu kết nối.");
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        if ("blocked".equalsIgnoreCase(user.getTrangThai())) {
            putFlash(request.getSession(), "detailError", "Tài khoản của bạn đang bị hạn chế và chưa thể gửi yêu cầu kết nối.");
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        int maXe;
        try {
            maXe = Integer.parseInt(request.getParameter("maXe"));
        } catch (NumberFormatException e) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        Xe xe = new XeDAO().getPublicXeById(maXe);
        if (xe == null) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        if (xe.isDaBan() || xe.isDangGiaoDich()
                || giaoDichXeDAO.hasCompletedTransactionForCar(maXe)
                || giaoDichXeDAO.hasActiveTransactionForCar(maXe)) {
            putFlash(request.getSession(), "detailError", "Xe này đang được giao dịch hoặc đã bán, hiện không nhận thêm yêu cầu mới.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        BaiDangDAO baiDangDAO = new BaiDangDAO();
        Integer maNguoiBan = baiDangDAO.getApprovedSellerIdByMaXe(maXe);
        if (maNguoiBan == null) {
            putFlash(request.getSession(), "detailError", "Không thể xác định người bán của bài đăng này.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        if (maNguoiBan.equals(user.getMaNguoiDung())) {
            putFlash(request.getSession(), "detailError", "Bạn không thể tự gửi yêu cầu kết nối cho chính bài đăng của mình.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        String ghiChuNguoiMua = trimToEmpty(request.getParameter("ghiChuNguoiMua"))
                .replace("<", "")
                .replace(">", "");
        if (ghiChuNguoiMua.length() > 1000) {
            putFlash(request.getSession(), "detailError", "Ghi chú yêu cầu kết nối không được vượt quá 1000 ký tự.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        String purchaseMode = trimToEmpty(request.getParameter("purchaseMode"));
        boolean taoGiaoDichNgay = "create-transaction".equalsIgnoreCase(purchaseMode)
                || "direct-sale".equalsIgnoreCase(purchaseMode);
        Timestamp thoiGianHen = null;
        String diaDiemHen = null;
        String ghiChuHen = null;
        String diaDiemGiaoXe = null;
        String soDienThoaiGiaoXe = null;

        if (taoGiaoDichNgay) {
            diaDiemGiaoXe = trimToEmpty(request.getParameter("diaDiemGiaoXe"))
                    .replace("<", "")
                    .replace(">", "");
            soDienThoaiGiaoXe = trimToEmpty(request.getParameter("soDienThoaiGiaoXe"))
                    .replace("<", "")
                    .replace(">", "");

            if (diaDiemGiaoXe.isBlank() || soDienThoaiGiaoXe.isBlank()) {
                putFlash(request.getSession(), "detailError", "Vui lòng nhập địa chỉ và số điện thoại để người bán giao xe.");
                WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                return;
            }
            if (diaDiemGiaoXe.length() > 255 || soDienThoaiGiaoXe.length() > 20
                    || !soDienThoaiGiaoXe.matches("^[0-9+().\\-\\s]{8,20}$")) {
                putFlash(request.getSession(), "detailError", "Thông tin giao xe không hợp lệ. Vui lòng kiểm tra lại địa chỉ và số điện thoại.");
                WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                return;
            }
        } else {
            thoiGianHen = parseDateTimeLocal(request.getParameter("thoiGianHen"));
            diaDiemHen = trimToEmpty(request.getParameter("diaDiemHen"))
                    .replace("<", "")
                    .replace(">", "");
            ghiChuHen = trimToEmpty(request.getParameter("ghiChuHen"))
                    .replace("<", "")
                    .replace(">", "");

            if (thoiGianHen == null || diaDiemHen.isBlank()) {
                putFlash(request.getSession(), "detailError", "Vui lòng nhập thời gian và địa điểm hẹn xem xe.");
                WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                return;
            }
            if (diaDiemHen.length() > 255 || ghiChuHen.length() > 1000) {
                putFlash(request.getSession(), "detailError", "Thông tin lịch hẹn quá dài. Vui lòng rút gọn nội dung.");
                WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                return;
            }
        }

        YeuCauKetNoiDAO yeuCauKetNoiDAO = new YeuCauKetNoiDAO();
        if (yeuCauKetNoiDAO.hasOpenRequestForCar(user.getMaNguoiDung(), maXe)) {
            putFlash(request.getSession(), "detailError", "Bạn đã có một yêu cầu kết nối đang mở cho xe này.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        if (taoGiaoDichNgay) {
            GiaoDichXe giaoDich = giaoDichXeDAO.createInProgressTransactionForCar(
                    user.getMaNguoiDung(),
                    maXe,
                    xe.getGia(),
                    ghiChuNguoiMua,
                    diaDiemGiaoXe,
                    soDienThoaiGiaoXe
            );
            if (giaoDich == null) {
                putFlash(request.getSession(), "detailError", "Không thể tạo giao dịch lúc này. Xe có thể đang giao dịch hoặc đã bán.");
                WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                return;
            }

            request.getSession().setAttribute(
                    "detailSuccess",
                    "Đã gửi yêu cầu giao dịch. Người bán cần tiếp nhận trong 48 giờ; xe chỉ khóa yêu cầu mới sau khi được tiếp nhận."
            );
            WebAppUrlUtil.redirect(request, response, "giao-dich/chi-tiet?id=" + giaoDich.getMaGiaoDich());
            return;
        }

        int maYeuCau = yeuCauKetNoiDAO.createYeuCauKetNoi(
                user.getMaNguoiDung(),
                maXe,
                xe.getGia(),
                ghiChuNguoiMua,
                thoiGianHen,
                diaDiemHen,
                ghiChuHen
        );
        if (maYeuCau <= 0) {
            putFlash(request.getSession(), "detailError", "Không thể tạo yêu cầu kết nối lúc này. Vui lòng thử lại.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        new HoiThoaiDAO().getOrCreateHoiThoai(user.getMaNguoiDung(), maNguoiBan, maXe);
        new ThongBaoDAO().createNotification(
                maNguoiBan,
                "CONNECTION_REQUEST",
                "Có yêu cầu kết nối mới",
                user.getHoTen() + " vừa gửi yêu cầu kết nối cho \"" + xe.getTenXe() + "\".",
                "lich-hen-cua-toi"
        );

        request.getSession().setAttribute(
                "requestSuccess",
                "Đã tạo yêu cầu kết nối #" + maYeuCau + ". Người bán sẽ nhận được yêu cầu này trong mục lịch hẹn và chat để tiếp tục trao đổi."
        );
        WebAppUrlUtil.redirect(request, response, "lich-hen-cua-toi?success=1");
    }
}
