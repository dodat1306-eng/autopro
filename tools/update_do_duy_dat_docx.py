# -*- coding: utf-8 -*-
from __future__ import annotations

import re
from copy import deepcopy
from pathlib import Path

from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.enum.table import WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Pt, Inches


INPUT = Path(r"D:\07_Do_Duy_Dat.docx")
OUTPUT = Path(r"D:\07_Do_Duy_Dat_hoan_chinh.docx")


ABBREVIATIONS = [
    ("CNTT", "Công nghệ thông tin", "Lĩnh vực nền tảng của đề tài."),
    ("CSDL", "Cơ sở dữ liệu", "Nơi lưu trữ dữ liệu hệ thống."),
    ("UC", "Use Case", "Ca sử dụng/chức năng nghiệp vụ của hệ thống."),
    ("UML", "Unified Modeling Language", "Ngôn ngữ mô hình hóa dùng để thiết kế hệ thống."),
    ("ERD", "Entity Relationship Diagram", "Sơ đồ thực thể - quan hệ của cơ sở dữ liệu."),
    ("MVC", "Model - View - Controller", "Mô hình tách dữ liệu, giao diện và điều khiển."),
    ("JSP", "JavaServer Pages", "Công nghệ tạo giao diện động phía server."),
    ("Servlet", "Java Servlet", "Thành phần xử lý request/response trong Java Web."),
    ("DAO", "Data Access Object", "Lớp truy cập dữ liệu và thao tác với CSDL."),
    ("JDBC", "Java Database Connectivity", "API Java dùng để kết nối và truy vấn CSDL."),
    ("SQL", "Structured Query Language", "Ngôn ngữ truy vấn cơ sở dữ liệu quan hệ."),
    ("MySQL", "My Structured Query Language", "Hệ quản trị cơ sở dữ liệu quan hệ."),
    ("HTML", "HyperText Markup Language", "Ngôn ngữ đánh dấu xây dựng nội dung trang web."),
    ("CSS", "Cascading Style Sheets", "Ngôn ngữ định kiểu giao diện web."),
    ("JS", "JavaScript", "Ngôn ngữ xử lý tương tác phía trình duyệt."),
    ("HTTP", "HyperText Transfer Protocol", "Giao thức truyền tải dữ liệu web."),
    ("CRUD", "Create - Read - Update - Delete", "Bốn thao tác dữ liệu cơ bản."),
    ("IDE", "Integrated Development Environment", "Môi trường phát triển tích hợp."),
    ("JDK", "Java Development Kit", "Bộ công cụ phát triển ứng dụng Java."),
    ("WAR", "Web Application Archive", "Gói triển khai ứng dụng web Java."),
    ("API", "Application Programming Interface", "Giao diện lập trình ứng dụng."),
    ("OTP", "One-Time Password", "Mã xác thực dùng một lần."),
]


def set_font(paragraph, bold: bool = False, size: float = 12) -> None:
    for run in paragraph.runs:
        run.font.name = "Times New Roman"
        run.font.size = Pt(size)
        run.bold = bold


def clear_paragraph(paragraph) -> None:
    p = paragraph._p
    for child in list(p):
        p.remove(child)


def set_cell_text(cell, text: str, bold: bool = False, align: WD_ALIGN_PARAGRAPH | None = None) -> None:
    cell.text = ""
    p = cell.paragraphs[0]
    if align is not None:
        p.alignment = align
    run = p.add_run(str(text))
    run.font.name = "Times New Roman"
    run.font.size = Pt(12)
    run.bold = bold
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER


def shade_cell(cell, fill: str = "D9EAF7") -> None:
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def add_bookmark(paragraph, name: str, bookmark_id: int) -> None:
    start = OxmlElement("w:bookmarkStart")
    start.set(qn("w:id"), str(bookmark_id))
    start.set(qn("w:name"), name)
    end = OxmlElement("w:bookmarkEnd")
    end.set(qn("w:id"), str(bookmark_id))
    paragraph._p.insert(0, start)
    paragraph._p.append(end)


def add_pageref_field(paragraph, bookmark_name: str) -> None:
    run = paragraph.add_run()
    begin = OxmlElement("w:fldChar")
    begin.set(qn("w:fldCharType"), "begin")
    begin.set(qn("w:dirty"), "true")

    instr_run = OxmlElement("w:r")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = f" PAGEREF {bookmark_name} \\h "
    instr_run.append(instr)

    separate = OxmlElement("w:fldChar")
    separate.set(qn("w:fldCharType"), "separate")

    placeholder_run = OxmlElement("w:r")
    text = OxmlElement("w:t")
    text.text = "..."
    placeholder_run.append(text)

    end = OxmlElement("w:fldChar")
    end.set(qn("w:fldCharType"), "end")

    run._r.append(begin)
    paragraph._p.append(instr_run)
    paragraph._p.append(separate)
    paragraph._p.append(placeholder_run)
    paragraph._p.append(end)


def add_page_number_field(paragraph) -> None:
    run = paragraph.add_run()
    begin = OxmlElement("w:fldChar")
    begin.set(qn("w:fldCharType"), "begin")

    instr_run = OxmlElement("w:r")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = " PAGE "
    instr_run.append(instr)

    separate = OxmlElement("w:fldChar")
    separate.set(qn("w:fldCharType"), "separate")

    result_run = OxmlElement("w:r")
    result = OxmlElement("w:t")
    result.text = "1"
    result_run.append(result)

    end = OxmlElement("w:fldChar")
    end.set(qn("w:fldCharType"), "end")

    run._r.append(begin)
    paragraph._p.append(instr_run)
    paragraph._p.append(separate)
    paragraph._p.append(result_run)
    paragraph._p.append(end)


def set_page_number_start(section, start: int) -> None:
    sect_pr = section._sectPr
    pg_num = sect_pr.find(qn("w:pgNumType"))
    if pg_num is None:
        pg_num = OxmlElement("w:pgNumType")
        sect_pr.append(pg_num)
    pg_num.set(qn("w:start"), str(start))


def add_update_fields_setting(doc: Document) -> None:
    settings = doc.settings.element
    existing = settings.find(qn("w:updateFields"))
    if existing is None:
        existing = OxmlElement("w:updateFields")
        settings.append(existing)
    existing.set(qn("w:val"), "true")


def insert_paragraph_before(doc: Document, ref_paragraph, text: str = "", style: str | None = None):
    p = doc.add_paragraph(text, style=style)
    ref_paragraph._p.addprevious(p._p)
    return p


def insert_page_break_before(doc: Document, ref_paragraph):
    p = doc.add_paragraph()
    p.add_run().add_break(WD_BREAK.PAGE)
    ref_paragraph._p.addprevious(p._p)
    return p


def insert_table_before(doc: Document, ref_paragraph, rows: int, cols: int):
    table = doc.add_table(rows=rows, cols=cols)
    try:
        table.style = "Table Grid"
    except KeyError:
        pass
    set_table_borders(table)
    ref_paragraph._p.addprevious(table._tbl)
    return table


def set_table_borders(table) -> None:
    tbl_pr = table._tbl.tblPr
    borders = tbl_pr.first_child_found_in("w:tblBorders")
    if borders is None:
        borders = OxmlElement("w:tblBorders")
        tbl_pr.append(borders)
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        tag = f"w:{edge}"
        element = borders.find(qn(tag))
        if element is None:
            element = OxmlElement(tag)
            borders.append(element)
        element.set(qn("w:val"), "single")
        element.set(qn("w:sz"), "6")
        element.set(qn("w:space"), "0")
        element.set(qn("w:color"), "000000")


def heading_before(doc: Document, ref_paragraph, text: str):
    p = insert_paragraph_before(doc, ref_paragraph, text)
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    set_font(p, bold=True, size=14)
    return p


def collect_caption_paragraphs(doc: Document):
    figures = []
    tables = []
    fig_re = re.compile(r"^Hình\s+\d+(?:\.\d+)*\s*[:.\-]", re.IGNORECASE)
    table_re = re.compile(r"^Bảng\s+\d+(?:\.\d+)*\s*[:.\-]?", re.IGNORECASE)
    for para in doc.paragraphs:
        text = para.text.strip()
        if fig_re.match(text):
            figures.append(para)
        elif table_re.match(text):
            tables.append(para)
    return figures, tables


def add_listing_table(doc: Document, ref_paragraph, captions, title_col: str, bookmark_prefix: str):
    table = insert_table_before(doc, ref_paragraph, rows=1, cols=3)
    headers = ["STT", title_col, "Trang"]
    widths = [Inches(0.65), Inches(5.3), Inches(0.85)]
    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        set_cell_text(cell, h, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER)
        shade_cell(cell)
        cell.width = widths[i]

    for idx, para in enumerate(captions, 1):
        bookmark = f"{bookmark_prefix}_{idx}"
        add_bookmark(para, bookmark, 1000 + idx if bookmark_prefix == "hinh" else 2000 + idx)
        cells = table.add_row().cells
        set_cell_text(cells[0], str(idx), align=WD_ALIGN_PARAGRAPH.CENTER)
        set_cell_text(cells[1], para.text.strip())
        cells[2].text = ""
        p = cells[2].paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        add_pageref_field(p, bookmark)
    return table


def add_abbreviation_table(doc: Document, ref_paragraph):
    table = insert_table_before(doc, ref_paragraph, rows=1, cols=4)
    headers = ["STT", "Từ viết tắt", "Cụm từ đầy đủ", "Ý nghĩa"]
    for i, h in enumerate(headers):
        cell = table.rows[0].cells[i]
        set_cell_text(cell, h, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER)
        shade_cell(cell)
    for idx, (abbr, full, meaning) in enumerate(ABBREVIATIONS, 1):
        cells = table.add_row().cells
        set_cell_text(cells[0], str(idx), align=WD_ALIGN_PARAGRAPH.CENTER)
        set_cell_text(cells[1], abbr, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER)
        set_cell_text(cells[2], full)
        set_cell_text(cells[3], meaning)
    return table


def remove_cover_page_number(doc: Document) -> None:
    section = doc.sections[0]
    section.different_first_page_header_footer = True
    for container in (section.first_page_header, section.first_page_footer):
        for para in container.paragraphs:
            clear_paragraph(para)
    footer_paragraph = section.footer.paragraphs[0] if section.footer.paragraphs else section.footer.add_paragraph()
    clear_paragraph(footer_paragraph)
    footer_paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    add_page_number_field(footer_paragraph)
    set_font(footer_paragraph, size=12)
    # Trang bìa không hiện số, trang kế tiếp hiển thị bắt đầu từ 1.
    set_page_number_start(section, 0)


def find_chapter_one(doc: Document):
    for para in doc.paragraphs:
        text = para.text.strip().lower()
        if text.startswith("chương 1") or text.startswith("chương 1:") or text.startswith("chương 1."):
            return para
    raise RuntimeError("Không tìm thấy vị trí Chương 1 để chèn danh mục.")


def main() -> None:
    doc = Document(INPUT)
    add_update_fields_setting(doc)
    remove_cover_page_number(doc)

    figures, tables = collect_caption_paragraphs(doc)
    ref = find_chapter_one(doc)

    heading_before(doc, ref, "DANH MỤC HÌNH")
    if figures:
        add_listing_table(doc, ref, figures, "Tên hình", "hinh")
    else:
        p = insert_paragraph_before(doc, ref, "Chưa phát hiện caption hình trong tài liệu.")
        set_font(p)
    insert_page_break_before(doc, ref)

    heading_before(doc, ref, "DANH MỤC BẢNG")
    if tables:
        add_listing_table(doc, ref, tables, "Tên bảng", "bang")
    else:
        p = insert_paragraph_before(doc, ref, "Chưa phát hiện caption bảng trong tài liệu.")
        set_font(p)
    insert_page_break_before(doc, ref)

    heading_before(doc, ref, "DANH MỤC TỪ VIẾT TẮT")
    add_abbreviation_table(doc, ref)
    insert_page_break_before(doc, ref)

    doc.save(OUTPUT)
    print(f"OUTPUT={OUTPUT}")
    print(f"FIGURES={len(figures)}")
    print(f"TABLE_CAPTIONS={len(tables)}")


if __name__ == "__main__":
    main()
