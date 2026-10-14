-- Fix seed image paths so public pages do not depend on external hotlink URLs.
-- Run this on the local car_market database if old seed rows still point to Wikimedia URLs.

UPDATE xe
SET hinhAnh = 'uploads/cars/toyota-camry.jpg'
WHERE LOWER(tenXe) LIKE '%toyota%' AND LOWER(tenXe) LIKE '%camry%';

UPDATE xe
SET hinhAnh = 'uploads/cars/ford-ranger.jpg'
WHERE LOWER(tenXe) LIKE '%ford%' AND LOWER(tenXe) LIKE '%ranger%';

UPDATE xe
SET hinhAnh = 'uploads/cars/mazda-cx5.jpg'
WHERE LOWER(tenXe) LIKE '%mazda%' AND (LOWER(tenXe) LIKE '%cx-5%' OR LOWER(tenXe) LIKE '%cx5%');

UPDATE xe_hinhanh xh
JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/toyota-camry.jpg'
WHERE LOWER(x.tenXe) LIKE '%toyota%' AND LOWER(x.tenXe) LIKE '%camry%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh
JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/ford-ranger.jpg'
WHERE LOWER(x.tenXe) LIKE '%ford%' AND LOWER(x.tenXe) LIKE '%ranger%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh
JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/mazda-cx5.jpg'
WHERE LOWER(x.tenXe) LIKE '%mazda%' AND (LOWER(x.tenXe) LIKE '%cx-5%' OR LOWER(x.tenXe) LIKE '%cx5%')
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe SET hinhAnh = 'uploads/cars/bmw-320i.jpg'
WHERE LOWER(tenXe) LIKE '%bmw%' AND LOWER(tenXe) LIKE '%320%';

UPDATE xe SET hinhAnh = 'uploads/cars/hyundai-santafe.jpg'
WHERE LOWER(tenXe) LIKE '%hyundai%' AND LOWER(tenXe) LIKE '%santa%';

UPDATE xe SET hinhAnh = 'uploads/cars/audi-q5.jpg'
WHERE LOWER(tenXe) LIKE '%audi%' AND LOWER(tenXe) LIKE '%q5%';

UPDATE xe SET hinhAnh = 'uploads/cars/honda-crv.jpg'
WHERE LOWER(tenXe) LIKE '%honda%' AND LOWER(tenXe) LIKE '%cr-v%';

UPDATE xe SET hinhAnh = 'uploads/cars/mercedes-c-class.jpg'
WHERE LOWER(tenXe) LIKE '%mercedes-benz%' AND LOWER(tenXe) LIKE '%c200%';

UPDATE xe SET hinhAnh = 'uploads/cars/toyota-vios.jpg'
WHERE LOWER(tenXe) LIKE '%toyota%' AND LOWER(tenXe) LIKE '%vios%';

UPDATE xe SET hinhAnh = 'uploads/cars/vinfast-vf8.jpg'
WHERE LOWER(tenXe) LIKE '%vinfast%' AND LOWER(tenXe) LIKE '%vf8%';

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/bmw-320i.jpg'
WHERE LOWER(x.tenXe) LIKE '%bmw%' AND LOWER(x.tenXe) LIKE '%320%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/hyundai-santafe.jpg'
WHERE LOWER(x.tenXe) LIKE '%hyundai%' AND LOWER(x.tenXe) LIKE '%santa%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/audi-q5.jpg'
WHERE LOWER(x.tenXe) LIKE '%audi%' AND LOWER(x.tenXe) LIKE '%q5%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/honda-crv.jpg'
WHERE LOWER(x.tenXe) LIKE '%honda%' AND LOWER(x.tenXe) LIKE '%cr-v%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/mercedes-c-class.jpg'
WHERE LOWER(x.tenXe) LIKE '%mercedes-benz%' AND LOWER(x.tenXe) LIKE '%c200%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/toyota-vios.jpg'
WHERE LOWER(x.tenXe) LIKE '%toyota%' AND LOWER(x.tenXe) LIKE '%vios%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');

UPDATE xe_hinhanh xh JOIN xe x ON x.maXe = xh.maXe
SET xh.duongDan = 'uploads/cars/vinfast-vf8.jpg'
WHERE LOWER(x.tenXe) LIKE '%vinfast%' AND LOWER(x.tenXe) LIKE '%vf8%'
  AND (xh.thuTu = 1 OR xh.duongDan LIKE '%wikimedia%' OR xh.duongDan LIKE '%Special:FilePath%');
