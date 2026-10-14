package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ThongBao;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThongBaoDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ThongBaoDAO.class.getName());

    private ThongBao mapThongBao(ResultSet rs) throws Exception {
        ThongBao thongBao = new ThongBao();
        thongBao.setMaThongBao(rs.getInt("maThongBao"));
        thongBao.setMaNguoiDung(rs.getInt("maNguoiDung"));
        thongBao.setLoai(rs.getString("loai"));
        thongBao.setTieuDe(rs.getString("tieuDe"));
        thongBao.setNoiDung(rs.getString("noiDung"));
        thongBao.setUrlDich(rs.getString("urlDich"));
        thongBao.setDaDoc(rs.getBoolean("daDoc"));
        thongBao.setNgayTao(rs.getTimestamp("ngayTao"));
        return thongBao;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public boolean createNotification(int maNguoiDung, String loai, String tieuDe, String noiDung, String urlDich) {
        String sql = "INSERT INTO thongbao(maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc, ngayTao) "
                + "VALUES(?, ?, ?, ?, ?, 0, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, trimToEmpty(loai));
            ps.setString(3, trimToEmpty(tieuDe));
            ps.setString(4, trimToEmpty(noiDung));
            if (trimToEmpty(urlDich).isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, trimToEmpty(urlDich));
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public void createNotificationForAdmins(String loai, String tieuDe, String noiDung, String urlDich) {
        String sql = "INSERT INTO thongbao(maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc, ngayTao) "
                + "SELECT maNguoiDung, ?, ?, ?, ?, 0, NOW() FROM nguoidung WHERE vaiTro = 'admin'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(loai));
            ps.setString(2, trimToEmpty(tieuDe));
            ps.setString(3, trimToEmpty(noiDung));
            if (trimToEmpty(urlDich).isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, trimToEmpty(urlDich));
            }
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
    }

    public int countUnread(int maNguoiDung) {
        String sql = "SELECT COUNT(*) FROM thongbao WHERE maNguoiDung = ? AND daDoc = 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public List<ThongBao> getRecentNotifications(int maNguoiDung, int limit) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM thongbao WHERE maNguoiDung = ? ORDER BY ngayTao DESC, maThongBao DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapThongBao(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<ThongBao> getAllNotifications(int maNguoiDung) {
        List<ThongBao> list = new ArrayList<>();
        String sql = "SELECT * FROM thongbao WHERE maNguoiDung = ? ORDER BY ngayTao DESC, maThongBao DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapThongBao(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public ThongBao getNotificationById(int maThongBao, int maNguoiDung) {
        String sql = "SELECT * FROM thongbao WHERE maThongBao = ? AND maNguoiDung = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maThongBao);
            ps.setInt(2, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapThongBao(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean markAsRead(int maThongBao, int maNguoiDung) {
        String sql = "UPDATE thongbao SET daDoc = 1 WHERE maThongBao = ? AND maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maThongBao);
            ps.setInt(2, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean markAllAsRead(int maNguoiDung) {
        String sql = "UPDATE thongbao SET daDoc = 1 WHERE maNguoiDung = ? AND daDoc = 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean markByUrlAsRead(int maNguoiDung, String urlDich) {
        String normalizedUrl = trimToEmpty(urlDich);
        if (normalizedUrl.isEmpty()) {
            return false;
        }

        String sql = "UPDATE thongbao SET daDoc = 1 WHERE maNguoiDung = ? AND urlDich = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, normalizedUrl);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }
}
