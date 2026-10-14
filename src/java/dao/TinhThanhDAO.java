package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.TinhThanh;

public class TinhThanhDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(TinhThanhDAO.class.getName());
    private static volatile List<TinhThanh> tinhThanhCache;
    private static volatile List<String> mienCache;

    private static void invalidateCache() {
        tinhThanhCache = null;
        mienCache = null;
    }

    public List<TinhThanh> getAllTinhThanh() {
        List<TinhThanh> cachedList = tinhThanhCache;
        if (cachedList != null) {
            return new ArrayList<>(cachedList);
        }

        List<TinhThanh> list = new ArrayList<>();
        String sql = "SELECT maTinh, tenTinh, mien FROM tinhthanh ORDER BY tenTinh";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TinhThanh(
                        rs.getInt("maTinh"),
                        rs.getString("tenTinh"),
                        rs.getString("mien")
                ));
            }
            tinhThanhCache = new ArrayList<>(list);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot load province list: {0}", e.getMessage());
        }
        return list;
    }

    public List<String> getAllMien() {
        List<String> cachedList = mienCache;
        if (cachedList != null) {
            return new ArrayList<>(cachedList);
        }

        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT mien FROM tinhthanh "
                + "WHERE mien IS NOT NULL AND mien <> '' "
                + "ORDER BY FIELD(mien, 'Miền Bắc', 'Miền Trung', 'Tây Nguyên', 'Miền Nam'), mien";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("mien"));
            }
            mienCache = new ArrayList<>(list);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot load region list: {0}", e.getMessage());
        }
        return list;
    }

    public boolean exists(int maTinh) {
        String sql = "SELECT 1 FROM tinhthanh WHERE maTinh = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTinh);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot check province existence: {0}", e.getMessage());
        }
        return false;
    }

    public int countPostsByTinh(int maTinh) {
        String sql = "SELECT COUNT(*) FROM baidang WHERE maTinh = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTinh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot count posts by province: {0}", e.getMessage());
        }
        return 0;
    }

    public boolean insert(TinhThanh tinhThanh) {
        String sql = "INSERT INTO tinhthanh(maTinh, tenTinh, mien) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tinhThanh.getMaTinh());
            ps.setString(2, tinhThanh.getTenTinh());
            ps.setString(3, tinhThanh.getMien());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot insert province: {0}", e.getMessage());
        }
        return false;
    }

    public boolean update(TinhThanh tinhThanh) {
        String sql = "UPDATE tinhthanh SET tenTinh = ?, mien = ? WHERE maTinh = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tinhThanh.getTenTinh());
            ps.setString(2, tinhThanh.getMien());
            ps.setInt(3, tinhThanh.getMaTinh());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot update province: {0}", e.getMessage());
        }
        return false;
    }

    public boolean delete(int maTinh) {
        String sql = "DELETE FROM tinhthanh WHERE maTinh = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maTinh);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                invalidateCache();
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot delete province: {0}", e.getMessage());
        }
        return false;
    }
}
