package model;

import java.sql.Timestamp;

public class BaiDang {

    private int maBaiDang;
    private int maNguoiDung;
    private int maXe;
    private int maTinh;
    private String tieuDe;
    private String moTa;
    private String trangThai;
    private String ghiChuAdmin;
    private Timestamp ngayDang;
    private double giaBan;
    private String tenXe;
    private String tenNguoiDang;
    private String emailNguoiDang;
    private String soDienThoaiNguoiDang;
    private String anhDaiDien;
    private String tenTinh;
    private String mien;
    private int luotXem;
    private Integer maGiaoDich;
    private String tenHang;
    private Integer namSanXuat;
    private String tinhTrangXe;
    private String nhienLieu;
    private String hopSo;
    private Integer soKmDaDi;
    private Integer soCho;
    private String xuatXu;
    private String kieuDang;
    private String mauXe;

    public BaiDang() {
    }

    public BaiDang(int maBaiDang, int maNguoiDung, int maXe, int maTinh,
            String tieuDe, String moTa, String trangThai, String ghiChuAdmin,
            Timestamp ngayDang, double giaBan, String tenXe) {
        this.maBaiDang = maBaiDang;
        this.maNguoiDung = maNguoiDung;
        this.maXe = maXe;
        this.maTinh = maTinh;
        this.tieuDe = tieuDe;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.ghiChuAdmin = ghiChuAdmin;
        this.ngayDang = ngayDang;
        this.giaBan = giaBan;
        this.tenXe = tenXe;
    }

    public int getMaBaiDang() {
        return maBaiDang;
    }

    public void setMaBaiDang(int maBaiDang) {
        this.maBaiDang = maBaiDang;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public int getMaTinh() {
        return maTinh;
    }

    public void setMaTinh(int maTinh) {
        this.maTinh = maTinh;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isDaBan() {
        return "da_ban".equalsIgnoreCase(normalizedTrangThai());
    }

    public boolean isDangGiaoDich() {
        return "dang_giao_dich".equalsIgnoreCase(normalizedTrangThai());
    }

    public boolean isBiKhoaGiaoDich() {
        return isDaBan() || isDangGiaoDich();
    }

    private String normalizedTrangThai() {
        return trangThai == null ? "" : trangThai.trim();
    }

    public String getGhiChuAdmin() {
        return ghiChuAdmin;
    }

    public void setGhiChuAdmin(String ghiChuAdmin) {
        this.ghiChuAdmin = ghiChuAdmin;
    }

    public Timestamp getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(Timestamp ngayDang) {
        this.ngayDang = ngayDang;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getTenNguoiDang() {
        return tenNguoiDang;
    }

    public void setTenNguoiDang(String tenNguoiDang) {
        this.tenNguoiDang = tenNguoiDang;
    }

    public String getEmailNguoiDang() {
        return emailNguoiDang;
    }

    public void setEmailNguoiDang(String emailNguoiDang) {
        this.emailNguoiDang = emailNguoiDang;
    }

    public String getSoDienThoaiNguoiDang() {
        return soDienThoaiNguoiDang;
    }

    public void setSoDienThoaiNguoiDang(String soDienThoaiNguoiDang) {
        this.soDienThoaiNguoiDang = soDienThoaiNguoiDang;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
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

    public int getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(int luotXem) {
        this.luotXem = luotXem;
    }

    public Integer getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(Integer maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public Integer getNamSanXuat() {
        return namSanXuat;
    }

    public void setNamSanXuat(Integer namSanXuat) {
        this.namSanXuat = namSanXuat;
    }

    public String getTinhTrangXe() {
        return tinhTrangXe;
    }

    public void setTinhTrangXe(String tinhTrangXe) {
        this.tinhTrangXe = tinhTrangXe;
    }

    public String getNhienLieu() {
        return nhienLieu;
    }

    public void setNhienLieu(String nhienLieu) {
        this.nhienLieu = nhienLieu;
    }

    public String getHopSo() {
        return hopSo;
    }

    public void setHopSo(String hopSo) {
        this.hopSo = hopSo;
    }

    public Integer getSoKmDaDi() {
        return soKmDaDi;
    }

    public void setSoKmDaDi(Integer soKmDaDi) {
        this.soKmDaDi = soKmDaDi;
    }

    public Integer getSoCho() {
        return soCho;
    }

    public void setSoCho(Integer soCho) {
        this.soCho = soCho;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public String getKieuDang() {
        return kieuDang;
    }

    public void setKieuDang(String kieuDang) {
        this.kieuDang = kieuDang;
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        this.mauXe = mauXe;
    }
}
