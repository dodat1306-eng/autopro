package model;

import java.sql.Timestamp;

public class DangKySeller {

    private int maDangKySeller;
    private int maNguoiDung;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String soCCCD;
    private String anhCCCDMatTruoc;
    private String anhCCCDMatSau;
    private int maTinh;
    private String tenTinh;
    private String mien;
    private String diaChiChiTiet;
    private String ghiChuNguoiDangKy;
    private String trangThai;
    private String lyDoTuChoi;
    private Integer nguoiPheDuyet;
    private String tenNguoiPheDuyet;
    private Timestamp ngayPheDuyet;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;
    private String tenNguoiDung;
    private String emailNguoiDung;

    public int getMaDangKySeller() {
        return maDangKySeller;
    }

    public void setMaDangKySeller(int maDangKySeller) {
        this.maDangKySeller = maDangKySeller;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getAnhCCCDMatTruoc() {
        return anhCCCDMatTruoc;
    }

    public void setAnhCCCDMatTruoc(String anhCCCDMatTruoc) {
        this.anhCCCDMatTruoc = anhCCCDMatTruoc;
    }

    public String getAnhCCCDMatSau() {
        return anhCCCDMatSau;
    }

    public void setAnhCCCDMatSau(String anhCCCDMatSau) {
        this.anhCCCDMatSau = anhCCCDMatSau;
    }

    public int getMaTinh() {
        return maTinh;
    }

    public void setMaTinh(int maTinh) {
        this.maTinh = maTinh;
    }

    public String getTenTinh() {
        return tenTinh;
    }

    public void setTenTinh(String tenTinh) {
        this.tenTinh = tenTinh;
    }

    public String getMien() {
        return mien;
    }

    public void setMien(String mien) {
        this.mien = mien;
    }

    public String getDiaChiChiTiet() {
        return diaChiChiTiet;
    }

    public void setDiaChiChiTiet(String diaChiChiTiet) {
        this.diaChiChiTiet = diaChiChiTiet;
    }

    public String getGhiChuNguoiDangKy() {
        return ghiChuNguoiDangKy;
    }

    public void setGhiChuNguoiDangKy(String ghiChuNguoiDangKy) {
        this.ghiChuNguoiDangKy = ghiChuNguoiDangKy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoTuChoi() {
        return lyDoTuChoi;
    }

    public void setLyDoTuChoi(String lyDoTuChoi) {
        this.lyDoTuChoi = lyDoTuChoi;
    }

    public Integer getNguoiPheDuyet() {
        return nguoiPheDuyet;
    }

    public void setNguoiPheDuyet(Integer nguoiPheDuyet) {
        this.nguoiPheDuyet = nguoiPheDuyet;
    }

    public String getTenNguoiPheDuyet() {
        return tenNguoiPheDuyet;
    }

    public void setTenNguoiPheDuyet(String tenNguoiPheDuyet) {
        this.tenNguoiPheDuyet = tenNguoiPheDuyet;
    }

    public Timestamp getNgayPheDuyet() {
        return ngayPheDuyet;
    }

    public void setNgayPheDuyet(Timestamp ngayPheDuyet) {
        this.ngayPheDuyet = ngayPheDuyet;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getTenNguoiDung() {
        return tenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
    }

    public String getEmailNguoiDung() {
        return emailNguoiDung;
    }

    public void setEmailNguoiDung(String emailNguoiDung) {
        this.emailNguoiDung = emailNguoiDung;
    }

    public boolean isChoDuyet() {
        return "CHO_DUYET".equals(trangThai);
    }

    public boolean isDaDuyet() {
        return "DA_DUYET".equals(trangThai);
    }

    public boolean isTuChoi() {
        return "TU_CHOI".equals(trangThai);
    }

    public boolean isDaXoa() {
        return "DA_XOA".equals(trangThai);
    }

    public String getTrangThaiHienThi() {
        if (isChoDuyet()) {
            return "Chờ duyệt";
        }
        if (isDaDuyet()) {
            return "Đã duyệt";
        }
        if (isTuChoi()) {
            return "Từ chối";
        }
        if (isDaXoa()) {
            return "Đã xóa";
        }
        return trangThai == null || trangThai.isBlank() ? "Chưa cập nhật" : trangThai;
    }

    public String getTrangThaiCssClass() {
        if (isChoDuyet()) {
            return "status-pending";
        }
        if (isDaDuyet()) {
            return "status-approved";
        }
        if (isTuChoi()) {
            return "status-rejected";
        }
        if (isDaXoa()) {
            return "status-deleted";
        }
        return "status-default";
    }
}
