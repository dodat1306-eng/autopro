-- Structure only. No user records or uploaded files.
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `baidang` (
  `maBaiDang` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiDung` int(11) DEFAULT NULL,
  `maXe` int(11) DEFAULT NULL,
  `tieuDe` varchar(255) DEFAULT NULL,
  `moTa` text DEFAULT NULL,
  `trangThai` varchar(50) DEFAULT NULL,
  `ngayDang` datetime DEFAULT current_timestamp(),
  `maTinh` int(11) DEFAULT NULL,
  `ghiChuAdmin` text DEFAULT NULL,
  `luotXem` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`maBaiDang`),
  KEY `fk_baidang_tinh` (`maTinh`),
  KEY `idx_baidang_xe_date` (`maXe`,`ngayDang`),
  KEY `idx_baidang_seller_status_date` (`maNguoiDung`,`trangThai`,`ngayDang`),
  CONSTRAINT `fk_baidang_nguoidung` FOREIGN KEY (`maNguoiDung`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_baidang_tinh` FOREIGN KEY (`maTinh`) REFERENCES `tinhthanh` (`maTinh`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_baidang_xe` FOREIGN KEY (`maXe`) REFERENCES `xe` (`maXe`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_baidang_trangthai` CHECK (`trangThai` is null or `trangThai` in ('cho_duyet','da_duyet','tu_choi','canh_bao','dang_giao_dich','da_ban'))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `baidang_yeuthich` (
  `maYeuThich` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiDung` int(11) NOT NULL,
  `maBaiDang` int(11) NOT NULL,
  `ngayLuu` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`maYeuThich`),
  UNIQUE KEY `uq_baidang_yeuthich_user_post` (`maNguoiDung`,`maBaiDang`),
  KEY `fk_baidang_yeuthich_post` (`maBaiDang`),
  CONSTRAINT `fk_baidang_yeuthich_post` FOREIGN KEY (`maBaiDang`) REFERENCES `baidang` (`maBaiDang`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_baidang_yeuthich_user` FOREIGN KEY (`maNguoiDung`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `baocao` (
  `maBaoCao` int(11) NOT NULL AUTO_INCREMENT,
  `maBaiDang` int(11) DEFAULT NULL,
  `maNguoiBaoCao` int(11) DEFAULT NULL,
  `lyDo` text DEFAULT NULL,
  `trangThai` varchar(50) DEFAULT NULL,
  `ngayBaoCao` datetime DEFAULT current_timestamp(),
  `ghiChuAdmin` text DEFAULT NULL,
  `maAdminXuLy` int(11) DEFAULT NULL,
  `ngayXuLy` datetime DEFAULT NULL,
  `chiTiet` text DEFAULT NULL,
  PRIMARY KEY (`maBaoCao`),
  KEY `fk_baocao_admin` (`maAdminXuLy`),
  KEY `fk_baocao_baidang` (`maBaiDang`),
  KEY `fk_baocao_nguoibaocao` (`maNguoiBaoCao`),
  CONSTRAINT `fk_baocao_admin` FOREIGN KEY (`maAdminXuLy`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_baocao_baidang` FOREIGN KEY (`maBaiDang`) REFERENCES `baidang` (`maBaiDang`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_baocao_nguoibaocao` FOREIGN KEY (`maNguoiBaoCao`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_baocao_trangthai` CHECK (`trangThai` is null or `trangThai` in ('CHO_XU_LY','DA_CANH_BAO','DA_XOA_BAI','BO_QUA'))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `dangkyseller` (
  `maDangKySeller` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiDung` int(11) NOT NULL,
  `hoTen` varchar(100) NOT NULL,
  `soDienThoai` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `soCCCD` varchar(20) NOT NULL,
  `anhCCCDMatTruoc` varchar(255) NOT NULL,
  `anhCCCDMatSau` varchar(255) NOT NULL,
  `maTinh` int(11) NOT NULL,
  `diaChiChiTiet` varchar(255) DEFAULT NULL,
  `ghiChuNguoiDangKy` text DEFAULT NULL,
  `trangThai` varchar(30) NOT NULL DEFAULT 'CHO_DUYET',
  `lyDoTuChoi` text DEFAULT NULL,
  `nguoiPheDuyet` int(11) DEFAULT NULL,
  `ngayPheDuyet` datetime DEFAULT NULL,
  `ngayTao` datetime NOT NULL DEFAULT current_timestamp(),
  `ngayCapNhat` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`maDangKySeller`),
  KEY `idx_dangkyseller_user_created` (`maNguoiDung`,`ngayTao`),
  KEY `idx_dangkyseller_status_created` (`trangThai`,`ngayTao`),
  KEY `idx_dangkyseller_cccd` (`soCCCD`),
  KEY `idx_dangkyseller_phone` (`soDienThoai`),
  KEY `idx_dangkyseller_email` (`email`),
  KEY `fk_dangkyseller_tinh` (`maTinh`),
  KEY `fk_dangkyseller_admin` (`nguoiPheDuyet`),
  CONSTRAINT `fk_dangkyseller_admin` FOREIGN KEY (`nguoiPheDuyet`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_dangkyseller_tinh` FOREIGN KEY (`maTinh`) REFERENCES `tinhthanh` (`maTinh`) ON UPDATE CASCADE,
  CONSTRAINT `fk_dangkyseller_user` FOREIGN KEY (`maNguoiDung`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_dangkyseller_trangthai` CHECK (`trangThai` in ('CHO_DUYET','DA_DUYET','TU_CHOI','DA_XOA'))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `giaodichxe` (
  `maGiaoDich` int(11) NOT NULL AUTO_INCREMENT,
  `maYeuCau` int(11) NOT NULL,
  `maNguoiMua` int(11) NOT NULL,
  `maNguoiBan` int(11) NOT NULL,
  `maXe` int(11) NOT NULL,
  `giaChot` double DEFAULT NULL,
  `ngayChot` datetime DEFAULT NULL,
  `diaDiemGiaoXe` varchar(255) DEFAULT NULL,
  `soDienThoaiGiaoXe` varchar(20) DEFAULT NULL,
  `ngayGiaoXe` datetime DEFAULT NULL,
  `trangThai` varchar(50) NOT NULL DEFAULT 'MOI_TAO',
  `xacNhanNguoiMua` tinyint(1) NOT NULL DEFAULT 0,
  `xacNhanNguoiBan` tinyint(1) NOT NULL DEFAULT 0,
  `thoiGianXacNhanMua` datetime DEFAULT NULL,
  `thoiGianXacNhanBan` datetime DEFAULT NULL,
  `ghiChuNguoiMua` text DEFAULT NULL,
  `ghiChuNguoiBan` text DEFAULT NULL,
  `adminXuLy` int(11) DEFAULT NULL,
  `ngayXuLy` datetime DEFAULT NULL,
  `ghiChuAdmin` text DEFAULT NULL,
  `ngayTao` datetime NOT NULL DEFAULT current_timestamp(),
  `ngayCapNhat` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`maGiaoDich`),
  UNIQUE KEY `uq_giaodichxe_yeucau` (`maYeuCau`),
  KEY `idx_giaodichxe_nguoimua` (`maNguoiMua`),
  KEY `idx_giaodichxe_nguoiban` (`maNguoiBan`),
  KEY `idx_giaodichxe_xe` (`maXe`),
  KEY `idx_giaodichxe_trangthai` (`trangThai`),
  KEY `fk_giaodichxe_admin` (`adminXuLy`),
  KEY `idx_giaodichxe_buyer_created` (`maNguoiMua`,`ngayTao`),
  KEY `idx_giaodichxe_seller_created` (`maNguoiBan`,`ngayTao`),
  CONSTRAINT `fk_giaodichxe_admin` FOREIGN KEY (`adminXuLy`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_giaodichxe_nguoiban` FOREIGN KEY (`maNguoiBan`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_giaodichxe_nguoimua` FOREIGN KEY (`maNguoiMua`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_giaodichxe_xe` FOREIGN KEY (`maXe`) REFERENCES `xe` (`maXe`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_giaodichxe_yeucau` FOREIGN KEY (`maYeuCau`) REFERENCES `yeucaumuaxe` (`maYeuCau`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `hangxe` (
  `maHang` int(11) NOT NULL AUTO_INCREMENT,
  `tenHang` varchar(100) DEFAULT NULL,
  `slug` varchar(120) DEFAULT NULL,
  `logoUrl` varchar(500) DEFAULT NULL,
  `tuKhoaDongBo` varchar(255) DEFAULT NULL,
  `thuTuSapXep` int(11) DEFAULT 0,
  PRIMARY KEY (`maHang`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `hoithoai` (
  `maHoiThoai` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiMua` int(11) DEFAULT NULL,
  `maNguoiBan` int(11) DEFAULT NULL,
  `maXe` int(11) DEFAULT NULL,
  PRIMARY KEY (`maHoiThoai`),
  UNIQUE KEY `uq_hoithoai` (`maNguoiMua`,`maNguoiBan`,`maXe`),
  KEY `fk_hoithoai_nguoiban` (`maNguoiBan`),
  KEY `fk_hoithoai_xe` (`maXe`),
  CONSTRAINT `fk_hoithoai_nguoiban` FOREIGN KEY (`maNguoiBan`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_hoithoai_nguoimua` FOREIGN KEY (`maNguoiMua`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_hoithoai_xe` FOREIGN KEY (`maXe`) REFERENCES `xe` (`maXe`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `lichhenxemxe` (
  `maLichHen` int(11) NOT NULL AUTO_INCREMENT,
  `maYeuCau` int(11) NOT NULL,
  `thoiGianHen` datetime DEFAULT NULL,
  `diaDiemHen` varchar(255) DEFAULT NULL,
  `ghiChuHen` text DEFAULT NULL,
  `trangThai` varchar(50) NOT NULL DEFAULT 'CHO_LIEN_HE',
  `ngayTao` datetime NOT NULL DEFAULT current_timestamp(),
  `ngayCapNhat` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`maLichHen`),
  KEY `idx_lichhen_yeucau_updated` (`maYeuCau`,`ngayCapNhat`),
  CONSTRAINT `fk_lichhen_yeucau` FOREIGN KEY (`maYeuCau`) REFERENCES `yeucaumuaxe` (`maYeuCau`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_lichhen_trangthai` CHECK (`trangThai` is null or `trangThai` in ('CHO_LIEN_HE','DA_LIEN_HE','DA_XEM_XE','DA_CHOT','HOAN_TAT','DA_HUY'))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `nguoidung` (
  `maNguoiDung` int(11) NOT NULL AUTO_INCREMENT,
  `hoTen` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `matKhau` varchar(100) DEFAULT NULL,
  `vaiTro` varchar(20) DEFAULT NULL,
  `trangThai` varchar(20) DEFAULT 'active',
  `soDienThoai` varchar(20) DEFAULT NULL,
  `otpReset` varchar(12) DEFAULT NULL,
  `otpHetHan` datetime DEFAULT NULL,
  `anhDaiDien` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`maNguoiDung`),
  UNIQUE KEY `uq_nguoidung_email` (`email`),
  KEY `email` (`email`) USING BTREE,
  CONSTRAINT `chk_nguoidung_vaitro` CHECK (`vaiTro` is null or `vaiTro` in ('admin','buyer','seller'))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `thongbao` (
  `maThongBao` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiDung` int(11) NOT NULL,
  `loai` varchar(50) NOT NULL,
  `tieuDe` varchar(255) NOT NULL,
  `noiDung` text DEFAULT NULL,
  `urlDich` varchar(255) DEFAULT NULL,
  `daDoc` tinyint(1) NOT NULL DEFAULT 0,
  `ngayTao` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`maThongBao`),
  KEY `idx_thongbao_user_unread_created` (`maNguoiDung`,`daDoc`,`ngayTao`),
  KEY `idx_thongbao_user_url` (`maNguoiDung`,`urlDich`),
  CONSTRAINT `fk_thongbao_nguoidung` FOREIGN KEY (`maNguoiDung`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tinhthanh` (
  `maTinh` int(11) NOT NULL AUTO_INCREMENT,
  `tenTinh` varchar(100) DEFAULT NULL,
  `mien` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`maTinh`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `tinnhan` (
  `maTinNhan` int(11) NOT NULL AUTO_INCREMENT,
  `maHoiThoai` int(11) DEFAULT NULL,
  `nguoiGui` int(11) DEFAULT NULL,
  `noiDung` text DEFAULT NULL,
  `thoiGian` datetime DEFAULT current_timestamp(),
  `trangThai` varchar(20) DEFAULT 'ChuaDoc',
  PRIMARY KEY (`maTinNhan`),
  KEY `fk_tinnhan_hoithoai` (`maHoiThoai`),
  KEY `fk_tinnhan_nguoigui` (`nguoiGui`),
  CONSTRAINT `fk_tinnhan_hoithoai` FOREIGN KEY (`maHoiThoai`) REFERENCES `hoithoai` (`maHoiThoai`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_tinnhan_nguoigui` FOREIGN KEY (`nguoiGui`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_tinnhan_trangthai` CHECK (`trangThai` is null or `trangThai` in ('ChuaDoc','DaDoc'))
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `xe` (
  `maXe` int(11) NOT NULL AUTO_INCREMENT,
  `maHang` int(11) DEFAULT NULL,
  `tenXe` varchar(100) DEFAULT NULL,
  `gia` double DEFAULT NULL,
  `hinhAnh` varchar(255) DEFAULT NULL,
  `moTa` text DEFAULT NULL,
  `kieuDang` varchar(50) DEFAULT NULL,
  `tinhTrang` varchar(20) DEFAULT NULL,
  `nhienLieu` varchar(30) DEFAULT NULL,
  `soCho` int(11) DEFAULT NULL,
  `hopSo` varchar(30) DEFAULT NULL,
  `namSanXuat` int(11) DEFAULT NULL,
  `mauXe` varchar(50) DEFAULT NULL,
  `soKmDaDi` int(11) DEFAULT NULL,
  `xuatXu` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`maXe`),
  KEY `fk_xe_hang` (`maHang`),
  CONSTRAINT `fk_xe_hang` FOREIGN KEY (`maHang`) REFERENCES `hangxe` (`maHang`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_xe_tinhtrang` CHECK (`tinhTrang` is null or `tinhTrang` in ('Cũ','Mới','Lướt')),
  CONSTRAINT `chk_xe_nhienlieu` CHECK (`nhienLieu` is null or `nhienLieu` in ('Xăng','Dầu (Diesel)','Điện','Hybrid')),
  CONSTRAINT `chk_xe_hopso` CHECK (`hopSo` is null or `hopSo` in ('Tự động','Số sàn'))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `xe_hinhanh` (
  `maHinhAnh` int(11) NOT NULL AUTO_INCREMENT,
  `maXe` int(11) NOT NULL,
  `duongDan` varchar(500) NOT NULL,
  `thuTu` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`maHinhAnh`),
  UNIQUE KEY `uq_xehinhanh_thutu` (`maXe`,`thuTu`),
  CONSTRAINT `fk_xehinhanh_xe` FOREIGN KEY (`maXe`) REFERENCES `xe` (`maXe`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `yeucaumuaxe` (
  `maYeuCau` int(11) NOT NULL AUTO_INCREMENT,
  `maNguoiMua` int(11) NOT NULL,
  `maNguoiBan` int(11) NOT NULL,
  `maXe` int(11) NOT NULL,
  `maHoiThoai` int(11) DEFAULT NULL,
  `giaThamKhao` double NOT NULL DEFAULT 0,
  `ghiChuNguoiMua` text DEFAULT NULL,
  `trangThai` varchar(50) NOT NULL DEFAULT 'CHO_LIEN_HE',
  `ngayTao` datetime NOT NULL DEFAULT current_timestamp(),
  `ngayCapNhat` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`maYeuCau`),
  KEY `fk_yeucaumuaxe_xe` (`maXe`),
  KEY `fk_yeucaumuaxe_hoithoai` (`maHoiThoai`),
  KEY `idx_yeucau_buyer_created` (`maNguoiMua`,`ngayTao`),
  KEY `idx_yeucau_seller_created` (`maNguoiBan`,`ngayTao`),
  CONSTRAINT `fk_yeucaumuaxe_hoithoai` FOREIGN KEY (`maHoiThoai`) REFERENCES `hoithoai` (`maHoiThoai`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_yeucaumuaxe_nguoiban` FOREIGN KEY (`maNguoiBan`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_yeucaumuaxe_nguoimua` FOREIGN KEY (`maNguoiMua`) REFERENCES `nguoidung` (`maNguoiDung`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_yeucaumuaxe_xe` FOREIGN KEY (`maXe`) REFERENCES `xe` (`maXe`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_yeucaumuaxe_trangthai` CHECK (`trangThai` is null or `trangThai` in ('CHO_LIEN_HE','DA_LIEN_HE','DA_XEM_XE','DA_CHOT','HOAN_TAT','DA_HUY'))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS=1;
