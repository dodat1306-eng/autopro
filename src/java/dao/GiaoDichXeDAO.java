package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import model.GiaoDichXe;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GiaoDichXeDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(GiaoDichXeDAO.class.getName());

    public static final String MOI_TAO = "MOI_TAO";
    public static final String CHO_XAC_NHAN_HAI_BEN = "CHO_XAC_NHAN_HAI_BEN";
    public static final String CHO_XAC_NHAN_CON_LAI = "CHO_XAC_NHAN_CON_LAI";
    public static final String HOAN_TAT = "HOAN_TAT";
    public static final String HUY = "HUY";
    public static final String TRANH_CHAP = "TRANH_CHAP";
    private static final String YEU_CAU_HOAN_TAT = "HOAN_TAT";
    private static final String YEU_CAU_DA_HUY = "DA_HUY";
    private static volatile boolean completedCarsSyncedToSoldPosts = false;

    private static final Set<String> USER_CONFIRMABLE_STATUSES = Set.of(
            CHO_XAC_NHAN_HAI_BEN,
            CHO_XAC_NHAN_CON_LAI
    );
    private static final Set<String> ADMIN_ALLOWED_STATUSES = Set.of(
            MOI_TAO,
            CHO_XAC_NHAN_HAI_BEN,
            CHO_XAC_NHAN_CON_LAI,
            HOAN_TAT,
            HUY,
            TRANH_CHAP
    );

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > 2000) {
            return trimmed.substring(0, 2000);
        }
        return trimmed;
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            int index = i + 1;
            if (value == null) {
                ps.setNull(index, Types.NULL);
            } else if (value instanceof Integer) {
                ps.setInt(index, (Integer) value);
            } else if (value instanceof Double) {
                ps.setDouble(index, (Double) value);
            } else if (value instanceof Timestamp) {
                ps.setTimestamp(index, (Timestamp) value);
            } else {
                ps.setString(index, value.toString());
            }
        }
    }

    private GiaoDichXe mapCore(ResultSet rs) throws SQLException {
        GiaoDichXe item = new GiaoDichXe();
        item.setMaGiaoDich(rs.getInt("maGiaoDich"));
        item.setMaYeuCau(rs.getInt("maYeuCau"));
        item.setMaNguoiMua(rs.getInt("maNguoiMua"));
        item.setMaNguoiBan(rs.getInt("maNguoiBan"));
        item.setMaXe(rs.getInt("maXe"));
        double giaChot = rs.getDouble("giaChot");
        if (!rs.wasNull()) {
            item.setGiaChot(giaChot);
        }
        item.setNgayChot(rs.getTimestamp("ngayChot"));
        item.setDiaDiemGiaoXe(rs.getString("diaDiemGiaoXe"));
        if (hasColumn(rs, "soDienThoaiGiaoXe")) {
            item.setSoDienThoaiGiaoXe(rs.getString("soDienThoaiGiaoXe"));
        }
        item.setNgayGiaoXe(rs.getTimestamp("ngayGiaoXe"));
        item.setTrangThai(rs.getString("trangThai"));
        item.setXacNhanNguoiMua(rs.getBoolean("xacNhanNguoiMua"));
        item.setXacNhanNguoiBan(rs.getBoolean("xacNhanNguoiBan"));
        item.setThoiGianXacNhanMua(rs.getTimestamp("thoiGianXacNhanMua"));
        item.setThoiGianXacNhanBan(rs.getTimestamp("thoiGianXacNhanBan"));
        item.setGhiChuNguoiMua(rs.getString("ghiChuNguoiMua"));
        item.setGhiChuNguoiBan(rs.getString("ghiChuNguoiBan"));
        int adminXuLy = rs.getInt("adminXuLy");
        if (!rs.wasNull()) {
            item.setAdminXuLy(adminXuLy);
        }
        item.setNgayXuLy(rs.getTimestamp("ngayXuLy"));
        item.setGhiChuAdmin(rs.getString("ghiChuAdmin"));
        item.setNgayTao(rs.getTimestamp("ngayTao"));
        item.setNgayCapNhat(rs.getTimestamp("ngayCapNhat"));
        return item;
    }

    private GiaoDichXe mapFull(ResultSet rs) throws SQLException {
        GiaoDichXe item = mapCore(rs);
        if (hasColumn(rs, "giaThamKhao")) {
            double giaThamKhao = rs.getDouble("giaThamKhao");
            if (!rs.wasNull()) {
                item.setGiaThamKhao(giaThamKhao);
            }
        }
        if (hasColumn(rs, "tenXe")) {
            item.setTenXe(rs.getString("tenXe"));
        }
        if (hasColumn(rs, "tenNguoiMua")) {
            item.setTenNguoiMua(rs.getString("tenNguoiMua"));
        }
        if (hasColumn(rs, "emailNguoiMua")) {
            item.setEmailNguoiMua(rs.getString("emailNguoiMua"));
        }
        if (hasColumn(rs, "soDienThoaiNguoiMua")) {
            item.setSoDienThoaiNguoiMua(rs.getString("soDienThoaiNguoiMua"));
        }
        if (hasColumn(rs, "tenNguoiBan")) {
            item.setTenNguoiBan(rs.getString("tenNguoiBan"));
        }
        if (hasColumn(rs, "emailNguoiBan")) {
            item.setEmailNguoiBan(rs.getString("emailNguoiBan"));
        }
        if (hasColumn(rs, "soDienThoaiNguoiBan")) {
            item.setSoDienThoaiNguoiBan(rs.getString("soDienThoaiNguoiBan"));
        }
        if (hasColumn(rs, "trangThaiYeuCau")) {
            item.setTrangThaiYeuCau(rs.getString("trangThaiYeuCau"));
        }
        if (hasColumn(rs, "maHoiThoai")) {
            int maHoiThoai = rs.getInt("maHoiThoai");
            if (!rs.wasNull()) {
                item.setMaHoiThoai(maHoiThoai);
            }
        }
        if (hasColumn(rs, "maBaiDang")) {
            int maBaiDang = rs.getInt("maBaiDang");
            if (!rs.wasNull()) {
                item.setMaBaiDang(maBaiDang);
            }
        }
        if (hasColumn(rs, "tieuDeBaiDang")) {
            item.setTieuDeBaiDang(rs.getString("tieuDeBaiDang"));
        }
        if (hasColumn(rs, "trangThaiBaiDang")) {
            item.setTrangThaiBaiDang(rs.getString("trangThaiBaiDang"));
        }
        return item;
    }

    private String getBaseSql() {
        return "SELECT g.*, y.giaThamKhao, y.trangThai AS trangThaiYeuCau, y.maHoiThoai, "
                + "x.tenXe, "
                + "buyer.hoTen AS tenNguoiMua, buyer.email AS emailNguoiMua, buyer.soDienThoai AS soDienThoaiNguoiMua, "
                + "seller.hoTen AS tenNguoiBan, seller.email AS emailNguoiBan, seller.soDienThoai AS soDienThoaiNguoiBan, "
                + "b.maBaiDang, b.tieuDe AS tieuDeBaiDang, b.trangThai AS trangThaiBaiDang "
                + "FROM giaodichxe g "
                + "JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "LEFT JOIN nguoidung buyer ON buyer.maNguoiDung = g.maNguoiMua "
                + "LEFT JOIN nguoidung seller ON seller.maNguoiDung = g.maNguoiBan "
                + "LEFT JOIN baidang b ON b.maBaiDang = ("
                + "    SELECT b2.maBaiDang FROM baidang b2 "
                + "    WHERE b2.maXe = g.maXe "
                + "    ORDER BY b2.ngayDang DESC, b2.maBaiDang DESC LIMIT 1"
                + ")";
    }

    private GiaoDichXe getCoreById(Connection conn, int maGiaoDich, boolean forUpdate) throws SQLException {
        if (forUpdate) {
            java.util.List<java.util.Map<String,Object>> rows = util.DbWork.list(conn, "SELECT maXe FROM giaodichxe WHERE maGiaoDich=?", maGiaoDich);
            if (rows.isEmpty()) return null;
            util.DbWork.one(conn, "SELECT maXe FROM xe WHERE maXe=? FOR UPDATE", rows.get(0).get("maXe"));
        }
        String sql = "SELECT * FROM giaodichxe WHERE maGiaoDich = ?" + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maGiaoDich);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCore(rs);
                }
            }
        }
        return null;
    }

    private GiaoDichXe getById(Connection conn, int maGiaoDich) throws SQLException {
        String sql = getBaseSql() + " WHERE g.maGiaoDich = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maGiaoDich);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFull(rs);
                }
            }
        }
        return null;
    }

    private GiaoDichXe getByYeuCau(Connection conn, int maYeuCau) throws SQLException {
        String sql = getBaseSql() + " WHERE g.maYeuCau = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maYeuCau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFull(rs);
                }
            }
        }
        return null;
    }

    private List<GiaoDichXe> getBySql(String sql, Object... params) {
        List<GiaoDichXe> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapFull(rs));
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return list;
    }

    private void insertNotification(Connection conn, int maNguoiDung, String loai, String tieuDe, String noiDung, String urlDich) throws SQLException {
        String sql = "INSERT INTO thongbao(maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc, ngayTao) "
                + "VALUES(?, ?, ?, ?, ?, 0, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, loai);
            ps.setString(3, tieuDe);
            ps.setString(4, noiDung);
            if (urlDich == null || urlDich.isBlank()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, urlDich);
            }
            ps.executeUpdate();
        }
    }

    private void insertNotificationForAdmins(Connection conn, String loai, String tieuDe, String noiDung, String urlDich) throws SQLException {
        String sql = "SELECT maNguoiDung FROM nguoidung WHERE vaiTro = 'admin'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                insertNotification(conn, rs.getInt("maNguoiDung"), loai, tieuDe, noiDung, urlDich);
            }
        }
    }

    private String buildUserDetailUrl(int maGiaoDich) {
        return "giao-dich/chi-tiet?id=" + maGiaoDich;
    }

    private String buildAdminDetailUrl(int maGiaoDich) {
        return "admin/giao-dich?id=" + maGiaoDich;
    }

    private String resolveTenXe(Connection conn, int maXe) throws SQLException {
        String sql = "SELECT tenXe FROM xe WHERE maXe = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenXe");
                }
            }
        }
        return "Xe #" + maXe;
    }

    private boolean hasCompletedTransactionForCar(Connection conn, int maXe, Integer excludeGiaoDich) throws SQLException {
        String sql = "SELECT 1 FROM giaodichxe WHERE maXe = ? AND trangThai = ?"
                + (excludeGiaoDich != null ? " AND maGiaoDich <> ?" : "")
                + " LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            ps.setString(2, HOAN_TAT);
            if (excludeGiaoDich != null) {
                ps.setInt(3, excludeGiaoDich);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasActiveTransactionForCar(Connection conn, int maXe, Integer excludeGiaoDich) throws SQLException {
        String sql = "SELECT 1 FROM giaodichxe WHERE maXe = ? AND trangThai NOT IN (?, ?, 'MOI_TAO')"
                + (excludeGiaoDich != null ? " AND maGiaoDich <> ?" : "")
                + " LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            ps.setString(2, HOAN_TAT);
            ps.setString(3, HUY);
            if (excludeGiaoDich != null) {
                ps.setInt(4, excludeGiaoDich);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer getApprovedSellerForCar(Connection conn, int maXe) throws SQLException {
        util.DbWork.one(conn, "SELECT maXe FROM xe WHERE maXe=? FOR UPDATE", maXe);
        String sql = "SELECT maNguoiDung FROM baidang "
                + "WHERE maXe = ? AND trangThai = 'da_duyet' "
                + "ORDER BY ngayDang DESC, maBaiDang DESC LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maNguoiDung");
                }
            }
        }
        return null;
    }

    private boolean hasOpenRequestForBuyerCar(Connection conn, int maNguoiMua, int maXe) throws SQLException {
        String sql = "SELECT 1 FROM yeucaumuaxe "
                + "WHERE maNguoiMua = ? AND maXe = ? AND trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT') "
                + "LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer getExistingConversation(Connection conn, int maNguoiMua, int maNguoiBan, int maXe) throws SQLException {
        String sql = "SELECT maHoiThoai FROM hoithoai WHERE maNguoiMua = ? AND maNguoiBan = ? AND maXe = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maNguoiBan);
            ps.setInt(3, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maHoiThoai");
                }
            }
        }
        return null;
    }

    private Integer getOrCreateConversation(Connection conn, int maNguoiMua, int maNguoiBan, int maXe) throws SQLException {
        Integer existing = getExistingConversation(conn, maNguoiMua, maNguoiBan, maXe);
        if (existing != null) {
            return existing;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO hoithoai(maNguoiMua, maNguoiBan, maXe) VALUES(?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maNguoiBan);
            ps.setInt(3, maXe);
            if (ps.executeUpdate() <= 0) {
                return null;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return getExistingConversation(conn, maNguoiMua, maNguoiBan, maXe);
    }

    private void updateYeuCauStatus(Connection conn, int maYeuCau, String trangThai) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE yeucaumuaxe SET trangThai = ?, ngayCapNhat = NOW() WHERE maYeuCau = ?")) {
            ps.setString(1, trangThai);
            ps.setInt(2, maYeuCau);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE lichhenxemxe SET trangThai = ?, ngayCapNhat = NOW() WHERE maYeuCau = ?")) {
            ps.setString(1, trangThai);
            ps.setInt(2, maYeuCau);
            ps.executeUpdate();
        }
    }

    private void markPostAsSold(Connection conn, int maXe) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE baidang SET trangThai = 'da_ban' WHERE maXe = ? AND trangThai IN ('da_duyet', 'canh_bao', 'cho_duyet', 'dang_giao_dich')")) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    private void removeSavedPostsForCar(Connection conn, int maXe) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE y FROM baidang_yeuthich y "
                + "JOIN baidang b ON b.maBaiDang = y.maBaiDang "
                + "WHERE b.maXe = ?")) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    private void markPostAsInTransaction(Connection conn, int maXe) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE baidang SET trangThai = 'dang_giao_dich' WHERE maXe = ? AND trangThai IN ('da_duyet', 'canh_bao', 'cho_duyet')")) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    private void releasePostFromInTransaction(Connection conn, int maXe) throws SQLException {
        if (hasCompletedTransactionForCar(conn, maXe, null) || hasActiveTransactionForCar(conn, maXe, null)) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE baidang SET trangThai = 'da_duyet' WHERE maXe = ? AND trangThai = 'dang_giao_dich'")) {
            ps.setInt(1, maXe);
            ps.executeUpdate();
        }
    }

    public int syncCompletedCarsToSoldPosts() {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                int updated = 0;

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE baidang b SET b.trangThai = 'da_ban' "
                        + "WHERE b.trangThai IN ('da_duyet', 'canh_bao', 'cho_duyet', 'dang_giao_dich') "
                        + "AND EXISTS (SELECT 1 FROM giaodichxe g WHERE g.maXe = b.maXe AND g.trangThai = ?)")) {
                    ps.setString(1, HOAN_TAT);
                    updated += ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE y FROM baidang_yeuthich y "
                        + "JOIN baidang b ON b.maBaiDang = y.maBaiDang "
                        + "JOIN giaodichxe g ON g.maXe = b.maXe AND g.trangThai = ?")) {
                    ps.setString(1, HOAN_TAT);
                    updated += ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE yeucaumuaxe y "
                        + "JOIN giaodichxe g ON g.maYeuCau = y.maYeuCau AND g.trangThai = ? "
                        + "SET y.trangThai = ?, y.ngayCapNhat = NOW() "
                        + "WHERE y.trangThai <> ?")) {
                    ps.setString(1, HOAN_TAT);
                    ps.setString(2, YEU_CAU_HOAN_TAT);
                    ps.setString(3, YEU_CAU_HOAN_TAT);
                    updated += ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE lichhenxemxe l "
                        + "JOIN giaodichxe g ON g.maYeuCau = l.maYeuCau AND g.trangThai = ? "
                        + "SET l.trangThai = ?, l.ngayCapNhat = NOW() "
                        + "WHERE l.trangThai <> ?")) {
                    ps.setString(1, HOAN_TAT);
                    ps.setString(2, YEU_CAU_HOAN_TAT);
                    ps.setString(3, YEU_CAU_HOAN_TAT);
                    updated += ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE lichhenxemxe l "
                        + "JOIN yeucaumuaxe y ON y.maYeuCau = l.maYeuCau "
                        + "JOIN giaodichxe g ON g.maXe = y.maXe AND g.trangThai = ? "
                        + "SET l.trangThai = ?, l.ngayCapNhat = NOW() "
                        + "WHERE y.maYeuCau <> g.maYeuCau "
                        + "AND l.trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT')")) {
                    ps.setString(1, HOAN_TAT);
                    ps.setString(2, YEU_CAU_DA_HUY);
                    updated += ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE yeucaumuaxe y "
                        + "JOIN giaodichxe g ON g.maXe = y.maXe AND g.trangThai = ? "
                        + "SET y.trangThai = ?, y.ngayCapNhat = NOW() "
                        + "WHERE y.maYeuCau <> g.maYeuCau "
                        + "AND y.trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT')")) {
                    ps.setString(1, HOAN_TAT);
                    ps.setString(2, YEU_CAU_DA_HUY);
                    updated += ps.executeUpdate();
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return updated;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return -1;
    }

    public void syncCompletedCarsToSoldPostsOnce() {
        if (completedCarsSyncedToSoldPosts) {
            return;
        }
        synchronized (GiaoDichXeDAO.class) {
            if (completedCarsSyncedToSoldPosts) {
                return;
            }
            // Chỉ đánh dấu đã sync khi DB trả kết quả hợp lệ. Nếu MySQL chưa sẵn sàng
            // lúc ứng dụng khởi động, request sau vẫn có cơ hội đồng bộ lại.
            completedCarsSyncedToSoldPosts = syncCompletedCarsToSoldPosts() >= 0;
        }
    }

    private void cancelOtherRequestsAndTransactions(Connection conn, GiaoDichXe currentTransaction) throws SQLException {
        String tenXe = resolveTenXe(conn, currentTransaction.getMaXe());
        Set<Integer> otherBuyerIds = new LinkedHashSet<>();
        Set<Integer> otherParticipantIds = new LinkedHashSet<>();

        String selectOtherRequestsSql = "SELECT maYeuCau, maNguoiMua FROM yeucaumuaxe "
                + "WHERE maXe = ? AND maYeuCau <> ? AND trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT')";
        List<Integer> otherRequestIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(selectOtherRequestsSql)) {
            ps.setInt(1, currentTransaction.getMaXe());
            ps.setInt(2, currentTransaction.getMaYeuCau());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    otherRequestIds.add(rs.getInt("maYeuCau"));
                    otherBuyerIds.add(rs.getInt("maNguoiMua"));
                }
            }
        }

        if (!otherRequestIds.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE yeucaumuaxe SET trangThai = 'DA_HUY', ngayCapNhat = NOW() "
                    + "WHERE maXe = ? AND maYeuCau <> ? AND trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT')")) {
                ps.setInt(1, currentTransaction.getMaXe());
                ps.setInt(2, currentTransaction.getMaYeuCau());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE lichhenxemxe SET trangThai = 'DA_HUY', ngayCapNhat = NOW() "
                    + "WHERE maYeuCau IN (SELECT maYeuCau FROM yeucaumuaxe WHERE maXe = ? AND maYeuCau <> ?)")) {
                ps.setInt(1, currentTransaction.getMaXe());
                ps.setInt(2, currentTransaction.getMaYeuCau());
                ps.executeUpdate();
            }
        }

        String selectOtherTransactionsSql = "SELECT maGiaoDich, maNguoiMua, maNguoiBan FROM giaodichxe "
                + "WHERE maXe = ? AND maGiaoDich <> ? AND trangThai NOT IN (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(selectOtherTransactionsSql)) {
            ps.setInt(1, currentTransaction.getMaXe());
            ps.setInt(2, currentTransaction.getMaGiaoDich());
            ps.setString(3, HOAN_TAT);
            ps.setString(4, HUY);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    otherParticipantIds.add(rs.getInt("maNguoiMua"));
                    otherParticipantIds.add(rs.getInt("maNguoiBan"));
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE giaodichxe SET trangThai = ?, ngayCapNhat = NOW() "
                + "WHERE maXe = ? AND maGiaoDich <> ? AND trangThai NOT IN (?, ?)")) {
            ps.setString(1, HUY);
            ps.setInt(2, currentTransaction.getMaXe());
            ps.setInt(3, currentTransaction.getMaGiaoDich());
            ps.setString(4, HOAN_TAT);
            ps.setString(5, HUY);
            ps.executeUpdate();
        }

        String body = "Xe \"" + tenXe + "\" đã được xác nhận bán thành công cho giao dịch khác, nên yêu cầu/giao dịch liên quan của bạn được hệ thống tự đóng.";
        for (Integer buyerId : otherBuyerIds) {
            insertNotification(conn, buyerId, "TRANSACTION", "Xe bạn quan tâm đã được bán", body, "lich-hen-cua-toi");
        }
        for (Integer participantId : otherParticipantIds) {
            if (participantId != null
                    && participantId != currentTransaction.getMaNguoiMua()
                    && participantId != currentTransaction.getMaNguoiBan()) {
                insertNotification(conn, participantId, "TRANSACTION", "Một giao dịch khác đã bị đóng", body, "lich-hen-cua-toi");
            }
        }
    }

    private void applyCompletionSideEffects(Connection conn, GiaoDichXe transaction) throws SQLException {
        updateYeuCauStatus(conn, transaction.getMaYeuCau(), YEU_CAU_HOAN_TAT);
        markPostAsSold(conn, transaction.getMaXe());
        removeSavedPostsForCar(conn, transaction.getMaXe());
        cancelOtherRequestsAndTransactions(conn, transaction);
    }

    private String syncStatusAfterConfirmation(Connection conn, GiaoDichXe transaction) throws SQLException {
        String targetStatus;
        if (transaction.isXacNhanNguoiMua() && transaction.isXacNhanNguoiBan()) {
            targetStatus = HOAN_TAT;
        } else if (transaction.isXacNhanNguoiMua() || transaction.isXacNhanNguoiBan()) {
            targetStatus = CHO_XAC_NHAN_CON_LAI;
        } else {
            targetStatus = CHO_XAC_NHAN_HAI_BEN;
        }

        if (HOAN_TAT.equals(targetStatus)) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE giaodichxe SET trangThai = ?, "
                    + "ngayChot = COALESCE(ngayChot, NOW()), "
                    + "ngayGiaoXe = COALESCE(ngayGiaoXe, NOW()), "
                    + "ngayCapNhat = NOW() "
                    + "WHERE maGiaoDich = ?")) {
                ps.setString(1, targetStatus);
                ps.setInt(2, transaction.getMaGiaoDich());
                ps.executeUpdate();
            }
            applyCompletionSideEffects(conn, transaction);
        } else {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE giaodichxe SET trangThai = ?, ngayCapNhat = NOW() WHERE maGiaoDich = ?")) {
                ps.setString(1, targetStatus);
                ps.setInt(2, transaction.getMaGiaoDich());
                ps.executeUpdate();
            }
        }

        transaction.setTrangThai(targetStatus);
        return targetStatus;
    }

    private void notifyTransactionCreated(Connection conn, GiaoDichXe transaction) throws SQLException {
        String userUrl = buildUserDetailUrl(transaction.getMaGiaoDich());
        String adminUrl = buildAdminDetailUrl(transaction.getMaGiaoDich());
        String tenXe = transaction.getTenXe() != null ? transaction.getTenXe() : ("Xe #" + transaction.getMaXe());
        String body = "Hồ sơ giao dịch cho \"" + tenXe + "\" đã được tạo. Người bán cần tiếp nhận trong 48 giờ. Xe chỉ chuyển sang đang giao dịch sau khi được tiếp nhận.";
        insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION", "Đã tạo hồ sơ giao dịch mới", body, userUrl);
        insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION", "Đã tạo hồ sơ giao dịch mới", body, userUrl);
        insertNotificationForAdmins(conn, "TRANSACTION", "Có hồ sơ giao dịch mới", "Giao dịch #" + transaction.getMaGiaoDich() + " vừa được tạo từ yêu cầu #" + transaction.getMaYeuCau() + ".", adminUrl);
    }

    private void notifyWaitingForCounterparty(Connection conn, GiaoDichXe transaction, boolean waitingForSeller) throws SQLException {
        String userUrl = buildUserDetailUrl(transaction.getMaGiaoDich());
        String tenXe = transaction.getTenXe() != null ? transaction.getTenXe() : ("Xe #" + transaction.getMaXe());
        if (waitingForSeller) {
            insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION",
                    "Người mua đã thanh toán và nhận xe",
                    "Người mua vừa xác nhận đã thanh toán ngoài hệ thống và đã nhận xe \"" + tenXe + "\". Hệ thống đang chờ bạn xác nhận đã giao xe để hoàn tất giao dịch.",
                    userUrl);
            insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION",
                    "Đã ghi nhận xác nhận của bạn",
                    "Bạn đã xác nhận đã thanh toán ngoài hệ thống và đã nhận xe \"" + tenXe + "\". Hệ thống đang chờ người bán xác nhận còn lại.",
                    userUrl);
        } else {
            insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION",
                    "Người bán đã xác nhận giao xe",
                    "Người bán vừa xác nhận đã giao xe \"" + tenXe + "\". Hệ thống đang chờ bạn xác nhận đã thanh toán ngoài hệ thống và đã nhận xe để hoàn tất giao dịch.",
                    userUrl);
            insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION",
                    "Đã ghi nhận xác nhận của bạn",
                    "Bạn đã xác nhận đã giao xe \"" + tenXe + "\". Hệ thống đang chờ người mua xác nhận còn lại.",
                    userUrl);
        }
    }

    private void notifyCompleted(Connection conn, GiaoDichXe transaction) throws SQLException {
        String userUrl = buildUserDetailUrl(transaction.getMaGiaoDich());
        String adminUrl = buildAdminDetailUrl(transaction.getMaGiaoDich());
        String tenXe = transaction.getTenXe() != null ? transaction.getTenXe() : ("Xe #" + transaction.getMaXe());
        String body = "Giao dịch cho \"" + tenXe + "\" đã được cả người mua và người bán xác nhận hoàn tất.";
        insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION", "Giao dịch đã hoàn tất", body, userUrl);
        insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION", "Giao dịch đã hoàn tất", body, userUrl);
        insertNotificationForAdmins(conn, "TRANSACTION", "Một giao dịch đã hoàn tất",
                "Giao dịch #" + transaction.getMaGiaoDich() + " đã chuyển sang trạng thái HOAN_TAT.", adminUrl);
    }

    private void notifyCancelled(Connection conn, GiaoDichXe transaction, boolean cancelledByBuyer) throws SQLException {
        String userUrl = buildUserDetailUrl(transaction.getMaGiaoDich());
        String adminUrl = buildAdminDetailUrl(transaction.getMaGiaoDich());
        String tenXe = transaction.getTenXe() != null ? transaction.getTenXe() : ("Xe #" + transaction.getMaXe());
        String actor = cancelledByBuyer ? "Người mua" : "Người bán";
        String body = actor + " vừa hủy giao dịch cho xe \"" + tenXe + "\".";
        insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION", "Giao dịch đã bị hủy", body, userUrl);
        insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION", "Giao dịch đã bị hủy", body, userUrl);
        insertNotificationForAdmins(conn, "TRANSACTION", "Một giao dịch đã bị hủy",
                "Giao dịch #" + transaction.getMaGiaoDich() + " vừa bị hủy bởi " + actor.toLowerCase(Locale.ROOT) + ".", adminUrl);
    }

    private void notifyAdminHandled(Connection conn, GiaoDichXe transaction) throws SQLException {
        String userUrl = buildUserDetailUrl(transaction.getMaGiaoDich());
        String body = "Admin vừa cập nhật hồ sơ giao dịch #" + transaction.getMaGiaoDich() + " sang trạng thái " + transaction.getTrangThai() + ".";
        insertNotification(conn, transaction.getMaNguoiMua(), "TRANSACTION", "Admin đã cập nhật giao dịch", body, userUrl);
        insertNotification(conn, transaction.getMaNguoiBan(), "TRANSACTION", "Admin đã cập nhật giao dịch", body, userUrl);
    }

    public GiaoDichXe createInProgressTransactionForCar(int maNguoiMua, int maXe, double giaThamKhao,
            String ghiChuNguoiMua, String diaDiemGiaoXe, String soDienThoaiGiaoXe) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                util.DbWork.one(conn,"SELECT maXe FROM xe WHERE maXe=? FOR UPDATE",maXe);
                util.DbWork.activeUser(conn,maNguoiMua);
                if (hasCompletedTransactionForCar(conn, maXe, null)
                        || hasActiveTransactionForCar(conn, maXe, null)
                        || hasOpenRequestForBuyerCar(conn, maNguoiMua, maXe)) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                Integer maNguoiBan = getApprovedSellerForCar(conn, maXe);
                if (!util.DbWork.list(conn, "SELECT maGiaoDich FROM giaodichxe WHERE maNguoiMua=? AND maXe=? AND trangThai='MOI_TAO'", maNguoiMua, maXe).isEmpty()) {
                    conn.rollback();
                    return null;
                }
                if (maNguoiBan == null || maNguoiBan <= 0 || maNguoiBan == maNguoiMua) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                Integer maHoiThoai = getOrCreateConversation(conn, maNguoiMua, maNguoiBan, maXe);
                int maYeuCau = -1;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO yeucaumuaxe(maNguoiMua, maNguoiBan, maXe, maHoiThoai, giaThamKhao, ghiChuNguoiMua, trangThai, ngayTao, ngayCapNhat) "
                        + "VALUES(?, ?, ?, ?, ?, ?, 'CHO_LIEN_HE', NOW(), NOW())",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, maNguoiMua);
                    ps.setInt(2, maNguoiBan);
                    ps.setInt(3, maXe);
                    if (maHoiThoai == null) {
                        ps.setNull(4, Types.INTEGER);
                    } else {
                        ps.setInt(4, maHoiThoai);
                    }
                    ps.setDouble(5, giaThamKhao);
                    ps.setString(6, trimToNull(ghiChuNguoiMua));
                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return null;
                    }
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            maYeuCau = rs.getInt(1);
                        }
                    }
                }

                if (maYeuCau <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                int maGiaoDich = -1;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO giaodichxe(maYeuCau, maNguoiMua, maNguoiBan, maXe, giaChot, diaDiemGiaoXe, soDienThoaiGiaoXe, trangThai, ngayTao, ngayCapNhat) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, maYeuCau);
                    ps.setInt(2, maNguoiMua);
                    ps.setInt(3, maNguoiBan);
                    ps.setInt(4, maXe);
                    ps.setDouble(5, giaThamKhao);
                    ps.setString(6, trimToNull(diaDiemGiaoXe));
                    ps.setString(7, trimToNull(soDienThoaiGiaoXe));
                    ps.setString(8, MOI_TAO);
                    if (ps.executeUpdate() <= 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return null;
                    }
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            maGiaoDich = rs.getInt(1);
                        }
                    }
                }

                if (maGiaoDich <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                GiaoDichWorkflowDAO.pendingCreated(conn, maGiaoDich, maNguoiMua);
                GiaoDichXe created = getById(conn, maGiaoDich);
                if (created == null) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                notifyTransactionCreated(conn, created);
                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return created;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return null;
    }

    public GiaoDichXe createFromYeuCau(int maYeuCau) {
        try {
            int transactionId = util.DbWork.transaction(conn -> {
                java.util.Map<String,Object> key = util.DbWork.one(conn, "SELECT maXe FROM yeucaumuaxe WHERE maYeuCau=?", maYeuCau);
                util.DbWork.one(conn, "SELECT maXe FROM xe WHERE maXe=? FOR UPDATE", key.get("maXe"));
                java.util.Map<String,Object> request = util.DbWork.one(conn, "SELECT * FROM yeucaumuaxe WHERE maYeuCau=? FOR UPDATE", maYeuCau);
                GiaoDichXe existing = getByYeuCau(conn, maYeuCau);
                if (existing != null) return existing.getMaGiaoDich();
                int car = util.DbWork.id(request,"maXe");
                util.DbWork.require(Set.of("DA_XEM_XE","DA_CHOT").contains(request.get("trangThai")), "Chưa đủ điều kiện tạo giao dịch.");
                util.DbWork.require(!util.DbWork.list(conn,
                        "SELECT l.maLichHen FROM lichhenxemxe l JOIN lichhenxacnhan v ON v.maLichHen=l.maLichHen WHERE l.maYeuCau=? AND v.trangThai='DA_XEM_XE'", maYeuCau).isEmpty(),
                        "Cần xác nhận đã xem xe trước khi tạo giao dịch.");
                util.DbWork.require(getApprovedSellerForCar(conn,car) != null
                        && !hasCompletedTransactionForCar(conn,car,null) && !hasActiveTransactionForCar(conn,car,null), "Xe không còn sẵn sàng.");
                int id = util.DbWork.insert(conn,
                        "INSERT INTO giaodichxe(maYeuCau,maNguoiMua,maNguoiBan,maXe,giaChot,trangThai) VALUES(?,?,?,?,?,'MOI_TAO')",
                        maYeuCau,request.get("maNguoiMua"),request.get("maNguoiBan"),car,request.get("giaThamKhao"));
                GiaoDichWorkflowDAO.pendingCreated(conn,id,util.DbWork.id(request,"maNguoiMua"));
                notifyTransactionCreated(conn,getById(conn,id));
                return id;
            });
            return getById(transactionId);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Cannot create pending transaction",e);
            return null;
        }
    }

    public GiaoDichXe createInProgressTransactionFromYeuCau(int maYeuCau) {
        return createFromYeuCau(maYeuCau);
    }

    public GiaoDichXe getById(int maGiaoDich) {
        try (Connection conn = getConnection()) {
            return getById(conn, maGiaoDich);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return null;
    }

    public GiaoDichXe getByYeuCau(int maYeuCau) {
        try (Connection conn = getConnection()) {
            return getByYeuCau(conn, maYeuCau);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return null;
    }

    public List<GiaoDichXe> getByBuyer(int maNguoiMua) {
        return getBySql(getBaseSql() + " WHERE g.maNguoiMua = ? ORDER BY g.ngayTao DESC, g.maGiaoDich DESC", maNguoiMua);
    }

    public List<GiaoDichXe> getBySeller(int maNguoiBan) {
        return getBySql(getBaseSql() + " WHERE g.maNguoiBan = ? ORDER BY g.ngayTao DESC, g.maGiaoDich DESC", maNguoiBan);
    }

    public List<GiaoDichXe> getAll(String trangThai) {
        String normalizedStatus = trimToNull(trangThai);
        if (normalizedStatus == null) {
            return getBySql(getBaseSql() + " ORDER BY g.ngayTao DESC, g.maGiaoDich DESC");
        }
        return getBySql(getBaseSql() + " WHERE g.trangThai = ? ORDER BY g.ngayTao DESC, g.maGiaoDich DESC", normalizedStatus);
    }

    public boolean buyerConfirmReceived(int maGiaoDich, int maNguoiMua, String ghiChuNguoiMua) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                GiaoDichXe transaction = getCoreById(conn, maGiaoDich, true);
                if (transaction == null
                        || transaction.getMaNguoiMua() != maNguoiMua
                        || transaction.isXacNhanNguoiMua()
                        || !USER_CONFIRMABLE_STATUSES.contains(transaction.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE giaodichxe SET xacNhanNguoiMua = 1, thoiGianXacNhanMua = NOW(), "
                        + "ghiChuNguoiMua = ?, ngayCapNhat = NOW() WHERE maGiaoDich = ?")) {
                    String note = trimToNull(ghiChuNguoiMua);
                    if (note == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, note);
                    }
                    ps.setInt(2, maGiaoDich);
                    ps.executeUpdate();
                }

                transaction.setXacNhanNguoiMua(true);
                transaction.setGhiChuNguoiMua(trimToNull(ghiChuNguoiMua));
                util.DbWork.activeUser(conn,maNguoiMua);
                String before = transaction.getTrangThai();
                String newStatus = syncStatusAfterConfirmation(conn, transaction);
                util.DbWork.audit(conn, "GIAO_DICH", maGiaoDich, maNguoiMua, "Người mua xác nhận thanh toán và nhận xe", before, newStatus, ghiChuNguoiMua, null);
                GiaoDichXe updated = getById(conn, maGiaoDich);

                if (HOAN_TAT.equals(newStatus) && updated != null) {
                    notifyCompleted(conn, updated);
                } else if (updated != null) {
                    notifyWaitingForCounterparty(conn, updated, true);
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean sellerConfirmDelivered(int maGiaoDich, int maNguoiBan, String ghiChuNguoiBan) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                GiaoDichXe transaction = getCoreById(conn, maGiaoDich, true);
                if (transaction == null
                        || transaction.getMaNguoiBan() != maNguoiBan
                        || transaction.isXacNhanNguoiBan()
                        || !USER_CONFIRMABLE_STATUSES.contains(transaction.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE giaodichxe SET xacNhanNguoiBan = 1, thoiGianXacNhanBan = NOW(), "
                        + "ghiChuNguoiBan = ?, ngayCapNhat = NOW() WHERE maGiaoDich = ?")) {
                    String note = trimToNull(ghiChuNguoiBan);
                    if (note == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, note);
                    }
                    ps.setInt(2, maGiaoDich);
                    ps.executeUpdate();
                }

                transaction.setXacNhanNguoiBan(true);
                transaction.setGhiChuNguoiBan(trimToNull(ghiChuNguoiBan));
                util.DbWork.activeUser(conn,maNguoiBan);
                String before = transaction.getTrangThai();
                String newStatus = syncStatusAfterConfirmation(conn, transaction);
                util.DbWork.audit(conn, "GIAO_DICH", maGiaoDich, maNguoiBan, "Người bán xác nhận giao xe", before, newStatus, ghiChuNguoiBan, null);
                GiaoDichXe updated = getById(conn, maGiaoDich);

                if (HOAN_TAT.equals(newStatus) && updated != null) {
                    notifyCompleted(conn, updated);
                } else if (updated != null) {
                    notifyWaitingForCounterparty(conn, updated, false);
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean cancelTransaction(int maGiaoDich, int actorId, String ghiChuHuy) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                GiaoDichXe transaction = getCoreById(conn, maGiaoDich, true);
                if (transaction == null
                        || (transaction.getMaNguoiMua() != actorId && transaction.getMaNguoiBan() != actorId)
                        || HOAN_TAT.equals(transaction.getTrangThai())
                        || HUY.equals(transaction.getTrangThai())
                        || TRANH_CHAP.equals(transaction.getTrangThai())
                        || transaction.isXacNhanNguoiMua() || transaction.isXacNhanNguoiBan()) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                util.DbWork.activeUser(conn,actorId);
                util.DbWork.require(ghiChuHuy != null && !ghiChuHuy.isBlank(), "Vui lòng nhập lý do hủy.");
                util.DbWork.audit(conn, "GIAO_DICH", maGiaoDich, actorId, "Hủy / từ chối giao dịch", transaction.getTrangThai(), HUY, ghiChuHuy, null);
                boolean cancelledByBuyer = transaction.getMaNguoiMua() == actorId;
                String sql = cancelledByBuyer
                        ? "UPDATE giaodichxe SET trangThai = ?, ghiChuNguoiMua = ?, ngayCapNhat = NOW() WHERE maGiaoDich = ?"
                        : "UPDATE giaodichxe SET trangThai = ?, ghiChuNguoiBan = ?, ngayCapNhat = NOW() WHERE maGiaoDich = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, HUY);
                    String note = trimToNull(ghiChuHuy);
                    if (note == null) {
                        ps.setNull(2, Types.VARCHAR);
                    } else {
                        ps.setString(2, note);
                    }
                    ps.setInt(3, maGiaoDich);
                    ps.executeUpdate();
                }

                updateYeuCauStatus(conn, transaction.getMaYeuCau(), "DA_HUY");
                releasePostFromInTransaction(conn, transaction.getMaXe());
                GiaoDichXe updated = getById(conn, maGiaoDich);
                if (updated != null) {
                    notifyCancelled(conn, updated, cancelledByBuyer);
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean adminUpdateStatus(int maGiaoDich, String trangThai, int adminId, String ghiChuAdmin) {
        String normalizedStatus = trimToNull(trangThai);
        if (normalizedStatus == null || !ADMIN_ALLOWED_STATUSES.contains(normalizedStatus)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                GiaoDichXe transaction = getCoreById(conn, maGiaoDich, true);
                if (transaction == null) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                util.DbWork.activeUser(conn,adminId);
                util.DbWork.require(!util.DbWork.list(conn,"SELECT maNguoiDung FROM nguoidung WHERE maNguoiDung=? AND vaiTro='admin'",adminId).isEmpty(), "Không có quyền quản trị.");
                util.DbWork.require(ghiChuAdmin != null && ghiChuAdmin.trim().length()>=10, "Cần ghi lý do xử lý ít nhất 10 ký tự.");
                String before = transaction.getTrangThai();
                boolean allowed = TRANH_CHAP.equals(before)
                        ? Set.of(HUY,CHO_XAC_NHAN_HAI_BEN,CHO_XAC_NHAN_CON_LAI,HOAN_TAT).contains(normalizedStatus)
                        : Set.of(CHO_XAC_NHAN_HAI_BEN,CHO_XAC_NHAN_CON_LAI).contains(before) && TRANH_CHAP.equals(normalizedStatus);
                util.DbWork.require(allowed, "Không thể chuyển giữa các trạng thái này.");
                util.DbWork.require(!HOAN_TAT.equals(normalizedStatus) || (transaction.isXacNhanNguoiMua() && transaction.isXacNhanNguoiBan()),
                        "Admin không được xác nhận thay hai bên. Hãy mở lại giao dịch để hai bên xác nhận.");
                if (Set.of(CHO_XAC_NHAN_HAI_BEN,CHO_XAC_NHAN_CON_LAI).contains(normalizedStatus)) {
                    normalizedStatus = transaction.isXacNhanNguoiMua() || transaction.isXacNhanNguoiBan()
                            ? CHO_XAC_NHAN_CON_LAI : CHO_XAC_NHAN_HAI_BEN;
                }
                String sql = "UPDATE giaodichxe SET trangThai=?,adminXuLy=?,ngayXuLy=NOW(),ghiChuAdmin=?,ngayCapNhat=NOW() WHERE maGiaoDich=?";
                util.DbWork.audit(conn,"GIAO_DICH",maGiaoDich,adminId,"Admin xử lý khiếu nại",before,normalizedStatus,ghiChuAdmin,null);

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, normalizedStatus);
                    ps.setInt(2, adminId);
                    String note = trimToNull(ghiChuAdmin);
                    if (note == null) {
                        ps.setNull(3, Types.VARCHAR);
                    } else {
                        ps.setString(3, note);
                    }
                    ps.setInt(4, maGiaoDich);
                    ps.executeUpdate();
                }

                if (HOAN_TAT.equals(normalizedStatus)) {
                    transaction.setTrangThai(HOAN_TAT);
                    applyCompletionSideEffects(conn, transaction);
                } else if (HUY.equals(normalizedStatus)) {
                    updateYeuCauStatus(conn, transaction.getMaYeuCau(), "DA_HUY");
                    releasePostFromInTransaction(conn, transaction.getMaXe());
                }

                GiaoDichXe updated = getById(conn, maGiaoDich);
                if (updated != null) {
                    notifyAdminHandled(conn, updated);
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean updateStatusAutomaticallyIfBothConfirmed(int maGiaoDich) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                GiaoDichXe transaction = getCoreById(conn, maGiaoDich, true);
                if (transaction == null || !USER_CONFIRMABLE_STATUSES.contains(transaction.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }
                syncStatusAfterConfirmation(conn, transaction);
                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean hasCompletedTransactionForCar(int maXe) {
        try (Connection conn = getConnection()) {
            return hasCompletedTransactionForCar(conn, maXe, null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }

    public boolean hasActiveTransactionForCar(int maXe) {
        try (Connection conn = getConnection()) {
            return hasActiveTransactionForCar(conn, maXe, null);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", ex);
        }
        return false;
    }
}
