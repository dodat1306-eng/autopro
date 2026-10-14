package model;

import java.sql.Timestamp;
import java.util.Locale;

public class NguoiDung {

    private int maNguoiDung;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private String vaiTro;
    private String trangThai;
    private String anhDaiDien;
    private String otpReset;
    private Timestamp otpHetHan;

    public NguoiDung() {
    }

    public NguoiDung(int maNguoiDung, String hoTen, String email, String soDienThoai,
            String matKhau, String vaiTro, String trangThai) {
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getVaiTroNormalized() {
        String normalized = vaiTro == null ? "" : vaiTro.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || "user".equals(normalized)) {
            return "buyer";
        }
        return normalized;
    }

    public boolean isAdmin() {
        return "admin".equals(getVaiTroNormalized());
    }

    public boolean isSeller() {
        return "seller".equals(getVaiTroNormalized());
    }

    public boolean isBuyer() {
        return "buyer".equals(getVaiTroNormalized());
    }

    public boolean isThanhVien() {
        return isBuyer() || isSeller();
    }

    public String getVaiTroHienThi() {
        if (isAdmin()) {
            return "Admin";
        }
        if (isSeller()) {
            return "Seller";
        }
        return "Buyer";
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public String getOtpReset() {
        return otpReset;
    }

    public void setOtpReset(String otpReset) {
        this.otpReset = otpReset;
    }

    public Timestamp getOtpHetHan() {
        return otpHetHan;
    }

    public void setOtpHetHan(Timestamp otpHetHan) {
        this.otpHetHan = otpHetHan;
    }
}
