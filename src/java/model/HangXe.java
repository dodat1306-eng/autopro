package model;

public class HangXe {

    private int maHang;
    private String tenHang;
    private String slug;
    private String logoUrl;
    private String tuKhoaDongBo;
    private int thuTuSapXep;

    public HangXe() {
    }

    public HangXe(int maHang, String tenHang, String slug, String logoUrl,
            String tuKhoaDongBo, int thuTuSapXep) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.tuKhoaDongBo = tuKhoaDongBo;
        this.thuTuSapXep = thuTuSapXep;
    }

    public int getMaHang() {
        return maHang;
    }

    public void setMaHang(int maHang) {
        this.maHang = maHang;
    }

    public String getTenHang() {
        return tenHang;
    }

    public void setTenHang(String tenHang) {
        this.tenHang = tenHang;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getTuKhoaDongBo() {
        return tuKhoaDongBo;
    }

    public void setTuKhoaDongBo(String tuKhoaDongBo) {
        this.tuKhoaDongBo = tuKhoaDongBo;
    }

    public int getThuTuSapXep() {
        return thuTuSapXep;
    }

    public void setThuTuSapXep(int thuTuSapXep) {
        this.thuTuSapXep = thuTuSapXep;
    }
}
