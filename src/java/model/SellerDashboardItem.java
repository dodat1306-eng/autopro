package model;

import java.sql.Timestamp;

public class SellerDashboardItem {

    private int maBaiDang;
    private int maXe;
    private String tieuDe;
    private String trangThai;
    private String ghiChuAdmin;
    private Timestamp ngayDang;
    private String tenXe;
    private double giaBan;
    private String anhDaiDien;
    private int luotXem;
    private int soNguoiNhanTin;
    private int soYeuCau;
    private int soLichHen;
    private int soGiaoDich;
    private int soGiaoDichHoanTat;
    private Timestamp ngayYeuCauMoiNhat;

    public int getMaBaiDang() {
        return maBaiDang;
    }

    public void setMaBaiDang(int maBaiDang) {
        this.maBaiDang = maBaiDang;
    }

    public int getMaXe() {
        return maXe;
    }

    public void setMaXe(int maXe) {
        this.maXe = maXe;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
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

    public Timestamp getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(Timestamp ngayDang) {
        this.ngayDang = ngayDang;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public int getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(int luotXem) {
        this.luotXem = luotXem;
    }

    public int getSoNguoiNhanTin() {
        return soNguoiNhanTin;
    }

    public void setSoNguoiNhanTin(int soNguoiNhanTin) {
        this.soNguoiNhanTin = soNguoiNhanTin;
    }

    public int getSoYeuCau() {
        return soYeuCau;
    }

    public void setSoYeuCau(int soYeuCau) {
        this.soYeuCau = soYeuCau;
    }

    public int getSoLichHen() {
        return soLichHen;
    }

    public void setSoLichHen(int soLichHen) {
        this.soLichHen = soLichHen;
    }

    public int getSoGiaoDich() {
        return soGiaoDich;
    }

    public void setSoGiaoDich(int soGiaoDich) {
        this.soGiaoDich = soGiaoDich;
    }

    public int getSoGiaoDichHoanTat() {
        return soGiaoDichHoanTat;
    }

    public void setSoGiaoDichHoanTat(int soGiaoDichHoanTat) {
        this.soGiaoDichHoanTat = soGiaoDichHoanTat;
    }

    public Timestamp getNgayYeuCauMoiNhat() {
        return ngayYeuCauMoiNhat;
    }

    public void setNgayYeuCauMoiNhat(Timestamp ngayYeuCauMoiNhat) {
        this.ngayYeuCauMoiNhat = ngayYeuCauMoiNhat;
    }

    public String getTrangThaiHienThi() {
        if ("da_duyet".equals(trangThai)) {
            return "Đang hiển thị";
        }
        if ("dang_giao_dich".equals(trangThai)) {
            return "Đang giao dịch";
        }
        if ("cho_duyet".equals(trangThai)) {
            return "Chờ admin duyệt";
        }
        if ("canh_bao".equals(trangThai)) {
            return "Bị cảnh báo";
        }
        if ("tu_choi".equals(trangThai)) {
            return "Bị từ chối";
        }
        if ("da_ban".equals(trangThai)) {
            return "Đã bán";
        }
        return trangThai == null || trangThai.isBlank() ? "Chưa cập nhật" : trangThai;
    }

    public String getTrangThaiCssClass() {
        if ("da_duyet".equals(trangThai)) {
            return "status-active";
        }
        if ("dang_giao_dich".equals(trangThai)) {
            return "status-trading";
        }
        if ("cho_duyet".equals(trangThai)) {
            return "status-pending";
        }
        if ("canh_bao".equals(trangThai)) {
            return "status-warning";
        }
        if ("tu_choi".equals(trangThai)) {
            return "status-rejected";
        }
        if ("da_ban".equals(trangThai)) {
            return "status-sold";
        }
        return "status-muted";
    }

    public int getDiemQuanTam() {
        return luotXem + (soNguoiNhanTin * 3) + (soYeuCau * 4) + (soLichHen * 5);
    }
}
