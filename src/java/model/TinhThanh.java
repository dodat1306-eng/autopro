package model;

public class TinhThanh {

    private int maTinh;
    private String tenTinh;
    private String mien;

    public TinhThanh() {
    }

    public TinhThanh(int maTinh, String tenTinh, String mien) {
        this.maTinh = maTinh;
        this.tenTinh = tenTinh;
        this.mien = mien;
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
}
