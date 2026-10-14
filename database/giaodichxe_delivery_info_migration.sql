-- Migration: bo sung so dien thoai giao xe cho ho so giao dich
-- Chay duoc nhieu lan, chi them cot neu chua ton tai.

SET @has_so_dien_thoai_giao_xe := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'giaodichxe'
      AND COLUMN_NAME = 'soDienThoaiGiaoXe'
);

SET @ddl := IF(
    @has_so_dien_thoai_giao_xe = 0,
    'ALTER TABLE giaodichxe ADD COLUMN soDienThoaiGiaoXe VARCHAR(20) NULL AFTER diaDiemGiaoXe',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
