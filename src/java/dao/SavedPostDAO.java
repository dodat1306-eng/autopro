package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.BaiDang;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavedPostDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(SavedPostDAO.class.getName());

    public boolean isSaved(int maNguoiDung, int maBaiDang) {
        String sql = "SELECT 1 FROM baidang_yeuthich WHERE maNguoiDung = ? AND maBaiDang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, maBaiDang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean save(int maNguoiDung, int maBaiDang) {
        if (isSaved(maNguoiDung, maBaiDang)) {
            return true;
        }
        String sql = "INSERT INTO baidang_yeuthich(maNguoiDung, maBaiDang) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, maBaiDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean unsave(int maNguoiDung, int maBaiDang) {
        String sql = "DELETE FROM baidang_yeuthich WHERE maNguoiDung = ? AND maBaiDang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, maBaiDang);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public Set<Integer> getSavedPostIds(int maNguoiDung) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT maBaiDang FROM baidang_yeuthich WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("maBaiDang"));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return ids;
    }

    public List<BaiDang> getSavedPostsByUser(int maNguoiDung) {
        List<BaiDang> list = new ArrayList<>();
        String sql = "SELECT b.maBaiDang, b.maNguoiDung, b.maXe, COALESCE(b.maTinh, 0) AS maTinh, b.tieuDe, b.moTa, "
                + "CASE "
                + "WHEN b.trangThai = 'da_ban' OR sold.maXe IS NOT NULL THEN 'da_ban' "
                + "WHEN b.trangThai = 'dang_giao_dich' OR active_tx.maXe IS NOT NULL THEN 'dang_giao_dich' "
                + "ELSE b.trangThai END AS trangThai, "
                + "b.ghiChuAdmin, b.ngayDang, x.tenXe, x.gia, x.hinhAnh AS anhDaiDien, "
                + "tt.tenTinh, tt.mien, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang "
                + "FROM baidang_yeuthich y "
                + "JOIN baidang b ON b.maBaiDang = y.maBaiDang "
                + "JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai = 'HOAN_TAT') sold ON sold.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai NOT IN ('HOAN_TAT', 'HUY', 'MOI_TAO')) active_tx ON active_tx.maXe = b.maXe "
                + "LEFT JOIN tinhthanh tt ON tt.maTinh = b.maTinh "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE y.maNguoiDung = ? "
                + "AND b.trangThai <> 'da_ban' "
                + "AND sold.maXe IS NULL "
                + "AND b.trangThai IN ('da_duyet', 'dang_giao_dich') "
                + "ORDER BY y.ngayLuu DESC, b.ngayDang DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BaiDang baiDang = new BaiDang();
                    baiDang.setMaBaiDang(rs.getInt("maBaiDang"));
                    baiDang.setMaNguoiDung(rs.getInt("maNguoiDung"));
                    baiDang.setMaXe(rs.getInt("maXe"));
                    baiDang.setMaTinh(rs.getInt("maTinh"));
                    baiDang.setTieuDe(rs.getString("tieuDe"));
                    baiDang.setMoTa(rs.getString("moTa"));
                    baiDang.setTrangThai(rs.getString("trangThai"));
                    baiDang.setGhiChuAdmin(rs.getString("ghiChuAdmin"));
                    baiDang.setNgayDang(rs.getTimestamp("ngayDang"));
                    baiDang.setTenXe(rs.getString("tenXe"));
                    baiDang.setGiaBan(rs.getDouble("gia"));
                    baiDang.setAnhDaiDien(rs.getString("anhDaiDien"));
                    baiDang.setTenTinh(rs.getString("tenTinh"));
                    baiDang.setMien(rs.getString("mien"));
                    baiDang.setTenNguoiDang(rs.getString("tenNguoiDang"));
                    baiDang.setEmailNguoiDang(rs.getString("emailNguoiDang"));
                    list.add(baiDang);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }
}
