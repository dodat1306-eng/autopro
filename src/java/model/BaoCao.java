package model;

import java.sql.Timestamp;

public class BaoCao {

    private int maBaoCao;
    private int maBaiDang;
    private int maNguoiBaoCao;
    private String lyDo;
    private String chiTiet;
    private String trangThai;
    private String ghiChuAdmin;
    private Integer maAdminXuLy;
    private Timestamp ngayBaoCao;
    private Timestamp ngayXuLy;
    private String tieuDeBaiDang;
    private String tenXe;
    private String tenNguoiBaoCao;
    private String emailNguoiBaoCao;
    private Integer maNguoiDang;
    private String tenNguoiDang;
    private String emailNguoiDang;
    private String trangThaiBaiDang;

    public int getMaBaoCao() {
        return maBaoCao;
    }

    public void setMaBaoCao(int maBaoCao) {
        this.maBaoCao = maBaoCao;
    }

    public int getMaBaiDang() {
        return maBaiDang;
    }

    public void setMaBaiDang(int maBaiDang) {
        this.maBaiDang = maBaiDang;
    }

    public int getMaNguoiBaoCao() {
        return maNguoiBaoCao;
    }

    public void setMaNguoiBaoCao(int maNguoiBaoCao) {
        this.maNguoiBaoCao = maNguoiBaoCao;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChuAdmin() {
        return ghiChuAdmin;
    }

    public void setGhiChuAdmin(String ghiChuAdmin) {
        this.ghiChuAdmin = ghiChuAdmin;
    }

    public Integer getMaAdminXuLy() {
        return maAdminXuLy;
    }

    public void setMaAdminXuLy(Integer maAdminXuLy) {
        this.maAdminXuLy = maAdminXuLy;
    }

    public Timestamp getNgayBaoCao() {
        return ngayBaoCao;
    }

    public void setNgayBaoCao(Timestamp ngayBaoCao) {
        this.ngayBaoCao = ngayBaoCao;
    }

    public Timestamp getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(Timestamp ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
    }

    public String getTieuDeBaiDang() {
        return tieuDeBaiDang;
    }

    public void setTieuDeBaiDang(String tieuDeBaiDang) {
        this.tieuDeBaiDang = tieuDeBaiDang;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public String getTenNguoiBaoCao() {
        return tenNguoiBaoCao;
    }

    public void setTenNguoiBaoCao(String tenNguoiBaoCao) {
        this.tenNguoiBaoCao = tenNguoiBaoCao;
    }

    public String getEmailNguoiBaoCao() {
        return emailNguoiBaoCao;
    }

    public void setEmailNguoiBaoCao(String emailNguoiBaoCao) {
        this.emailNguoiBaoCao = emailNguoiBaoCao;
    }

    public Integer getMaNguoiDang() {
        return maNguoiDang;
    }

    public void setMaNguoiDang(Integer maNguoiDang) {
        this.maNguoiDang = maNguoiDang;
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

    public String getTrangThaiBaiDang() {
        return trangThaiBaiDang;
    }

    public void setTrangThaiBaiDang(String trangThaiBaiDang) {
        this.trangThaiBaiDang = trangThaiBaiDang;
    }

    public boolean isBaiDangDaBan() {
        return trangThaiBaiDang != null && "da_ban".equalsIgnoreCase(trangThaiBaiDang.trim());
    }

    public boolean isBaiDangDangGiaoDich() {
        return trangThaiBaiDang != null && "dang_giao_dich".equalsIgnoreCase(trangThaiBaiDang.trim());
    }

    public boolean isBaiDangBiKhoaGiaoDich() {
        return isBaiDangDaBan() || isBaiDangDangGiaoDich();
    }
}
