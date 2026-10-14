package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.BaoCao;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaoCaoDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BaoCaoDAO.class.getName());

    private BaoCao mapBaoCao(ResultSet rs) throws Exception {
        BaoCao baoCao = new BaoCao();
        baoCao.setMaBaoCao(rs.getInt("maBaoCao"));
        baoCao.setMaBaiDang(rs.getInt("maBaiDang"));
        baoCao.setMaNguoiBaoCao(rs.getInt("maNguoiBaoCao"));
        baoCao.setLyDo(rs.getString("lyDo"));
        baoCao.setChiTiet(rs.getString("chiTiet"));
        baoCao.setTrangThai(rs.getString("trangThai"));
        baoCao.setGhiChuAdmin(rs.getString("ghiChuAdmin"));
        int maAdminXuLy = rs.getInt("maAdminXuLy");
        if (!rs.wasNull()) {
            baoCao.setMaAdminXuLy(maAdminXuLy);
        }
        baoCao.setNgayBaoCao(rs.getTimestamp("ngayBaoCao"));
        baoCao.setNgayXuLy(rs.getTimestamp("ngayXuLy"));

        try {
            baoCao.setTieuDeBaiDang(rs.getString("tieuDeBaiDang"));
            baoCao.setTenXe(rs.getString("tenXe"));
            baoCao.setTenNguoiBaoCao(rs.getString("tenNguoiBaoCao"));
            baoCao.setEmailNguoiBaoCao(rs.getString("emailNguoiBaoCao"));
            int maNguoiDang = rs.getInt("maNguoiDang");
            if (!rs.wasNull()) {
                baoCao.setMaNguoiDang(maNguoiDang);
            }
            baoCao.setTenNguoiDang(rs.getString("tenNguoiDang"));
            baoCao.setEmailNguoiDang(rs.getString("emailNguoiDang"));
            baoCao.setTrangThaiBaiDang(rs.getString("trangThaiBaiDang"));
        } catch (Exception ignored) {
        }

        return baoCao;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public boolean hasPendingReportByUser(int maBaiDang, int maNguoiBaoCao) {
        String sql = "SELECT 1 FROM baocao WHERE maBaiDang = ? AND maNguoiBaoCao = ? AND trangThai = 'CHO_XU_LY' LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            ps.setInt(2, maNguoiBaoCao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean createReport(int maBaiDang, int maNguoiBaoCao, String lyDo, String chiTiet) {
        String sql = "INSERT INTO baocao(maBaiDang, maNguoiBaoCao, lyDo, chiTiet, trangThai, ghiChuAdmin, maAdminXuLy, ngayBaoCao, ngayXuLy) "
                + "VALUES(?, ?, ?, ?, 'CHO_XU_LY', NULL, NULL, NOW(), NULL)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            ps.setInt(2, maNguoiBaoCao);
            ps.setString(3, trimToEmpty(lyDo));
            ps.setString(4, trimToEmpty(chiTiet));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public int countPendingReports() {
        String sql = "SELECT COUNT(*) FROM baocao WHERE trangThai = 'CHO_XU_LY'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public List<BaoCao> getAllReportsForAdmin() {
        List<BaoCao> list = new ArrayList<>();
        String sql = "SELECT bc.*, b.tieuDe AS tieuDeBaiDang, "
                + "CASE "
                + "WHEN b.trangThai = 'da_ban' OR sold.maXe IS NOT NULL THEN 'da_ban' "
                + "WHEN b.trangThai = 'dang_giao_dich' OR active_tx.maXe IS NOT NULL THEN 'dang_giao_dich' "
                + "ELSE b.trangThai END AS trangThaiBaiDang, x.tenXe, "
                + "reporter.hoTen AS tenNguoiBaoCao, reporter.email AS emailNguoiBaoCao, "
                + "owner.maNguoiDung AS maNguoiDang, owner.hoTen AS tenNguoiDang, owner.email AS emailNguoiDang "
                + "FROM baocao bc "
                + "JOIN baidang b ON b.maBaiDang = bc.maBaiDang "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai = 'HOAN_TAT') sold ON sold.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai NOT IN ('HOAN_TAT', 'HUY', 'MOI_TAO')) active_tx ON active_tx.maXe = b.maXe "
                + "LEFT JOIN nguoidung reporter ON reporter.maNguoiDung = bc.maNguoiBaoCao "
                + "LEFT JOIN nguoidung owner ON owner.maNguoiDung = b.maNguoiDung "
                + "ORDER BY CASE WHEN bc.trangThai = 'CHO_XU_LY' THEN 0 ELSE 1 END, bc.ngayBaoCao DESC, bc.maBaoCao DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBaoCao(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<BaoCao> getReportsByPost(int maBaiDang) {
        List<BaoCao> list = new ArrayList<>();
        String sql = "SELECT bc.*, reporter.hoTen AS tenNguoiBaoCao, reporter.email AS emailNguoiBaoCao "
                + "FROM baocao bc "
                + "LEFT JOIN nguoidung reporter ON reporter.maNguoiDung = bc.maNguoiBaoCao "
                + "WHERE bc.maBaiDang = ? ORDER BY bc.ngayBaoCao DESC, bc.maBaoCao DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBaoCao(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public BaoCao getReportById(int maBaoCao) {
        String sql = "SELECT bc.*, b.tieuDe AS tieuDeBaiDang, "
                + "CASE "
                + "WHEN b.trangThai = 'da_ban' OR sold.maXe IS NOT NULL THEN 'da_ban' "
                + "WHEN b.trangThai = 'dang_giao_dich' OR active_tx.maXe IS NOT NULL THEN 'dang_giao_dich' "
                + "ELSE b.trangThai END AS trangThaiBaiDang, x.tenXe, "
                + "reporter.hoTen AS tenNguoiBaoCao, reporter.email AS emailNguoiBaoCao, "
                + "owner.maNguoiDung AS maNguoiDang, owner.hoTen AS tenNguoiDang, owner.email AS emailNguoiDang "
                + "FROM baocao bc "
                + "JOIN baidang b ON b.maBaiDang = bc.maBaiDang "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai = 'HOAN_TAT') sold ON sold.maXe = b.maXe "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai NOT IN ('HOAN_TAT', 'HUY', 'MOI_TAO')) active_tx ON active_tx.maXe = b.maXe "
                + "LEFT JOIN nguoidung reporter ON reporter.maNguoiDung = bc.maNguoiBaoCao "
                + "LEFT JOIN nguoidung owner ON owner.maNguoiDung = b.maNguoiDung "
                + "WHERE bc.maBaoCao = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaoCao);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaoCao(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean resolveReport(int maBaoCao, String trangThai, Integer maAdminXuLy, String ghiChuAdmin) {
        String sql = "UPDATE baocao SET trangThai = ?, maAdminXuLy = ?, ghiChuAdmin = ?, ngayXuLy = NOW() WHERE maBaoCao = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(trangThai));
            if (maAdminXuLy == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, maAdminXuLy);
            }
            if (trimToEmpty(ghiChuAdmin).isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, trimToEmpty(ghiChuAdmin));
            }
            ps.setInt(4, maBaoCao);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean resolvePendingReportsForPost(int maBaiDang, String trangThai, Integer maAdminXuLy, String ghiChuAdmin) {
        String sql = "UPDATE baocao SET trangThai = ?, maAdminXuLy = ?, ghiChuAdmin = ?, ngayXuLy = NOW() "
                + "WHERE maBaiDang = ? AND trangThai = 'CHO_XU_LY'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(trangThai));
            if (maAdminXuLy == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, maAdminXuLy);
            }
            if (trimToEmpty(ghiChuAdmin).isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, trimToEmpty(ghiChuAdmin));
            }
            ps.setInt(4, maBaiDang);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }
}
