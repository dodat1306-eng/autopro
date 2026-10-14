-- Run once against the existing AutoPro database, before deploying the new WAR.
-- Additive migration: existing completed transactions and approved identities stay intact.
SET NAMES utf8mb4;
CREATE TABLE IF NOT EXISTS nhatkyhoatdong (
    maNhatKy BIGINT AUTO_INCREMENT PRIMARY KEY,
    doiTuong VARCHAR(30) NOT NULL,
    maDoiTuong INT NOT NULL,
    nguoiThucHien INT NULL,
    hanhDong VARCHAR(100) NOT NULL,
    trangThaiCu VARCHAR(50) NULL,
    trangThaiMoi VARCHAR(50) NULL,
    ghiChu TEXT NULL,
    tepDinhKem VARCHAR(255) NULL,
    ngayTao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nhatky_object (doiTuong, maDoiTuong, maNhatKy)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS giaodichtiepnhan (
    maGiaoDich INT PRIMARY KEY,
    hanPhanHoi DATETIME NOT NULL,
    ngayChapNhan DATETIME NULL,
    FOREIGN KEY (maGiaoDich) REFERENCES giaodichxe(maGiaoDich) ON DELETE CASCADE,
    INDEX idx_tiepnhan_expiry (ngayChapNhan, hanPhanHoi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS lichhenxacnhan (
    maLichHen INT PRIMARY KEY,
    trangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_XAC_NHAN',
    nguoiDeXuat INT NOT NULL,
    phienBan INT NOT NULL DEFAULT 1,
    daNhac BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (maLichHen) REFERENCES lichhenxemxe(maLichHen) ON DELETE CASCADE,
    CHECK (trangThai IN ('CHO_XAC_NHAN','DA_XAC_NHAN','DA_XEM_XE','KHONG_DEN','DA_HUY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT IGNORE INTO lichhenxacnhan(maLichHen,trangThai,nguoiDeXuat)
SELECT l.maLichHen,
    CASE WHEN y.trangThai IN ('DA_CHOT','HOAN_TAT') THEN 'DA_XEM_XE'
         WHEN y.trangThai='DA_HUY' THEN 'DA_HUY'
         WHEN y.trangThai='DA_XEM_XE' THEN 'DA_XAC_NHAN' ELSE 'CHO_XAC_NHAN' END,
    y.maNguoiMua
FROM lichhenxemxe l JOIN yeucaumuaxe y ON y.maYeuCau=l.maYeuCau;

CREATE TABLE IF NOT EXISTS xacminhlienhe (
    maXacMinh CHAR(36) PRIMARY KEY,
    khoaPhien CHAR(64) NOT NULL,
    maNguoiDung INT NULL,
    mucDich VARCHAR(20) NOT NULL,
    loai VARCHAR(10) NOT NULL,
    lienHe VARCHAR(100) NOT NULL,
    hoTen VARCHAR(100) NULL,
    matKhauHash VARCHAR(255) NULL,
    otpHash VARCHAR(255) NOT NULL,
    soLanSai INT NOT NULL DEFAULT 0,
    daGui BOOLEAN NOT NULL DEFAULT FALSE,
    daDung BOOLEAN NOT NULL DEFAULT FALSE,
    hanDung DATETIME NOT NULL,
    ngayTao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_xacminh_target (lienHe,ngayTao),
    CHECK (mucDich IN ('DANG_KY','LIEN_HE')),
    CHECK (loai IN ('email','phone'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS lienhedaxacminh (
    maNguoiDung INT NOT NULL,
    loai VARCHAR(10) NOT NULL,
    lienHe VARCHAR(100) NOT NULL,
    ngayXacMinh DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(maNguoiDung,loai),
    UNIQUE KEY uq_verified_contact(loai,lienHe),
    FOREIGN KEY (maNguoiDung) REFERENCES nguoidung(maNguoiDung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS khoalienhe (
    lienHe VARCHAR(110) PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
