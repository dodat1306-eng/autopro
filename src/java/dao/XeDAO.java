package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Xe;
import util.XeFieldNormalizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XeDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(XeDAO.class.getName());

    private String valueOrDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String getImageJoinSql(String xeAlias) {
        return " LEFT JOIN ("
                + "SELECT maXe, GROUP_CONCAT(duongDan ORDER BY thuTu SEPARATOR ',') AS dsHinhAnh "
                + "FROM xe_hinhanh GROUP BY maXe"
                + ") xh ON xh.maXe = " + xeAlias + ".maXe ";
    }

    private List<String> resolveImages(Xe x) {
        List<String> images = new ArrayList<>(x.getDanhSachHinhAnh());
        if (images.isEmpty() && x.getHinhAnh() != null && !x.getHinhAnh().isBlank()) {
            x.setDanhSachHinhAnhFromCsv(x.getHinhAnh());
            images.addAll(x.getDanhSachHinhAnh());
        }
        return images;
    }

    private void replaceXeImages(Connection conn, int maXe, List<String> images) throws SQLException {
        try (PreparedStatement deletePs = conn.prepareStatement("DELETE FROM xe_hinhanh WHERE maXe = ?")) {
            deletePs.setInt(1, maXe);
            deletePs.executeUpdate();
        }

        if (images.isEmpty()) {
            return;
        }

        try (PreparedStatement insertPs = conn.prepareStatement("INSERT INTO xe_hinhanh(maXe, duongDan, thuTu) VALUES(?, ?, ?)")) {
            int thuTu = 1;
            for (String image : images) {
                insertPs.setInt(1, maXe);
                insertPs.setString(2, image);
                insertPs.setInt(3, thuTu++);
                insertPs.executeUpdate();
            }
        }
    }

    private void normalizeXeForPersistence(Xe x) {
        x.setTinhTrang(XeFieldNormalizer.normalizeTinhTrangForStorage(x.getTinhTrang()));
        x.setNhienLieu(XeFieldNormalizer.normalizeNhienLieuForStorage(x.getNhienLieu()));
        x.setHopSo(XeFieldNormalizer.normalizeHopSoForStorage(x.getHopSo()));
    }

    private Xe mapXe(ResultSet rs) throws SQLException {
        Xe xe = new Xe(
                rs.getInt("maXe"),
                rs.getInt("maHang"),
                rs.getString("tenXe"),
                rs.getDouble("gia"),
                rs.getString("hinhAnh"),
                rs.getString("moTa"),
                rs.getString("kieuDang"),
                rs.getString("tinhTrang"),
                rs.getString("nhienLieu"),
                rs.getInt("soCho"),
                rs.getString("hopSo"),
                rs.getInt("namSanXuat"),
                rs.getString("mauXe"),
                rs.getInt("soKmDaDi"),
                rs.getString("xuatXu")
        );
        if (hasColumn(rs, "dsHinhAnh")) {
            xe.setDanhSachHinhAnhFromCsv(rs.getString("dsHinhAnh"));
        } else {
            xe.setDanhSachHinhAnhFromCsv(rs.getString("hinhAnh"));
        }
        xe.setTinhTrang(XeFieldNormalizer.normalizeTinhTrang(xe.getTinhTrang(), null));
        xe.setNhienLieu(XeFieldNormalizer.normalizeNhienLieu(xe.getNhienLieu(), null));
        xe.setHopSo(XeFieldNormalizer.normalizeHopSo(xe.getHopSo(), null));

        if (hasColumn(rs, "maTinh")) {
            int maTinh = rs.getInt("maTinh");
            if (!rs.wasNull()) {
                xe.setMaTinh(maTinh);
            }
        }
        if (hasColumn(rs, "tenTinh")) {
            xe.setTenTinh(rs.getString("tenTinh"));
        }
        if (hasColumn(rs, "mien")) {
            xe.setMien(rs.getString("mien"));
        }
        if (hasColumn(rs, "maBaiDang")) {
            int maBaiDang = rs.getInt("maBaiDang");
            if (!rs.wasNull()) {
                xe.setMaBaiDang(maBaiDang);
            }
        }
        if (hasColumn(rs, "tieuDeBaiDang")) {
            xe.setTieuDeBaiDang(rs.getString("tieuDeBaiDang"));
        }
        if (hasColumn(rs, "trangThaiBaiDang")) {
            xe.setTrangThaiBaiDang(rs.getString("trangThaiBaiDang"));
        }

        return xe;
    }

    private String getPublicBaseSql() {
        return "SELECT x.*, COALESCE(xh.dsHinhAnh, x.hinhAnh) AS dsHinhAnh, "
                + "b.maBaiDang, b.tieuDe AS tieuDeBaiDang, "
                + "CASE "
                + "WHEN b.trangThai = 'dang_giao_dich' OR active_tx.maXe IS NOT NULL THEN 'dang_giao_dich' "
                + "ELSE b.trangThai END AS trangThaiBaiDang, "
                + "b.maTinh, tt.tenTinh, tt.mien FROM xe x "
                + getImageJoinSql("x")
                + "JOIN baidang b ON b.maBaiDang = ("
                + "SELECT b2.maBaiDang FROM baidang b2 "
                + "WHERE b2.maXe = x.maXe "
                + "AND b2.trangThai <> 'da_ban' "
                + "AND NOT EXISTS (SELECT 1 FROM giaodichxe g2 WHERE g2.maXe = x.maXe AND g2.trangThai = 'HOAN_TAT') "
                + "AND b2.trangThai IN ('da_duyet', 'dang_giao_dich') "
                + "ORDER BY b2.ngayDang DESC, b2.maBaiDang DESC LIMIT 1"
                + ") "
                + "LEFT JOIN (SELECT DISTINCT maXe FROM giaodichxe WHERE trangThai NOT IN ('HOAN_TAT', 'HUY', 'MOI_TAO')) active_tx ON active_tx.maXe = x.maXe "
                + "LEFT JOIN tinhthanh tt ON tt.maTinh = b.maTinh "
                + "WHERE b.trangThai <> 'da_ban' "
                + "AND NOT EXISTS (SELECT 1 FROM giaodichxe sold WHERE sold.maXe = x.maXe AND sold.trangThai = 'HOAN_TAT') "
                + "AND b.trangThai IN ('da_duyet', 'dang_giao_dich')";
    }

    public List<Xe> filterXe(String keyword, Integer maHang, Double giaMin, Double giaMax, Integer namSX,
            String kieuDang, Integer maTinh, String mien, String nhienLieu, String hopSo,
            String tinhTrang, Integer soChoMin, Integer soChoMax, Integer kmMin,
            Integer kmMax, String xuatXu) {
        List<Xe> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(getPublicBaseSql());
        String normalizedNhienLieu = XeFieldNormalizer.normalizeNhienLieu(nhienLieu, null);
        String normalizedHopSo = XeFieldNormalizer.normalizeHopSo(hopSo, null);
        String normalizedTinhTrang = XeFieldNormalizer.normalizeTinhTrang(tinhTrang, null);

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (x.tenXe LIKE ? OR b.tieuDe LIKE ? OR x.moTa LIKE ?)");
        }
        if (maHang != null) {
            sql.append(" AND x.maHang = ?");
        }
        if (giaMin != null) {
            sql.append(" AND x.gia >= ?");
        }
        if (giaMax != null) {
            sql.append(" AND x.gia <= ?");
        }
        if (namSX != null) {
            sql.append(" AND x.namSanXuat = ?");
        }
        if (kieuDang != null && !kieuDang.isBlank()) {
            sql.append(" AND x.kieuDang = ?");
        }
        if (maTinh != null) {
            sql.append(" AND b.maTinh = ?");
        }
        if (mien != null && !mien.isBlank()) {
            sql.append(" AND tt.mien = ?");
        }
        if (normalizedNhienLieu != null && !normalizedNhienLieu.isBlank()) {
            sql.append(" AND x.nhienLieu = ?");
        }
        if (normalizedHopSo != null && !normalizedHopSo.isBlank()) {
            sql.append(" AND x.hopSo = ?");
        }
        if (normalizedTinhTrang != null && !normalizedTinhTrang.isBlank()) {
            sql.append(" AND x.tinhTrang = ?");
        }
        if (soChoMin != null) {
            sql.append(" AND x.soCho >= ?");
        }
        if (soChoMax != null) {
            sql.append(" AND x.soCho <= ?");
        }
        if (kmMin != null) {
            sql.append(" AND x.soKmDaDi >= ?");
        }
        if (kmMax != null) {
            sql.append(" AND x.soKmDaDi <= ?");
        }
        if (xuatXu != null && !xuatXu.isBlank()) {
            sql.append(" AND x.xuatXu LIKE ?");
        }
        sql.append(" ORDER BY b.ngayDang DESC, x.maXe DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;
            if (keyword != null && !keyword.isBlank()) {
                ps.setString(i++, "%" + keyword.trim() + "%");
                ps.setString(i++, "%" + keyword.trim() + "%");
                ps.setString(i++, "%" + keyword.trim() + "%");
            }
            if (maHang != null) {
                ps.setInt(i++, maHang);
            }
            if (giaMin != null) {
                ps.setDouble(i++, giaMin);
            }
            if (giaMax != null) {
                ps.setDouble(i++, giaMax);
            }
            if (namSX != null) {
                ps.setInt(i++, namSX);
            }
            if (kieuDang != null && !kieuDang.isBlank()) {
                ps.setString(i++, kieuDang.trim());
            }
            if (maTinh != null) {
                ps.setInt(i++, maTinh);
            }
            if (mien != null && !mien.isBlank()) {
                ps.setString(i++, mien.trim());
            }
            if (normalizedNhienLieu != null && !normalizedNhienLieu.isBlank()) {
                ps.setString(i++, normalizedNhienLieu.trim());
            }
            if (normalizedHopSo != null && !normalizedHopSo.isBlank()) {
                ps.setString(i++, normalizedHopSo.trim());
            }
            if (normalizedTinhTrang != null && !normalizedTinhTrang.isBlank()) {
                ps.setString(i++, normalizedTinhTrang.trim());
            }
            if (soChoMin != null) {
                ps.setInt(i++, soChoMin);
            }
            if (soChoMax != null) {
                ps.setInt(i++, soChoMax);
            }
            if (kmMin != null) {
                ps.setInt(i++, kmMin);
            }
            if (kmMax != null) {
                ps.setInt(i++, kmMax);
            }
            if (xuatXu != null && !xuatXu.isBlank()) {
                ps.setString(i++, "%" + xuatXu.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapXe(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public Xe getXeById(int id) {
        String sql = "SELECT x.*, COALESCE(xh.dsHinhAnh, x.hinhAnh) AS dsHinhAnh FROM xe x "
                + getImageJoinSql("x")
                + "WHERE x.maXe = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapXe(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public Xe getPublicXeById(int id) {
        String sql = getPublicBaseSql() + " AND x.maXe = ? ORDER BY b.ngayDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapXe(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public List<Xe> searchXe(String keyword) {
        return filterXe(keyword, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public List<Xe> getAllXe() {
        List<Xe> list = new ArrayList<>();
        String sql = getPublicBaseSql() + " ORDER BY b.ngayDang DESC, x.maXe DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapXe(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<Xe> getAllXeAdmin() {
        List<Xe> list = new ArrayList<>();
        String sql = "SELECT x.*, COALESCE(xh.dsHinhAnh, x.hinhAnh) AS dsHinhAnh FROM xe x "
                + getImageJoinSql("x")
                + "ORDER BY x.maXe DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Xe x = mapXe(rs);
                list.add(x);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        return list;
    }

    public boolean deleteXe(int maXe) {
        String sql = "DELETE FROM xe WHERE maXe = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maXe);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateXe(Xe x) {
        normalizeXeForPersistence(x);
        List<String> images = resolveImages(x);
        String sql = "UPDATE xe SET maHang = ?, tenXe = ?, gia = ?, hinhAnh = ?, moTa = ?, kieuDang = ?, "
                + "tinhTrang = ?, nhienLieu = ?, soCho = ?, hopSo = ?, namSanXuat = ?, mauXe = ?, "
                + "soKmDaDi = ?, xuatXu = ? WHERE maXe = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (x.getMaHang() > 0) {
                ps.setInt(1, x.getMaHang());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, valueOrDefault(x.getTenXe(), "Chua cap nhat"));
            ps.setDouble(3, x.getGia());
            ps.setString(4, images.isEmpty() ? "" : images.get(0));
            ps.setString(5, valueOrDefault(x.getMoTa(), ""));
            ps.setString(6, valueOrDefault(x.getKieuDang(), "Sedan"));
            ps.setString(7, valueOrDefault(x.getTinhTrang(), "Cũ"));
            ps.setString(8, valueOrDefault(x.getNhienLieu(), "Xăng"));
            ps.setInt(9, x.getSoCho() > 0 ? x.getSoCho() : 5);
            ps.setString(10, valueOrDefault(x.getHopSo(), "Tự động"));
            ps.setInt(11, x.getNamSanXuat());
            ps.setString(12, valueOrDefault(x.getMauXe(), "Chua cap nhat"));
            ps.setInt(13, Math.max(x.getSoKmDaDi(), 0));
            ps.setString(14, valueOrDefault(x.getXuatXu(), "Viet Nam"));
            ps.setInt(15, x.getMaXe());
            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                replaceXeImages(conn, x.getMaXe(), images);
            }
            return updated;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public int insertXe(Xe x) {
        normalizeXeForPersistence(x);
        List<String> images = resolveImages(x);
        String sql = "INSERT INTO xe (maHang, tenXe, gia, hinhAnh, moTa, kieuDang, tinhTrang, nhienLieu, soCho, hopSo, namSanXuat, mauXe, soKmDaDi, xuatXu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (x.getMaHang() > 0) {
                ps.setInt(1, x.getMaHang());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, valueOrDefault(x.getTenXe(), "Chua cap nhat"));
            ps.setDouble(3, x.getGia());
            ps.setString(4, images.isEmpty() ? "" : images.get(0));
            ps.setString(5, valueOrDefault(x.getMoTa(), ""));
            ps.setString(6, valueOrDefault(x.getKieuDang(), "Sedan"));
            ps.setString(7, valueOrDefault(x.getTinhTrang(), "Cũ"));
            ps.setString(8, valueOrDefault(x.getNhienLieu(), "Xăng"));
            ps.setInt(9, x.getSoCho() > 0 ? x.getSoCho() : 5);
            ps.setString(10, valueOrDefault(x.getHopSo(), "Tự động"));
            ps.setInt(11, x.getNamSanXuat());
            ps.setString(12, valueOrDefault(x.getMauXe(), "Chua cap nhat"));
            ps.setInt(13, Math.max(x.getSoKmDaDi(), 0));
            ps.setString(14, valueOrDefault(x.getXuatXu(), "Viet Nam"));

            int updated = ps.executeUpdate();
            if (updated <= 0) {
                return -1;
            }

            int maXe = -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    maXe = rs.getInt(1);
                }
            }

            if (maXe <= 0) {
                try (PreparedStatement fallback = conn.prepareStatement("SELECT LAST_INSERT_ID()");
                     ResultSet rs = fallback.executeQuery()) {
                    if (rs.next()) {
                        maXe = rs.getInt(1);
                    }
                }
            }

            if (maXe > 0) {
                replaceXeImages(conn, maXe, images);
                return maXe;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return -1;
    }
}
