package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Xe {

    private int maXe;
    private int maHang;
    private String tenXe;
    private double gia;
    private String hinhAnh;
    private String moTa;
    private String kieuDang;
    private String tinhTrang;
    private String nhienLieu;
    private int soCho;
    private String hopSo;
    private int namSanXuat;
    private String mauXe;
    private int soKmDaDi;
    private String xuatXu;
    private Integer maNguoiBan;
    private Integer maTinh;
    private String tenTinh;
    private String mien;
    private Integer maBaiDang;
    private String tieuDeBaiDang;
    private String trangThaiBaiDang;
    private List<String> danhSachHinhAnh = new ArrayList<>();

    public Xe() {
    }

    public Xe(int maXe, int maHang, String tenXe, double gia, String hinhAnh, String moTa,
            String kieuDang, String tinhTrang, String nhienLieu, int soCho,
            String hopSo, int namSanXuat, String mauXe, int soKmDaDi, String xuatXu) {
        this.maXe = maXe;
        this.maHang = maHang;
        this.tenXe = tenXe;
        this.gia = gia;
        this.hinhAnh = hinhAnh;
        this.moTa = moTa;
        this.kieuDang = kieuDang;
        this.tinhTrang = tinhTrang;
        this.nhienLieu = nhienLieu;
        this.soCho = soCho;
        this.hopSo = hopSo;
        this.namSanXuat = namSanXuat;
        this.mauXe = mauXe;
        this.soKmDaDi = soKmDaDi;
        this.xuatXu = xuatXu;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public int getMaHang() {
        return maHang;
    }

    public void setMaHang(int maHang) {
        this.maHang = maHang;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public double getGia() {
        return (gia);
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public List<String> getDanhSachHinhAnh() {
        if ((danhSachHinhAnh == null || danhSachHinhAnh.isEmpty()) && hinhAnh != null && !hinhAnh.isBlank()) {
            setDanhSachHinhAnhFromCsv(hinhAnh);
        }
        if ((danhSachHinhAnh == null || danhSachHinhAnh.isEmpty()) && !getSeedImageFallback().isBlank()) {
            return Collections.singletonList(getSeedImageFallback());
        }
        return danhSachHinhAnh == null ? Collections.emptyList() : Collections.unmodifiableList(danhSachHinhAnh);
    }

    public void setDanhSachHinhAnh(List<String> danhSachHinhAnh) {
        this.danhSachHinhAnh = new ArrayList<>();
        if (danhSachHinhAnh != null) {
            for (String image : danhSachHinhAnh) {
                String normalized = image == null ? "" : image.trim();
                if (!normalized.isEmpty() && !this.danhSachHinhAnh.contains(normalized)) {
                    this.danhSachHinhAnh.add(normalizeSeedImage(normalized));
                }
            }
        }
        this.hinhAnh = this.danhSachHinhAnh.isEmpty() ? "" : this.danhSachHinhAnh.get(0);
    }

    public void setDanhSachHinhAnhFromCsv(String imageCsv) {
        List<String> images = new ArrayList<>();
        if (imageCsv != null && !imageCsv.isBlank()) {
            for (String image : imageCsv.split(",")) {
                String normalized = image == null ? "" : image.trim();
                if (!normalized.isEmpty() && !images.contains(normalized)) {
                    images.add(normalized);
                }
            }
        }
        setDanhSachHinhAnh(images);
    }

    public String getDanhSachHinhAnhCsv() {
        return String.join(",", getDanhSachHinhAnh());
    }

    public String getAnhDaiDien() {
        for (String image : getDanhSachHinhAnh()) {
            if (image != null && !image.isBlank()) {
                return normalizeSeedImage(image.trim());
            }
        }
        return "";
    }

    private String normalizeSeedImage(String image) {
        if (image == null || image.isBlank()) {
            return getSeedImageFallback();
        }
        String lowerImage = image.toLowerCase(Locale.ROOT);
        if (lowerImage.contains("a_toyota_camry") || lowerImage.contains("a%20toyota%20camry")) {
            return "uploads/cars/toyota-camry.jpg";
        }
        if (lowerImage.contains("2020_ford_ranger") || lowerImage.contains("2020%20ford%20ranger")) {
            return "uploads/cars/ford-ranger.jpg";
        }
        if (lowerImage.contains("mazda_cx5") || lowerImage.contains("mazda%20cx5")) {
            return "uploads/cars/mazda-cx5.jpg";
        }
        if (lowerImage.contains("bmw%203%20series") || lowerImage.contains("bmw_3_series")) {
            return "uploads/cars/bmw-320i.jpg";
        }
        if (lowerImage.contains("hyundai%20santa%20fe") || lowerImage.contains("hyundai_santa_fe")) {
            return "uploads/cars/hyundai-santafe.jpg";
        }
        if (lowerImage.contains("audi%20q5") || lowerImage.contains("audi_q5")) {
            return "uploads/cars/audi-q5.jpg";
        }
        if (lowerImage.contains("honda%20cr-v") || lowerImage.contains("honda_cr-v")) {
            return "uploads/cars/honda-crv.jpg";
        }
        if (lowerImage.contains("mercedes%20benz%20c%20class") || lowerImage.contains("mercedes_benz_c_class")) {
            return "uploads/cars/mercedes-c-class.jpg";
        }
        if (lowerImage.contains("toyota%20vios") || lowerImage.contains("toyota_vios")) {
            return "uploads/cars/toyota-vios.jpg";
        }
        if (lowerImage.contains("vinfast%20vf8") || lowerImage.contains("vinfast_vf8")) {
            return "uploads/cars/vinfast-vf8.jpg";
        }
        return image;
    }

    private String getSeedImageFallback() {
        if (tenXe == null || tenXe.isBlank()) {
            return "";
        }
        String lowerName = tenXe.toLowerCase(Locale.ROOT);
        if (lowerName.contains("toyota") && lowerName.contains("camry")) {
            return "uploads/cars/toyota-camry.jpg";
        }
        if (lowerName.contains("ford") && lowerName.contains("ranger")) {
            return "uploads/cars/ford-ranger.jpg";
        }
        if (lowerName.contains("mazda") && lowerName.contains("cx-5")) {
            return "uploads/cars/mazda-cx5.jpg";
        }
        if (lowerName.contains("bmw") && lowerName.contains("320")) {
            return "uploads/cars/bmw-320i.jpg";
        }
        if (lowerName.contains("hyundai") && lowerName.contains("santa")) {
            return "uploads/cars/hyundai-santafe.jpg";
        }
        if (lowerName.contains("audi") && lowerName.contains("q5")) {
            return "uploads/cars/audi-q5.jpg";
        }
        if (lowerName.contains("honda") && lowerName.contains("cr-v")) {
            return "uploads/cars/honda-crv.jpg";
        }
        if (lowerName.contains("mercedes-benz") && lowerName.contains("c200")) {
            return "uploads/cars/mercedes-c-class.jpg";
        }
        if (lowerName.contains("toyota") && lowerName.contains("vios")) {
            return "uploads/cars/toyota-vios.jpg";
        }
        if (lowerName.contains("vinfast") && lowerName.contains("vf8")) {
            return "uploads/cars/vinfast-vf8.jpg";
        }
        return "";
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getKieuDang() {
        return kieuDang;
    }

    public void setKieuDang(String kieuDang) {
        this.kieuDang = kieuDang;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getNhienLieu() {
        return nhienLieu;
    }

    public void setNhienLieu(String nhienLieu) {
        this.nhienLieu = nhienLieu;
    }

    public int getSoCho() {
        return soCho;
    }

    public void setSoCho(int soCho) {
        this.soCho = soCho;
    }

    public String getHopSo() {
        return hopSo;
    }

    public void setHopSo(String hopSo) {
        this.hopSo = hopSo;
    }

    public int getNamSanXuat() {
        return namSanXuat;
    }

    public void setNamSanXuat(int namSanXuat) {
        this.namSanXuat = namSanXuat;
    }

    public String getMauXe() {
        return mauXe;
    }

    public void setMauXe(String mauXe) {
        this.mauXe = mauXe;
    }

    public int getSoKmDaDi() {
        return soKmDaDi;
    }

    public void setSoKmDaDi(int soKmDaDi) {
        this.soKmDaDi = soKmDaDi;
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        this.xuatXu = xuatXu;
    }

    public Integer getMaNguoiBan() {
        return maNguoiBan;
    }

    public void setMaNguoiBan(Integer maNguoiBan) {
        this.maNguoiBan = maNguoiBan;
    }

    public Integer getMaTinh() {
        return maTinh;
    }

    public void setMaTinh(Integer maTinh) {
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

    public boolean isDaBan() {
        return trangThaiBaiDang != null && "da_ban".equalsIgnoreCase(trangThaiBaiDang.trim());
    }

    public boolean isDangGiaoDich() {
        return trangThaiBaiDang != null && "dang_giao_dich".equalsIgnoreCase(trangThaiBaiDang.trim());
    }

    public boolean isKhoaYeuCauMua() {
        return isDaBan() || isDangGiaoDich();
    }
}
