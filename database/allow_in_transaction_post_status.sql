-- Cho phep bai dang tam khoa khi xe dang trong qua trinh giao dich.

ALTER TABLE baidang DROP CONSTRAINT chk_baidang_trangthai;
ALTER TABLE baidang
    ADD CONSTRAINT chk_baidang_trangthai
    CHECK (trangThai IS NULL OR trangThai IN (
        'cho_duyet',
        'da_duyet',
        'tu_choi',
        'canh_bao',
        'dang_giao_dich',
        'da_ban'
    ));
