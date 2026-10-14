-- Cho phep yeu cau mua xe / lich hen duoc dong bo sang HOAN_TAT
-- khi giao dich xe da duoc ca nguoi mua va nguoi ban xac nhan.

ALTER TABLE yeucaumuaxe DROP CONSTRAINT chk_yeucaumuaxe_trangthai;
ALTER TABLE yeucaumuaxe
    ADD CONSTRAINT chk_yeucaumuaxe_trangthai
    CHECK (trangThai IS NULL OR trangThai IN (
        'CHO_LIEN_HE',
        'DA_LIEN_HE',
        'DA_XEM_XE',
        'DA_CHOT',
        'HOAN_TAT',
        'DA_HUY'
    ));

ALTER TABLE lichhenxemxe DROP CONSTRAINT chk_lichhen_trangthai;
ALTER TABLE lichhenxemxe
    ADD CONSTRAINT chk_lichhen_trangthai
    CHECK (trangThai IS NULL OR trangThai IN (
        'CHO_LIEN_HE',
        'DA_LIEN_HE',
        'DA_XEM_XE',
        'DA_CHOT',
        'HOAN_TAT',
        'DA_HUY'
    ));

-- Sua du lieu cu: yeu cau/lien hen cua giao dich da hoan tat cung phai HOAN_TAT.
UPDATE yeucaumuaxe y
JOIN giaodichxe g ON g.maYeuCau = y.maYeuCau AND g.trangThai = 'HOAN_TAT'
SET y.trangThai = 'HOAN_TAT', y.ngayCapNhat = NOW()
WHERE y.trangThai <> 'HOAN_TAT';

UPDATE lichhenxemxe l
JOIN giaodichxe g ON g.maYeuCau = l.maYeuCau AND g.trangThai = 'HOAN_TAT'
SET l.trangThai = 'HOAN_TAT', l.ngayCapNhat = NOW()
WHERE l.trangThai <> 'HOAN_TAT';
