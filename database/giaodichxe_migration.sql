-- Migration: bo sung bang giaodichxe cho luong xac nhan hoan tat mua ban xe
-- AutoPro / Java Web JSP-Servlet / MySQL

START TRANSACTION;

CREATE TABLE IF NOT EXISTS giaodichxe (
    maGiaoDich INT AUTO_INCREMENT PRIMARY KEY,
    maYeuCau INT NOT NULL,
    maNguoiMua INT NOT NULL,
    maNguoiBan INT NOT NULL,
    maXe INT NOT NULL,
    giaChot DOUBLE NULL,
    ngayChot DATETIME NULL,
    diaDiemGiaoXe VARCHAR(255) NULL,
    soDienThoaiGiaoXe VARCHAR(20) NULL,
    ngayGiaoXe DATETIME NULL,
    trangThai VARCHAR(50) NOT NULL DEFAULT 'MOI_TAO',
    xacNhanNguoiMua TINYINT(1) NOT NULL DEFAULT 0,
    xacNhanNguoiBan TINYINT(1) NOT NULL DEFAULT 0,
    thoiGianXacNhanMua DATETIME NULL,
    thoiGianXacNhanBan DATETIME NULL,
    ghiChuNguoiMua TEXT NULL,
    ghiChuNguoiBan TEXT NULL,
    adminXuLy INT NULL,
    ngayXuLy DATETIME NULL,
    ghiChuAdmin TEXT NULL,
    ngayTao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngayCapNhat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE giaodichxe
    ADD CONSTRAINT uq_giaodichxe_yeucau UNIQUE (maYeuCau);

CREATE INDEX idx_giaodichxe_nguoimua ON giaodichxe(maNguoiMua);
CREATE INDEX idx_giaodichxe_nguoiban ON giaodichxe(maNguoiBan);
CREATE INDEX idx_giaodichxe_xe ON giaodichxe(maXe);
CREATE INDEX idx_giaodichxe_trangthai ON giaodichxe(trangThai);
CREATE INDEX idx_giaodichxe_ngaytao ON giaodichxe(ngayTao);

ALTER TABLE giaodichxe
    ADD CONSTRAINT fk_giaodichxe_yeucau
        FOREIGN KEY (maYeuCau) REFERENCES yeucaumuaxe(maYeuCau)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_giaodichxe_nguoimua
        FOREIGN KEY (maNguoiMua) REFERENCES nguoidung(maNguoiDung)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_giaodichxe_nguoiban
        FOREIGN KEY (maNguoiBan) REFERENCES nguoidung(maNguoiDung)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_giaodichxe_xe
        FOREIGN KEY (maXe) REFERENCES xe(maXe)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_giaodichxe_admin
        FOREIGN KEY (adminXuLy) REFERENCES nguoidung(maNguoiDung)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

ALTER TABLE giaodichxe
    ADD CONSTRAINT chk_giaodichxe_trangthai
        CHECK (trangThai IN (
            'MOI_TAO',
            'CHO_XAC_NHAN_HAI_BEN',
            'CHO_XAC_NHAN_CON_LAI',
            'HOAN_TAT',
            'HUY',
            'TRANH_CHAP'
        ));

COMMIT;

-- Neu he thong hien tai dang co CHECK constraint cho baidang.trangThai
-- thi can cap nhat de cho phep gia tri 'da_ban', vi giao dich HOAN_TAT
-- se doi bai dang sang trang thai nay.
--
-- Vi du:
-- ALTER TABLE baidang DROP CHECK chk_baidang_trangthai;
-- ALTER TABLE baidang
--     ADD CONSTRAINT chk_baidang_trangthai
--     CHECK (trangThai IS NULL OR trangThai IN ('cho_duyet', 'da_duyet', 'tu_choi', 'canh_bao', 'da_ban'));
