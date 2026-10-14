package model;

import java.sql.Timestamp;

public class GiaoDichXe {

    private int maGiaoDich;
    private int maYeuCau;
    private int maNguoiMua;
    private int maNguoiBan;
    private int maXe;
    private Double giaChot;
    private Double giaThamKhao;
    private Timestamp ngayChot;
    private String diaDiemGiaoXe;
    private String soDienThoaiGiaoXe;
    private Timestamp ngayGiaoXe;
    private String trangThai;
    private boolean xacNhanNguoiMua;
    private boolean xacNhanNguoiBan;
    private Timestamp thoiGianXacNhanMua;
    private Timestamp thoiGianXacNhanBan;
    private String ghiChuNguoiMua;
    private String ghiChuNguoiBan;
    private Integer adminXuLy;
    private Timestamp ngayXuLy;
    private String ghiChuAdmin;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;

    private String tenXe;
    private String tenNguoiMua;
    private String emailNguoiMua;
    private String soDienThoaiNguoiMua;
    private String tenNguoiBan;
    private String emailNguoiBan;
    private String soDienThoaiNguoiBan;
    private String trangThaiYeuCau;
    private Integer maHoiThoai;
    private Integer maBaiDang;
    private String tieuDeBaiDang;
    private String trangThaiBaiDang;

    public int getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(int maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public int getMaYeuCau() {
        return maYeuCau;
    }

    public void setMaYeuCau(int maYeuCau) {
        this.maYeuCau = maYeuCau;
    }

    public int getMaNguoiMua() {
        return maNguoiMua;
    }

    public void setMaNguoiMua(int maNguoiMua) {
        this.maNguoiMua = maNguoiMua;
    }

    public int getMaNguoiBan() {
        return maNguoiBan;
    }

    public void setMaNguoiBan(int maNguoiBan) {
        this.maNguoiBan = maNguoiBan;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public Double getGiaChot() {
        return giaChot;
    }

    public void setGiaChot(Double giaChot) {
        this.giaChot = giaChot;
    }

    public Double getGiaThamKhao() {
        return giaThamKhao;
    }

    public void setGiaThamKhao(Double giaThamKhao) {
        this.giaThamKhao = giaThamKhao;
    }

    public Timestamp getNgayChot() {
        return ngayChot;
    }

    public void setNgayChot(Timestamp ngayChot) {
        this.ngayChot = ngayChot;
    }

    public String getDiaDiemGiaoXe() {
        return diaDiemGiaoXe;
    }

    public void setDiaDiemGiaoXe(String diaDiemGiaoXe) {
        this.diaDiemGiaoXe = diaDiemGiaoXe;
    }

    public String getSoDienThoaiGiaoXe() {
        return soDienThoaiGiaoXe;
    }

    public void setSoDienThoaiGiaoXe(String soDienThoaiGiaoXe) {
        this.soDienThoaiGiaoXe = soDienThoaiGiaoXe;
    }

    public Timestamp getNgayGiaoXe() {
        return ngayGiaoXe;
    }

    public void setNgayGiaoXe(Timestamp ngayGiaoXe) {
        this.ngayGiaoXe = ngayGiaoXe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isXacNhanNguoiMua() {
        return xacNhanNguoiMua;
    }

    public void setXacNhanNguoiMua(boolean xacNhanNguoiMua) {
        this.xacNhanNguoiMua = xacNhanNguoiMua;
    }

    public boolean isXacNhanNguoiBan() {
        return xacNhanNguoiBan;
    }

    public void setXacNhanNguoiBan(boolean xacNhanNguoiBan) {
        this.xacNhanNguoiBan = xacNhanNguoiBan;
    }

    public Timestamp getThoiGianXacNhanMua() {
        return thoiGianXacNhanMua;
    }

    public void setThoiGianXacNhanMua(Timestamp thoiGianXacNhanMua) {
        this.thoiGianXacNhanMua = thoiGianXacNhanMua;
    }

    public Timestamp getThoiGianXacNhanBan() {
        return thoiGianXacNhanBan;
    }

    public void setThoiGianXacNhanBan(Timestamp thoiGianXacNhanBan) {
        this.thoiGianXacNhanBan = thoiGianXacNhanBan;
    }

    public String getGhiChuNguoiMua() {
        return ghiChuNguoiMua;
    }

    public void setGhiChuNguoiMua(String ghiChuNguoiMua) {
        this.ghiChuNguoiMua = ghiChuNguoiMua;
    }

    public String getGhiChuNguoiBan() {
        return ghiChuNguoiBan;
    }

    public void setGhiChuNguoiBan(String ghiChuNguoiBan) {
        this.ghiChuNguoiBan = ghiChuNguoiBan;
    }

    public Integer getAdminXuLy() {
        return adminXuLy;
    }

    public void setAdminXuLy(Integer adminXuLy) {
        this.adminXuLy = adminXuLy;
    }

    public Timestamp getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(Timestamp ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
    }

    public String getGhiChuAdmin() {
        return ghiChuAdmin;
    }

    public void setGhiChuAdmin(String ghiChuAdmin) {
        this.ghiChuAdmin = ghiChuAdmin;
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

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getTenNguoiMua() {
        return tenNguoiMua;
    }

    public void setTenNguoiMua(String tenNguoiMua) {
        this.tenNguoiMua = tenNguoiMua;
    }

    public String getEmailNguoiMua() {
        return emailNguoiMua;
    }

    public void setEmailNguoiMua(String emailNguoiMua) {
        this.emailNguoiMua = emailNguoiMua;
    }

    public String getSoDienThoaiNguoiMua() {
        return soDienThoaiNguoiMua;
    }

    public void setSoDienThoaiNguoiMua(String soDienThoaiNguoiMua) {
        this.soDienThoaiNguoiMua = soDienThoaiNguoiMua;
    }

    public String getTenNguoiBan() {
        return tenNguoiBan;
    }

    public void setTenNguoiBan(String tenNguoiBan) {
        this.tenNguoiBan = tenNguoiBan;
    }

    public String getEmailNguoiBan() {
        return emailNguoiBan;
    }

    public void setEmailNguoiBan(String emailNguoiBan) {
        this.emailNguoiBan = emailNguoiBan;
    }

    public String getSoDienThoaiNguoiBan() {
        return soDienThoaiNguoiBan;
    }

    public void setSoDienThoaiNguoiBan(String soDienThoaiNguoiBan) {
        this.soDienThoaiNguoiBan = soDienThoaiNguoiBan;
    }

    public String getTrangThaiYeuCau() {
        return trangThaiYeuCau;
    }

    public void setTrangThaiYeuCau(String trangThaiYeuCau) {
        this.trangThaiYeuCau = trangThaiYeuCau;
    }

    public Integer getMaHoiThoai() {
        return maHoiThoai;
    }

    public void setMaHoiThoai(Integer maHoiThoai) {
        this.maHoiThoai = maHoiThoai;
    }

    public Integer getMaBaiDang() {
        return maBaiDang;
    }

    public void setMaBaiDang(Integer maBaiDang) {
        this.maBaiDang = maBaiDang;
    }

    public String getTieuDeBaiDang() {
        return tieuDeBaiDang;
    }

    public void setTieuDeBaiDang(String tieuDeBaiDang) {
        this.tieuDeBaiDang = tieuDeBaiDang;
    }

    public String getTrangThaiBaiDang() {
        return trangThaiBaiDang;
    }

    public void setTrangThaiBaiDang(String trangThaiBaiDang) {
        this.trangThaiBaiDang = trangThaiBaiDang;
    }
}
