-- Migration: them bang dangkyseller cho luong dang ky nguoi ban va admin phe duyet seller
-- AutoPro / Java Web JSP-Servlet / MySQL

START TRANSACTION;

CREATE TABLE IF NOT EXISTS dangkyseller (
    maDangKySeller INT AUTO_INCREMENT PRIMARY KEY,
    maNguoiDung INT NOT NULL,
    hoTen VARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    soCCCD VARCHAR(20) NOT NULL,
    anhCCCDMatTruoc VARCHAR(255) NOT NULL,
    anhCCCDMatSau VARCHAR(255) NOT NULL,
    maTinh INT NOT NULL,
    diaChiChiTiet VARCHAR(255) NULL,
    ghiChuNguoiDangKy TEXT NULL,
    trangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_DUYET',
    lyDoTuChoi TEXT NULL,
    nguoiPheDuyet INT NULL,
    ngayPheDuyet DATETIME NULL,
    ngayTao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngayCapNhat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_dangkyseller_user_created ON dangkyseller(maNguoiDung, ngayTao);
CREATE INDEX idx_dangkyseller_status_created ON dangkyseller(trangThai, ngayTao);
CREATE INDEX idx_dangkyseller_cccd ON dangkyseller(soCCCD);
CREATE INDEX idx_dangkyseller_phone ON dangkyseller(soDienThoai);
CREATE INDEX idx_dangkyseller_email ON dangkyseller(email);

ALTER TABLE dangkyseller
    ADD CONSTRAINT fk_dangkyseller_user
        FOREIGN KEY (maNguoiDung) REFERENCES nguoidung(maNguoiDung)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_dangkyseller_tinh
        FOREIGN KEY (maTinh) REFERENCES tinhthanh(maTinh)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    ADD CONSTRAINT fk_dangkyseller_admin
        FOREIGN KEY (nguoiPheDuyet) REFERENCES nguoidung(maNguoiDung)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

ALTER TABLE dangkyseller
    ADD CONSTRAINT chk_dangkyseller_trangthai
        CHECK (trangThai IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_XOA'));

COMMIT;

-- Ghi chu nghiep vu:
-- 1. Khong can bo sung cot boolean sellerDaDuyet trong nguoidung.
--    He thong tiep tuc dung cot vaiTro va chi doi sang 'seller'
--    sau khi admin phe duyet ho so seller.
-- 2. Ho so seller xoa theo huong xoa mem bang trangThai = 'DA_XOA'.
-- 3. Neu admin xoa ho so da duyet thi van giu nguyen vaiTro seller trong nguoidung.
