package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.HangXe;

public class HangXeDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(HangXeDAO.class.getName());
    private static volatile List<HangXe> hangXeCache;

    private static void invalidateCache() {
        hangXeCache = null;
    }

    private HangXe mapHangXe(ResultSet rs) throws Exception {
        return new HangXe(
                rs.getInt("maHang"),
                rs.getString("tenHang"),
                rs.getString("slug"),
                rs.getString("logoUrl"),
                rs.getString("tuKhoaDongBo"),
                rs.getInt("thuTuSapXep")
        );
    }

    public List<HangXe> getAllHangXe() {
        List<HangXe> cachedList = hangXeCache;
        if (cachedList != null) {
            return new ArrayList<>(cachedList);
        }

        List<HangXe> list = new ArrayList<>();
        String sql = "SELECT maHang, tenHang, slug, logoUrl, tuKhoaDongBo, thuTuSapXep "
                + "FROM hangxe ORDER BY thuTuSapXep ASC, tenHang ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapHangXe(rs));
            }
            hangXeCache = new ArrayList<>(list);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot load brand list: {0}", e.getMessage());
        }
        return list;
    }

    public boolean exists(int maHang) {
        String sql = "SELECT 1 FROM hangxe WHERE maHang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot check brand existence: {0}", e.getMessage());
        }
        return false;
    }

    public int countCarsByHang(int maHang) {
        String sql = "SELECT COUNT(*) FROM xe WHERE maHang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot count cars by brand: {0}", e.getMessage());
        }
        return 0;
    }

    public boolean insert(HangXe hangXe) {
        String sql = "INSERT INTO hangxe(maHang, tenHang, slug, logoUrl, tuKhoaDongBo, thuTuSapXep) "
                + "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hangXe.getMaHang());
            ps.setString(2, hangXe.getTenHang());
            ps.setString(3, hangXe.getSlug());
            ps.setString(4, hangXe.getLogoUrl());
            ps.setString(5, hangXe.getTuKhoaDongBo());
            ps.setInt(6, hangXe.getThuTuSapXep());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot insert brand: {0}", e.getMessage());
        }
        return false;
    }

    public boolean update(HangXe hangXe) {
        String sql = "UPDATE hangxe SET tenHang = ?, slug = ?, logoUrl = ?, tuKhoaDongBo = ?, thuTuSapXep = ? "
                + "WHERE maHang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hangXe.getTenHang());
            ps.setString(2, hangXe.getSlug());
            ps.setString(3, hangXe.getLogoUrl());
            ps.setString(4, hangXe.getTuKhoaDongBo());
            ps.setInt(5, hangXe.getThuTuSapXep());
            ps.setInt(6, hangXe.getMaHang());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot update brand: {0}", e.getMessage());
        }
        return false;
    }

    public boolean delete(int maHang) {
        String sql = "DELETE FROM hangxe WHERE maHang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHang);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot delete brand: {0}", e.getMessage());
        }
        return false;
    }
}
