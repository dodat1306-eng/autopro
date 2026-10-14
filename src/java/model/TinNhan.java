package model;

import java.sql.Timestamp;

public class TinNhan {

    private int maTinNhan;
    private int maHoiThoai;
    private int maNguoiGui;
    private String noiDung;
    private Timestamp thoiGian;
    private String trangThai;

    public TinNhan() {
    }

    public TinNhan(int maTinNhan, int maHoiThoai, int maNguoiGui, String noiDung, Timestamp thoiGian, String trangThai) {
        this.maTinNhan = maTinNhan;
        this.maHoiThoai = maHoiThoai;
        this.maNguoiGui = maNguoiGui;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
        this.trangThai = trangThai;
    }

    public int getMaTinNhan() {
        return maTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        this.maTinNhan = maTinNhan;
    }

    public int getMaHoiThoai() {
        return maHoiThoai;
    }

    public void setMaHoiThoai(int maHoiThoai) {
        this.maHoiThoai = maHoiThoai;
    }

    public int getMaNguoiGui() {
        return maNguoiGui;
    }

    public void setMaNguoiGui(int maNguoiGui) {
        this.maNguoiGui = maNguoiGui;
    }

    public int getNguoiGui() {
        return maNguoiGui;
    }

    public void setNguoiGui(int nguoiGui) {
        this.maNguoiGui = nguoiGui;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Timestamp getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Timestamp thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
