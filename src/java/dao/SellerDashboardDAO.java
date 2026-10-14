package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.SellerDashboardItem;
import model.SellerDashboardSummary;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SellerDashboardDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(SellerDashboardDAO.class.getName());

    private SellerDashboardItem mapItem(ResultSet rs) throws Exception {
        SellerDashboardItem item = new SellerDashboardItem();
        item.setMaBaiDang(rs.getInt("maBaiDang"));
        item.setMaXe(rs.getInt("maXe"));
        item.setTieuDe(rs.getString("tieuDe"));
        item.setTrangThai(rs.getString("trangThai"));
        item.setGhiChuAdmin(rs.getString("ghiChuAdmin"));
        item.setNgayDang(rs.getTimestamp("ngayDang"));
        item.setTenXe(rs.getString("tenXe"));
        item.setGiaBan(rs.getDouble("gia"));
        item.setAnhDaiDien(rs.getString("anhDaiDien"));
        item.setLuotXem(rs.getInt("luotXem"));
        item.setSoNguoiNhanTin(rs.getInt("soNguoiNhanTin"));
        item.setSoYeuCau(rs.getInt("soYeuCau"));
        item.setSoLichHen(rs.getInt("soLichHen"));
        item.setSoGiaoDich(rs.getInt("soGiaoDich"));
        item.setSoGiaoDichHoanTat(rs.getInt("soGiaoDichHoanTat"));
        item.setNgayYeuCauMoiNhat(rs.getTimestamp("ngayYeuCauMoiNhat"));
        return item;
    }

    public List<SellerDashboardItem> getItemsBySeller(int maNguoiBan) {
        List<SellerDashboardItem> items = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String viewExpression = hasColumn(conn, "baidang", "luotXem")
                    ? "COALESCE(b.luotXem, 0)"
                    : "0";
            String sql = "SELECT b.maBaiDang, b.maXe, b.tieuDe, b.trangThai, b.ghiChuAdmin, b.ngayDang, "
                    + viewExpression + " AS luotXem, "
                    + "x.tenXe, x.gia, "
                    + "COALESCE((SELECT xh.duongDan FROM xe_hinhanh xh WHERE xh.maXe = b.maXe ORDER BY xh.thuTu ASC, xh.maHinhAnh ASC LIMIT 1), x.hinhAnh) AS anhDaiDien, "
                    + "COALESCE(ht.soNguoiNhanTin, 0) AS soNguoiNhanTin, "
                    + "COALESCE(yc.soYeuCau, 0) AS soYeuCau, "
                    + "yc.ngayYeuCauMoiNhat, "
                    + "COALESCE(lh.soLichHen, 0) AS soLichHen, "
                    + "COALESCE(gd.soGiaoDich, 0) AS soGiaoDich, "
                    + "COALESCE(gd.soGiaoDichHoanTat, 0) AS soGiaoDichHoanTat "
                    + "FROM baidang b "
                    + "LEFT JOIN xe x ON x.maXe = b.maXe "
                    + "LEFT JOIN ("
                    + "    SELECT maXe, maNguoiBan, COUNT(DISTINCT maNguoiMua) AS soNguoiNhanTin "
                    + "    FROM hoithoai GROUP BY maXe, maNguoiBan"
                    + ") ht ON ht.maXe = b.maXe AND ht.maNguoiBan = b.maNguoiDung "
                    + "LEFT JOIN ("
                    + "    SELECT maXe, maNguoiBan, COUNT(*) AS soYeuCau, MAX(ngayTao) AS ngayYeuCauMoiNhat "
                    + "    FROM yeucaumuaxe GROUP BY maXe, maNguoiBan"
                    + ") yc ON yc.maXe = b.maXe AND yc.maNguoiBan = b.maNguoiDung "
                    + "LEFT JOIN ("
                    + "    SELECT y.maXe, y.maNguoiBan, COUNT(DISTINCT l.maLichHen) AS soLichHen "
                    + "    FROM yeucaumuaxe y "
                    + "    JOIN lichhenxemxe l ON l.maYeuCau = y.maYeuCau "
                    + "    WHERE l.thoiGianHen IS NOT NULL OR l.diaDiemHen IS NOT NULL OR l.trangThai IN ('DA_LIEN_HE', 'DA_XEM_XE', 'DA_CHOT') "
                    + "    GROUP BY y.maXe, y.maNguoiBan"
                    + ") lh ON lh.maXe = b.maXe AND lh.maNguoiBan = b.maNguoiDung "
                    + "LEFT JOIN ("
                    + "    SELECT maXe, maNguoiBan, COUNT(*) AS soGiaoDich, "
                    + "           SUM(CASE WHEN trangThai = 'HOAN_TAT' THEN 1 ELSE 0 END) AS soGiaoDichHoanTat "
                    + "    FROM giaodichxe GROUP BY maXe, maNguoiBan"
                    + ") gd ON gd.maXe = b.maXe AND gd.maNguoiBan = b.maNguoiDung "
                    + "WHERE b.maNguoiDung = ? "
                    + "ORDER BY b.ngayDang DESC, b.maBaiDang DESC";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maNguoiBan);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(mapItem(rs));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        return items;
    }

    public SellerDashboardSummary buildSummary(List<SellerDashboardItem> items) {
        SellerDashboardSummary summary = new SellerDashboardSummary();
        if (items == null) {
            return summary;
        }
        for (SellerDashboardItem item : items) {
            summary.addItem(item);
        }
        return summary;
    }
}
