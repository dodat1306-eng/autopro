package model;

import java.sql.Timestamp;

public class YeuCauKetNoi {

    private int maYeuCau;
    private int maNguoiMua;
    private Integer maNguoiBan;
    private Integer maLichHen;
    private Timestamp ngayTao;
    private double giaThamKhao;
    private String trangThai;
    private Integer maXe;
    private String tenXe;
    private String tenNguoiMua;
    private String emailNguoiMua;
    private String soDienThoaiNguoiMua;
    private String tenNguoiBan;
    private String emailNguoiBan;
    private String soDienThoaiNguoiBan;
    private Integer maHoiThoai;
    private String ghiChuNguoiMua;
    private Timestamp thoiGianHen;
    private String diaDiemHen;
    private String ghiChuHen;

    public YeuCauKetNoi() {
    }

    public YeuCauKetNoi(int maYeuCau, int maNguoiMua, Timestamp ngayTao, double giaThamKhao, String trangThai) {
        this.maYeuCau = maYeuCau;
        this.maNguoiMua = maNguoiMua;
        this.ngayTao = ngayTao;
        this.giaThamKhao = giaThamKhao;
        this.trangThai = trangThai;
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

    public Integer getMaNguoiBan() {
        return maNguoiBan;
    }

    public void setMaNguoiBan(Integer maNguoiBan) {
        this.maNguoiBan = maNguoiBan;
    }

    public Integer getMaLichHen() {
        return maLichHen;
    }

    public void setMaLichHen(Integer maLichHen) {
        this.maLichHen = maLichHen;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public double getGiaThamKhao() {
        return giaThamKhao;
    }

    public void setGiaThamKhao(double giaThamKhao) {
        this.giaThamKhao = giaThamKhao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getMaXe() {
        return maXe;
    }

    public void setMaXe(Integer maXe) {
        this.maXe = maXe;
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

    public Integer getMaHoiThoai() {
        return maHoiThoai;
    }

    public void setMaHoiThoai(Integer maHoiThoai) {
        this.maHoiThoai = maHoiThoai;
    }

    public String getGhiChuNguoiMua() {
        return ghiChuNguoiMua;
    }

    public void setGhiChuNguoiMua(String ghiChuNguoiMua) {
        this.ghiChuNguoiMua = ghiChuNguoiMua;
    }

    public Timestamp getThoiGianHen() {
        return thoiGianHen;
    }

    public void setThoiGianHen(Timestamp thoiGianHen) {
        this.thoiGianHen = thoiGianHen;
    }

    public String getDiaDiemHen() {
        return diaDiemHen;
    }

    public void setDiaDiemHen(String diaDiemHen) {
        this.diaDiemHen = diaDiemHen;
    }

    public String getGhiChuHen() {
        return ghiChuHen;
    }

    public void setGhiChuHen(String ghiChuHen) {
        this.ghiChuHen = ghiChuHen;
    }
}
