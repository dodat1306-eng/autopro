package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.BaiDang;
import model.Xe;
import util.XeFieldNormalizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaiDangDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BaiDangDAO.class.getName());

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(conn.getCatalog(), null, tableName, null)) {
            return rs.next();
        }
    }

    private String valueOrDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String getImageJoinSql(String xeAlias) {
        return " LEFT JOIN ("
                + "SELECT maXe, GROUP_CONCAT(duongDan ORDER BY thuTu SEPARATOR ',') AS dsHinhAnh "
                + "FROM xe_hinhanh GROUP BY maXe"
                + ") xh ON xh.maXe = " + xeAlias + ".maXe ";
    }

    private void normalizeXeForPersistence(Xe xe) {
        xe.setTinhTrang(XeFieldNormalizer.normalizeTinhTrangForStorage(xe.getTinhTrang()));
        xe.setNhienLieu(XeFieldNormalizer.normalizeNhienLieuForStorage(xe.getNhienLieu()));
        xe.setHopSo(XeFieldNormalizer.normalizeHopSoForStorage(xe.getHopSo()));
    }

    private List<String> resolveImages(Xe xe) {
        List<String> images = new ArrayList<>(xe.getDanhSachHinhAnh());
        if (images.isEmpty() && xe.getHinhAnh() != null && !xe.getHinhAnh().isBlank()) {
            xe.setDanhSachHinhAnhFromCsv(xe.getHinhAnh());
            images.addAll(xe.getDanhSachHinhAnh());
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

    private String buildPlaceholders(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("?");
        }
        return builder.toString();
    }

    private BaiDang mapBaiDang(ResultSet rs) throws SQLException {
        BaiDang bd = new BaiDang();
        bd.setMaBaiDang(rs.getInt("maBaiDang"));
        bd.setMaNguoiDung(rs.getInt("maNguoiDung"));
        bd.setMaXe(rs.getInt("maXe"));

        int maTinh = rs.getInt("maTinh");
        if (!rs.wasNull()) {
            bd.setMaTinh(maTinh);
        }

        bd.setTieuDe(rs.getString("tieuDe"));
        bd.setMoTa(rs.getString("moTa"));
        bd.setTrangThai(rs.getString("trangThai"));
        bd.setNgayDang(rs.getTimestamp("ngayDang"));

        if (hasColumn(rs, "ghiChuAdmin")) {
            bd.setGhiChuAdmin(rs.getString("ghiChuAdmin"));
        }
        if (hasColumn(rs, "gia")) {
            bd.setGiaBan(rs.getDouble("gia"));
        }
        if (hasColumn(rs, "tenXe")) {
            bd.setTenXe(rs.getString("tenXe"));
        }
        if (hasColumn(rs, "tenNguoiDang")) {
            bd.setTenNguoiDang(rs.getString("tenNguoiDang"));
        }
        if (hasColumn(rs, "emailNguoiDang")) {
            bd.setEmailNguoiDang(rs.getString("emailNguoiDang"));
        }
        if (hasColumn(rs, "soDienThoaiNguoiDang")) {
            bd.setSoDienThoaiNguoiDang(rs.getString("soDienThoaiNguoiDang"));
        }
        if (hasColumn(rs, "anhDaiDien")) {
            String anhDaiDien = rs.getString("anhDaiDien");
            if (anhDaiDien != null && anhDaiDien.contains(",")) {
                anhDaiDien = anhDaiDien.split(",", 2)[0].trim();
            }
            bd.setAnhDaiDien(anhDaiDien);
        }
        if (hasColumn(rs, "tenTinh")) {
            bd.setTenTinh(rs.getString("tenTinh"));
        }
        if (hasColumn(rs, "mien")) {
            bd.setMien(rs.getString("mien"));
        }
        if (hasColumn(rs, "luotXem")) {
            bd.setLuotXem(rs.getInt("luotXem"));
        }
        if (hasColumn(rs, "maGiaoDich")) {
            int maGiaoDich = rs.getInt("maGiaoDich");
            if (!rs.wasNull()) {
                bd.setMaGiaoDich(maGiaoDich);
            }
        }
        if (hasColumn(rs, "tenHang")) {
            bd.setTenHang(rs.getString("tenHang"));
        }
        if (hasColumn(rs, "namSanXuat")) {
            int namSanXuat = rs.getInt("namSanXuat");
            if (!rs.wasNull()) {
                bd.setNamSanXuat(namSanXuat);
            }
        }
        if (hasColumn(rs, "tinhTrangXe")) {
            bd.setTinhTrangXe(rs.getString("tinhTrangXe"));
        }
        if (hasColumn(rs, "nhienLieu")) {
            bd.setNhienLieu(rs.getString("nhienLieu"));
        }
        if (hasColumn(rs, "hopSo")) {
            bd.setHopSo(rs.getString("hopSo"));
        }
        if (hasColumn(rs, "soKmDaDi")) {
            int soKmDaDi = rs.getInt("soKmDaDi");
            if (!rs.wasNull()) {
                bd.setSoKmDaDi(soKmDaDi);
            }
        }
        if (hasColumn(rs, "soCho")) {
            int soCho = rs.getInt("soCho");
            if (!rs.wasNull()) {
                bd.setSoCho(soCho);
            }
        }
        if (hasColumn(rs, "xuatXu")) {
            bd.setXuatXu(rs.getString("xuatXu"));
        }
        if (hasColumn(rs, "kieuDang")) {
            bd.setKieuDang(rs.getString("kieuDang"));
        }
        if (hasColumn(rs, "mauXe")) {
            bd.setMauXe(rs.getString("mauXe"));
        }
        return bd;
    }

    public List<BaiDang> getBaiDangByUser(int maNguoiDung) {
        List<BaiDang> list = new ArrayList<>();
        String sql = "SELECT b.*, x.tenXe, x.gia, "
                + "(SELECT g.maGiaoDich FROM giaodichxe g "
                + "WHERE g.maXe = b.maXe AND g.maNguoiBan = b.maNguoiDung AND g.trangThai = 'HOAN_TAT' "
                + "ORDER BY g.ngayCapNhat DESC, g.maGiaoDich DESC LIMIT 1) AS maGiaoDich "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "WHERE b.maNguoiDung = ? ORDER BY b.ngayDang DESC, b.maBaiDang DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBaiDang(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<BaiDang> getBaiDangChoDuyet() {
        List<BaiDang> list = new ArrayList<>();
        String sql = "SELECT b.*, x.tenXe, x.gia, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE b.trangThai IN ('cho_duyet', 'canh_bao') "
                + "ORDER BY b.ngayDang DESC, b.maBaiDang DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBaiDang(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    private List<BaiDang> getAdminPostListByCondition(String extraCondition, String sellerKeyword) {
        List<BaiDang> list = new ArrayList<>();
        String normalizedKeyword = sellerKeyword == null ? "" : sellerKeyword.trim();
        StringBuilder sql = new StringBuilder("SELECT b.*, x.tenXe, x.gia, COALESCE(xh.dsHinhAnh, x.hinhAnh) AS anhDaiDien, "
                + "x.namSanXuat, x.tinhTrang AS tinhTrangXe, "
                + "x.nhienLieu, x.hopSo, x.soKmDaDi, x.soCho, x.xuatXu, x.kieuDang, x.mauXe, "
                + "h.tenHang, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang, u.soDienThoai AS soDienThoaiNguoiDang, "
                + "(SELECT g.maGiaoDich FROM giaodichxe g "
                + "WHERE g.maXe = b.maXe AND g.maNguoiBan = b.maNguoiDung AND g.trangThai = 'HOAN_TAT' "
                + "ORDER BY g.ngayCapNhat DESC, g.maGiaoDich DESC LIMIT 1) AS maGiaoDich "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + getImageJoinSql("x")
                + "LEFT JOIN hangxe h ON h.maHang = x.maHang "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE 1=1 ");
        if (extraCondition != null && !extraCondition.isBlank()) {
            sql.append(extraCondition);
        }
        if (!normalizedKeyword.isEmpty()) {
            sql.append(" AND (u.hoTen LIKE ? OR u.email LIKE ? OR COALESCE(u.soDienThoai, '') LIKE ? "
                    + "OR x.tenXe LIKE ? OR h.tenHang LIKE ?) ");
        }
        sql.append("ORDER BY b.ngayDang DESC, b.maBaiDang DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (!normalizedKeyword.isEmpty()) {
                String searchPattern = "%" + normalizedKeyword + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBaiDang(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<BaiDang> getPendingBaiDangForAdmin(String sellerKeyword) {
        return getAdminPostListByCondition(" AND b.trangThai = 'cho_duyet' ", sellerKeyword);
    }

    public List<BaiDang> getNonPendingBaiDangForAdmin(String sellerKeyword) {
        return getAdminPostListByCondition(" AND (b.trangThai IS NULL OR b.trangThai <> 'cho_duyet') ", sellerKeyword);
    }

    public List<BaiDang> getAllBaiDangForAdmin() {
        return getAdminPostListByCondition("", "");
    }

    /**
     * Insert xe và baidang trong một transaction duy nhất.
     * Trả về maXe nếu thành công, -1 nếu lỗi (cả 2 đều rollback).
     * bd.maXe sẽ được gán tự động từ xe vừa insert.
     */
    public int insertXeVaBaiDang(Xe xe, BaiDang bd) {
        normalizeXeForPersistence(xe);
        List<String> images = resolveImages(xe);
        String insertXeSql = "INSERT INTO xe (maHang, tenXe, gia, hinhAnh, moTa, kieuDang, tinhTrang, nhienLieu, soCho, hopSo, namSanXuat, mauXe, soKmDaDi, xuatXu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertBdSql = "INSERT INTO baidang (maNguoiDung, maXe, maTinh, tieuDe, moTa, trangThai, ghiChuAdmin, ngayDang) "
                + "VALUES (?, ?, ?, ?, ?, ?, NULL, NOW())";

        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // 1. Insert xe
                int maXe = -1;
                try (PreparedStatement ps = conn.prepareStatement(insertXeSql, Statement.RETURN_GENERATED_KEYS)) {
                    if (xe.getMaHang() > 0) {
                        ps.setInt(1, xe.getMaHang());
                    } else {
                        ps.setNull(1, Types.INTEGER);
                    }
                    ps.setString(2, valueOrDefault(xe.getTenXe(), "Chua cap nhat"));
                    ps.setDouble(3, xe.getGia());
                    ps.setString(4, images.isEmpty() ? "" : images.get(0));
                    ps.setString(5, valueOrDefault(xe.getMoTa(), ""));
                    ps.setString(6, valueOrDefault(xe.getKieuDang(), "Sedan"));
                    ps.setString(7, valueOrDefault(xe.getTinhTrang(), "Cũ"));
                    ps.setString(8, valueOrDefault(xe.getNhienLieu(), "Xăng"));
                    ps.setInt(9, xe.getSoCho() > 0 ? xe.getSoCho() : 5);
                    ps.setString(10, valueOrDefault(xe.getHopSo(), "Tự động"));
                    ps.setInt(11, xe.getNamSanXuat());
                    ps.setString(12, valueOrDefault(xe.getMauXe(), "Chua cap nhat"));
                    ps.setInt(13, Math.max(xe.getSoKmDaDi(), 0));
                    ps.setString(14, valueOrDefault(xe.getXuatXu(), "Viet Nam"));
                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return -1;
                    }
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            maXe = rs.getInt(1);
                        }
                    }
                }

                if (maXe <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return -1;
                }

                // Insert ảnh xe vào xe_hinhanh
                replaceXeImages(conn, maXe, images);

                // 2. Insert baidang
                bd.setMaXe(maXe);
                try (PreparedStatement ps = conn.prepareStatement(insertBdSql)) {
                    ps.setInt(1, bd.getMaNguoiDung());
                    ps.setInt(2, maXe);
                    if (bd.getMaTinh() > 0) {
                        ps.setInt(3, bd.getMaTinh());
                    } else {
                        ps.setNull(3, Types.INTEGER);
                    }
                    ps.setString(4, bd.getTieuDe());
                    ps.setString(5, bd.getMoTa());
                    ps.setString(6, (bd.getTrangThai() == null || bd.getTrangThai().isBlank()) ? "cho_duyet" : bd.getTrangThai());
                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return -1;
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return maXe;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return -1;
    }

    public boolean insertBaiDang(BaiDang bd) {
        String sql = "INSERT INTO baidang (maNguoiDung, maXe, maTinh, tieuDe, moTa, trangThai, ghiChuAdmin, ngayDang) "
                + "VALUES (?, ?, ?, ?, ?, ?, NULL, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bd.getMaNguoiDung());
            ps.setInt(2, bd.getMaXe());
            if (bd.getMaTinh() > 0) {
                ps.setInt(3, bd.getMaTinh());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, bd.getTieuDe());
            ps.setString(5, bd.getMoTa());
            ps.setString(6, (bd.getTrangThai() == null || bd.getTrangThai().isBlank()) ? "cho_duyet" : bd.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public BaiDang getBaiDangById(int maBaiDang, int maNguoiDung) {
        String sql = "SELECT b.*, x.tenXe, x.gia FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "WHERE b.maBaiDang = ? AND b.maNguoiDung = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            ps.setInt(2, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaiDang(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public BaiDang getBaiDangByIdForAdmin(int maBaiDang) {
        String sql = "SELECT b.*, x.tenXe, x.gia, COALESCE(xh.dsHinhAnh, x.hinhAnh) AS anhDaiDien, "
                + "x.namSanXuat, x.tinhTrang AS tinhTrangXe, "
                + "x.nhienLieu, x.hopSo, x.soKmDaDi, x.soCho, x.xuatXu, x.kieuDang, x.mauXe, "
                + "h.tenHang, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang, u.soDienThoai AS soDienThoaiNguoiDang, "
                + "(SELECT g.maGiaoDich FROM giaodichxe g "
                + "WHERE g.maXe = b.maXe AND g.maNguoiBan = b.maNguoiDung AND g.trangThai = 'HOAN_TAT' "
                + "ORDER BY g.ngayCapNhat DESC, g.maGiaoDich DESC LIMIT 1) AS maGiaoDich "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + getImageJoinSql("x")
                + "LEFT JOIN hangxe h ON h.maHang = x.maHang "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE b.maBaiDang = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaiDang(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public BaiDang getLatestApprovedBaiDangByMaXe(int maXe) {
        String sql = "SELECT b.*, x.tenXe, x.gia, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE b.maXe = ? AND b.trangThai = 'da_duyet' "
                + "ORDER BY b.ngayDang DESC, b.maBaiDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaiDang(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public BaiDang getLatestPublicBaiDangByMaXe(int maXe) {
        String sql = "SELECT b.*, x.tenXe, x.gia, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE b.maXe = ? "
                + "AND b.trangThai <> 'da_ban' "
                + "AND NOT EXISTS (SELECT 1 FROM giaodichxe g WHERE g.maXe = b.maXe AND g.trangThai = 'HOAN_TAT') "
                + "AND b.trangThai IN ('da_duyet', 'dang_giao_dich') "
                + "ORDER BY b.ngayDang DESC, b.maBaiDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaiDang(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public BaiDang getApprovedBaiDangById(int maBaiDang) {
        String sql = "SELECT b.*, x.tenXe, x.gia, u.hoTen AS tenNguoiDang, u.email AS emailNguoiDang "
                + "FROM baidang b "
                + "LEFT JOIN xe x ON x.maXe = b.maXe "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = b.maNguoiDung "
                + "WHERE b.maBaiDang = ? AND b.trangThai = 'da_duyet' LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBaiDang(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public Integer getMaNguoiDungByMaXe(int maXe) {
        String sql = "SELECT maNguoiDung FROM baidang WHERE maXe = ? ORDER BY ngayDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maNguoiDung");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public Integer getApprovedSellerIdByMaXe(int maXe) {
        String sql = "SELECT maNguoiDung FROM baidang "
                + "WHERE maXe = ? AND trangThai = 'da_duyet' "
                + "ORDER BY ngayDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maNguoiDung");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public Integer getPublicSellerIdByMaXe(int maXe) {
        String sql = "SELECT b.maNguoiDung FROM baidang b "
                + "WHERE b.maXe = ? "
                + "AND b.trangThai <> 'da_ban' "
                + "AND NOT EXISTS (SELECT 1 FROM giaodichxe g WHERE g.maXe = b.maXe AND g.trangThai = 'HOAN_TAT') "
                + "AND b.trangThai IN ('da_duyet', 'dang_giao_dich') "
                + "ORDER BY b.ngayDang DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maNguoiDung");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean updateBaiDang(BaiDang bd) {
        String sql = "UPDATE baidang SET maTinh = ?, tieuDe = ?, moTa = ?, trangThai = ?, ghiChuAdmin = NULL "
                + "WHERE maBaiDang = ? AND maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (bd.getMaTinh() > 0) {
                ps.setInt(1, bd.getMaTinh());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, bd.getTieuDe());
            ps.setString(3, bd.getMoTa());
            ps.setString(4, bd.getTrangThai());
            ps.setInt(5, bd.getMaBaiDang());
            ps.setInt(6, bd.getMaNguoiDung());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateBaiDangVaXe(BaiDang bd, Xe xe) {
        normalizeXeForPersistence(xe);
        List<String> images = resolveImages(xe);
        String updateBaiDangSql = "UPDATE baidang SET maTinh = ?, tieuDe = ?, moTa = ?, trangThai = ?, ghiChuAdmin = NULL "
                + "WHERE maBaiDang = ? AND maNguoiDung = ?";
        String updateXeSql = "UPDATE xe SET maHang = ?, tenXe = ?, gia = ?, hinhAnh = ?, moTa = ?, kieuDang = ?, "
                + "tinhTrang = ?, nhienLieu = ?, soCho = ?, hopSo = ?, namSanXuat = ?, mauXe = ?, "
                + "soKmDaDi = ?, xuatXu = ? WHERE maXe = ?";

        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement postStatement = conn.prepareStatement(updateBaiDangSql);
                 PreparedStatement carStatement = conn.prepareStatement(updateXeSql)) {

                if (bd.getMaTinh() > 0) {
                    postStatement.setInt(1, bd.getMaTinh());
                } else {
                    postStatement.setNull(1, Types.INTEGER);
                }
                postStatement.setString(2, bd.getTieuDe());
                postStatement.setString(3, bd.getMoTa());
                postStatement.setString(4, bd.getTrangThai());
                postStatement.setInt(5, bd.getMaBaiDang());
                postStatement.setInt(6, bd.getMaNguoiDung());

                if (xe.getMaHang() > 0) {
                    carStatement.setInt(1, xe.getMaHang());
                } else {
                    carStatement.setNull(1, Types.INTEGER);
                }
                carStatement.setString(2, valueOrDefault(xe.getTenXe(), "Chua cap nhat"));
                carStatement.setDouble(3, xe.getGia());
                carStatement.setString(4, images.isEmpty() ? "" : images.get(0));
                carStatement.setString(5, valueOrDefault(xe.getMoTa(), ""));
                carStatement.setString(6, valueOrDefault(xe.getKieuDang(), "Sedan"));
                carStatement.setString(7, valueOrDefault(xe.getTinhTrang(), "Cũ"));
                carStatement.setString(8, valueOrDefault(xe.getNhienLieu(), "Xăng"));
                carStatement.setInt(9, xe.getSoCho() > 0 ? xe.getSoCho() : 5);
                carStatement.setString(10, valueOrDefault(xe.getHopSo(), "Tự động"));
                carStatement.setInt(11, xe.getNamSanXuat());
                carStatement.setString(12, valueOrDefault(xe.getMauXe(), "Chua cap nhat"));
                carStatement.setInt(13, Math.max(xe.getSoKmDaDi(), 0));
                carStatement.setString(14, valueOrDefault(xe.getXuatXu(), "Viet Nam"));
                carStatement.setInt(15, xe.getMaXe());

                boolean updatedPost = postStatement.executeUpdate() > 0;
                boolean updatedCar = carStatement.executeUpdate() > 0;
                if (!updatedPost || !updatedCar) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                replaceXeImages(conn, xe.getMaXe(), images);

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    private List<Integer> getRequestIdsByXe(Connection conn, int maXe) throws SQLException {
        List<Integer> requestIds = new ArrayList<>();
        if (!tableExists(conn, "yeucaumuaxe")) {
            return requestIds;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT maYeuCau FROM yeucaumuaxe WHERE maXe = ?")) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestIds.add(rs.getInt("maYeuCau"));
                }
            }
        }
        return requestIds;
    }

    private void deleteConversationDataByXe(Connection conn, int maXe) throws SQLException {
        if (!tableExists(conn, "hoithoai")) {
            return;
        }

        if (tableExists(conn, "tinnhan")) {
            String deleteMessagesSql = "DELETE FROM tinnhan "
                    + "WHERE maHoiThoai IN (SELECT maHoiThoai FROM hoithoai WHERE maXe = ?)";
            try (PreparedStatement ps = conn.prepareStatement(deleteMessagesSql)) {
                ps.setInt(1, maXe);
                ps.executeUpdate();
            }
        }

        String deleteRoomsSql = "DELETE FROM hoithoai WHERE maXe = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteRoomsSql)) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    private void deleteRequestDataByXe(Connection conn, int maXe, List<Integer> requestIds) throws SQLException {
        if (!tableExists(conn, "yeucaumuaxe")) {
            return;
        }

        if (tableExists(conn, "giaodichxe")) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM giaodichxe WHERE maXe = ?")) {
                ps.setInt(1, maXe);
                ps.executeUpdate();
            }
        }

        if (!requestIds.isEmpty() && tableExists(conn, "lichhenxemxe")) {
            String sql = "DELETE FROM lichhenxemxe WHERE maYeuCau IN (" + buildPlaceholders(requestIds.size()) + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < requestIds.size(); i++) {
                    ps.setInt(i + 1, requestIds.get(i));
                }
                ps.executeUpdate();
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM yeucaumuaxe WHERE maXe = ?")) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    private void deletePostDependentData(Connection conn, int maBaiDang) throws SQLException {
        if (tableExists(conn, "baidang_yeuthich")) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM baidang_yeuthich WHERE maBaiDang = ?")) {
                ps.setInt(1, maBaiDang);
                ps.executeUpdate();
            }
        }
        if (tableExists(conn, "baocao")) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM baocao WHERE maBaiDang = ?")) {
                ps.setInt(1, maBaiDang);
                ps.executeUpdate();
            }
        }
    }

    private void deleteCarImageData(Connection conn, int maXe) throws SQLException {
        if (tableExists(conn, "xe_hinhanh")) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM xe_hinhanh WHERE maXe = ?")) {
                ps.setInt(1, maXe);
                ps.executeUpdate();
            }
        }
    }

    private boolean deleteBaiDangInternal(int maBaiDang, Integer maNguoiDung) {
        String selectSql = maNguoiDung == null
                ? "SELECT maXe FROM baidang WHERE maBaiDang = ?"
                : "SELECT maXe FROM baidang WHERE maBaiDang = ? AND maNguoiDung = ?";
        String deletePostSql = maNguoiDung == null
                ? "DELETE FROM baidang WHERE maBaiDang = ?"
                : "DELETE FROM baidang WHERE maBaiDang = ? AND maNguoiDung = ?";
        String deleteCarSql = "DELETE FROM xe WHERE maXe = ? AND NOT EXISTS (SELECT 1 FROM baidang WHERE maXe = ?)";

        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                Integer maXe = null;
                try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                    ps.setInt(1, maBaiDang);
                    if (maNguoiDung != null) {
                        ps.setInt(2, maNguoiDung);
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            maXe = rs.getInt("maXe");
                        }
                    }
                }

                if (maXe == null) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                List<Integer> requestIds = getRequestIdsByXe(conn, maXe);
                deleteRequestDataByXe(conn, maXe, requestIds);
                deleteConversationDataByXe(conn, maXe);
                deletePostDependentData(conn, maBaiDang);

                int deletedPosts;
                try (PreparedStatement ps = conn.prepareStatement(deletePostSql)) {
                    ps.setInt(1, maBaiDang);
                    if (maNguoiDung != null) {
                        ps.setInt(2, maNguoiDung);
                    }
                    deletedPosts = ps.executeUpdate();
                }

                if (deletedPosts <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                if (tableExists(conn, "xe")) {
                    deleteCarImageData(conn, maXe);
                    try (PreparedStatement ps = conn.prepareStatement(deleteCarSql)) {
                        ps.setInt(1, maXe);
                        ps.setInt(2, maXe);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean deleteBaiDang(int maBaiDang, int maNguoiDung) {
        return deleteBaiDangInternal(maBaiDang, maNguoiDung);
    }

    public boolean deleteBaiDangByAdmin(int maBaiDang) {
        return deleteBaiDangInternal(maBaiDang, null);
    }

    public boolean deleteByXeForAdmin(int maXe) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                List<Integer> requestIds = getRequestIdsByXe(conn, maXe);
                deleteRequestDataByXe(conn, maXe, requestIds);
                deleteConversationDataByXe(conn, maXe);
                if (tableExists(conn, "baidang")) {
                    try (PreparedStatement selectPosts = conn.prepareStatement("SELECT maBaiDang FROM baidang WHERE maXe = ?")) {
                        selectPosts.setInt(1, maXe);
                        try (ResultSet rs = selectPosts.executeQuery()) {
                            while (rs.next()) {
                                deletePostDependentData(conn, rs.getInt("maBaiDang"));
                            }
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM baidang WHERE maXe = ?")) {
                    ps.setInt(1, maXe);
                    ps.executeUpdate();
                }

                if (tableExists(conn, "xe")) {
                    deleteCarImageData(conn, maXe);
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM xe WHERE maXe = ?")) {
                        ps.setInt(1, maXe);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateTrangThai(int maBaiDang, String trangThai) {
        return updateTrangThaiVaGhiChu(maBaiDang, trangThai, null);
    }

    public boolean updateTrangThaiVaGhiChu(int maBaiDang, String trangThai, String ghiChuAdmin) {
        String sql = "UPDATE baidang SET trangThai = ?, ghiChuAdmin = ? WHERE maBaiDang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            if (ghiChuAdmin == null || ghiChuAdmin.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, ghiChuAdmin.trim());
            }
            ps.setInt(3, maBaiDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean incrementLuotXem(int maBaiDang) {
        String sql = "UPDATE baidang SET luotXem = COALESCE(luotXem, 0) + 1 WHERE maBaiDang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maBaiDang);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }
}
