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
import java.util.List;
import model.DangKySeller;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DangKySellerDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(DangKySellerDAO.class.getName());

    public static final String CHO_DUYET = "CHO_DUYET";
    public static final String DA_DUYET = "DA_DUYET";
    public static final String TU_CHOI = "TU_CHOI";
    public static final String DA_XOA = "DA_XOA";

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String trimToNull(String value) {
        String trimmed = trimToEmpty(value);
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeEmail(String email) {
        String normalized = trimToEmpty(email).toLowerCase();
        return normalized;
    }

    private String normalizePhone(String phone) {
        String normalized = trimToEmpty(phone);
        return normalized.replaceAll("[^0-9+]", "");
    }

    private String normalizeCccd(String soCCCD) {
        return trimToEmpty(soCCCD).replaceAll("[^0-9]", "");
    }

    private DangKySeller mapDangKySeller(ResultSet rs) throws SQLException {
        DangKySeller item = new DangKySeller();
        item.setMaDangKySeller(rs.getInt("maDangKySeller"));
        item.setMaNguoiDung(rs.getInt("maNguoiDung"));
        item.setHoTen(rs.getString("hoTen"));
        item.setSoDienThoai(rs.getString("soDienThoai"));
        item.setEmail(rs.getString("email"));
        item.setSoCCCD(rs.getString("soCCCD"));
        item.setAnhCCCDMatTruoc(rs.getString("anhCCCDMatTruoc"));
        item.setAnhCCCDMatSau(rs.getString("anhCCCDMatSau"));
        item.setMaTinh(rs.getInt("maTinh"));
        item.setDiaChiChiTiet(rs.getString("diaChiChiTiet"));
        item.setGhiChuNguoiDangKy(rs.getString("ghiChuNguoiDangKy"));
        item.setTrangThai(rs.getString("trangThai"));
        item.setLyDoTuChoi(rs.getString("lyDoTuChoi"));

        int nguoiPheDuyet = rs.getInt("nguoiPheDuyet");
        if (!rs.wasNull()) {
            item.setNguoiPheDuyet(nguoiPheDuyet);
        }

        item.setNgayPheDuyet(rs.getTimestamp("ngayPheDuyet"));
        item.setNgayTao(rs.getTimestamp("ngayTao"));
        item.setNgayCapNhat(rs.getTimestamp("ngayCapNhat"));

        if (hasColumn(rs, "tenTinh")) {
            item.setTenTinh(rs.getString("tenTinh"));
        }
        if (hasColumn(rs, "mien")) {
            item.setMien(rs.getString("mien"));
        }
        if (hasColumn(rs, "tenNguoiPheDuyet")) {
            item.setTenNguoiPheDuyet(rs.getString("tenNguoiPheDuyet"));
        }
        if (hasColumn(rs, "tenNguoiDung")) {
            item.setTenNguoiDung(rs.getString("tenNguoiDung"));
        }
        if (hasColumn(rs, "emailNguoiDung")) {
            item.setEmailNguoiDung(rs.getString("emailNguoiDung"));
        }
        return item;
    }

    private String getBaseSql() {
        return "SELECT d.*, t.tenTinh, t.mien, "
                + "u.hoTen AS tenNguoiDung, u.email AS emailNguoiDung, "
                + "admin.hoTen AS tenNguoiPheDuyet "
                + "FROM dangkyseller d "
                + "LEFT JOIN tinhthanh t ON t.maTinh = d.maTinh "
                + "LEFT JOIN nguoidung u ON u.maNguoiDung = d.maNguoiDung "
                + "LEFT JOIN nguoidung admin ON admin.maNguoiDung = d.nguoiPheDuyet ";
    }

    private void bindNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        String normalized = trimToNull(value);
        if (normalized == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, normalized);
        }
    }

    private void insertNotification(Connection conn, int maNguoiDung, String loai,
            String tieuDe, String noiDung, String urlDich) throws SQLException {
        String sql = "INSERT INTO thongbao(maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc, ngayTao) "
                + "VALUES(?, ?, ?, ?, ?, 0, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, trimToEmpty(loai));
            ps.setString(3, trimToEmpty(tieuDe));
            ps.setString(4, trimToEmpty(noiDung));
            bindNullableString(ps, 5, urlDich);
            ps.executeUpdate();
        }
    }

    private void insertNotificationForAdmins(Connection conn, String loai,
            String tieuDe, String noiDung, String urlDich) throws SQLException {
        String sql = "INSERT INTO thongbao(maNguoiDung, loai, tieuDe, noiDung, urlDich, daDoc, ngayTao) "
                + "SELECT maNguoiDung, ?, ?, ?, ?, 0, NOW() FROM nguoidung WHERE vaiTro = 'admin'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(loai));
            ps.setString(2, trimToEmpty(tieuDe));
            ps.setString(3, trimToEmpty(noiDung));
            bindNullableString(ps, 4, urlDich);
            ps.executeUpdate();
        }
    }

    private boolean hasPendingRequest(Connection conn, int maNguoiDung) throws SQLException {
        String sql = "SELECT 1 FROM dangkyseller WHERE maNguoiDung = ? AND trangThai = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, CHO_DUYET);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasApprovedSellerRole(Connection conn, int maNguoiDung) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM dangkyseller WHERE maNguoiDung = ? AND trangThai = 'DA_DUYET') AS coSeller";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("coSeller");
                }
            }
        }
        return false;
    }

    private boolean existsEmailForOtherUser(Connection conn, String email, int excludeUserId) throws SQLException {
        String normalized = normalizeEmail(email);
        if (normalized.isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM nguoidung WHERE LOWER(COALESCE(email, '')) = ? AND maNguoiDung <> ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalized);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existsPhoneForOtherUser(Connection conn, String phone, int excludeUserId) throws SQLException {
        String normalized = normalizePhone(phone);
        if (normalized.isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM nguoidung WHERE COALESCE(soDienThoai, '') = ? AND maNguoiDung <> ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalized);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existsCccdForOtherUser(Connection conn, String soCCCD, int excludeUserId) throws SQLException {
        String normalized = normalizeCccd(soCCCD);
        if (normalized.isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM dangkyseller "
                + "WHERE soCCCD = ? AND maNguoiDung <> ? AND trangThai <> ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalized);
            ps.setInt(2, excludeUserId);
            ps.setString(3, DA_XOA);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private DangKySeller getById(Connection conn, int maDangKySeller, boolean forUpdate) throws SQLException {
        String sql = getBaseSql() + "WHERE d.maDangKySeller = ? LIMIT 1"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maDangKySeller);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDangKySeller(rs);
                }
            }
        }
        return null;
    }

    private void updateUserFromApprovedRequest(Connection conn, DangKySeller request) throws SQLException {
        String sql = "UPDATE nguoidung SET hoTen = ?, vaiTro = 'seller' WHERE maNguoiDung = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(request.getHoTen()));
            ps.setInt(2, request.getMaNguoiDung());
            ps.executeUpdate();
        }
    }

    private int getFallbackTinhId(Connection conn) throws SQLException {
        String sql = "SELECT maTinh FROM tinhthanh ORDER BY maTinh LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("maTinh");
            }
        }
        return 1;
    }

    private String buildLegacyCccd(int maNguoiDung) {
        long normalizedId = Math.abs((long) maNguoiDung) % 1000000L;
        return String.format("900000%06d", normalizedId);
    }

    private DangKySeller getLatestNonDeletedByUser(Connection conn, int maNguoiDung) throws SQLException {
        String sql = getBaseSql()
                + "WHERE d.maNguoiDung = ? AND d.trangThai <> ? "
                + "ORDER BY d.ngayTao DESC, d.maDangKySeller DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, DA_XOA);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDangKySeller(rs);
                }
            }
        }
        return null;
    }

    private DangKySeller getLatestNonDeletedByUser(int maNguoiDung) {
        try (Connection conn = getConnection()) {
            return getLatestNonDeletedByUser(conn, maNguoiDung);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    private DangKySeller approveExistingSellerProfile(int maDangKySeller) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                DangKySeller profile = getById(conn, maDangKySeller, true);
                if (profile == null || DA_XOA.equals(profile.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return profile;
                }
                if (!DA_DUYET.equals(profile.getTrangThai())) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dangkyseller SET trangThai = ?, lyDoTuChoi = NULL, "
                            + "ngayPheDuyet = COALESCE(ngayPheDuyet, NOW()), ngayCapNhat = NOW() "
                            + "WHERE maDangKySeller = ?")) {
                        ps.setString(1, DA_DUYET);
                        ps.setInt(2, maDangKySeller);
                        ps.executeUpdate();
                    }
                }
                DangKySeller updated = getById(conn, maDangKySeller, false);
                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return updated;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean createRegistration(DangKySeller dangKySeller) {
        if (dangKySeller == null) {
            return false;
        }

        String sql = "INSERT INTO dangkyseller(maNguoiDung, hoTen, soDienThoai, email, soCCCD, "
                + "anhCCCDMatTruoc, anhCCCDMatSau, maTinh, diaChiChiTiet, ghiChuNguoiDangKy, "
                + "trangThai, lyDoTuChoi, nguoiPheDuyet, ngayPheDuyet, ngayTao, ngayCapNhat) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, NOW(), NOW())";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (hasPendingRequest(conn, dangKySeller.getMaNguoiDung()) || hasApprovedSellerRole(conn, dangKySeller.getMaNguoiDung())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }
                if (existsEmailForOtherUser(conn, dangKySeller.getEmail(), dangKySeller.getMaNguoiDung())
                        || existsPhoneForOtherUser(conn, dangKySeller.getSoDienThoai(), dangKySeller.getMaNguoiDung())
                        || existsCccdForOtherUser(conn, dangKySeller.getSoCCCD(), dangKySeller.getMaNguoiDung())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                ps.setInt(1, dangKySeller.getMaNguoiDung());
                ps.setString(2, trimToEmpty(dangKySeller.getHoTen()));
                ps.setString(3, normalizePhone(dangKySeller.getSoDienThoai()));
                ps.setString(4, normalizeEmail(dangKySeller.getEmail()));
                ps.setString(5, normalizeCccd(dangKySeller.getSoCCCD()));
                ps.setString(6, trimToEmpty(dangKySeller.getAnhCCCDMatTruoc()));
                ps.setString(7, trimToEmpty(dangKySeller.getAnhCCCDMatSau()));
                ps.setInt(8, dangKySeller.getMaTinh());
                bindNullableString(ps, 9, dangKySeller.getDiaChiChiTiet());
                bindNullableString(ps, 10, dangKySeller.getGhiChuNguoiDangKy());
                ps.setString(11, CHO_DUYET);

                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        dangKySeller.setMaDangKySeller(rs.getInt(1));
                    }
                }

                insertNotification(conn, dangKySeller.getMaNguoiDung(),
                        "SELLER_SUBMITTED",
                        "Đã gửi hồ sơ đăng ký người bán",
                        "Hồ sơ seller của bạn đã được gửi tới admin. Bạn sẽ nhận thông báo ngay khi có kết quả xét duyệt.",
                        "seller-register/status");
                insertNotificationForAdmins(conn,
                        "SELLER_REVIEW",
                        "Có hồ sơ seller mới cần duyệt",
                        "Một người dùng vừa gửi hồ sơ xác minh người bán. Vui lòng mở danh sách hồ sơ seller để kiểm tra.",
                        "admin/seller-requests");

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

    public DangKySeller getById(int maDangKySeller) {
        try (Connection conn = getConnection()) {
            return getById(conn, maDangKySeller, false);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public DangKySeller getByUserId(int maNguoiDung) {
        String sql = getBaseSql() + "WHERE d.maNguoiDung = ? ORDER BY d.ngayTao DESC, d.maDangKySeller DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDangKySeller(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public DangKySeller ensureApprovedSellerProfile(int maNguoiDung, String hoTen, String email, String soDienThoai) {
        DangKySeller existingProfile = getLatestNonDeletedByUser(maNguoiDung);
        if (existingProfile != null) {
            if (DA_DUYET.equals(existingProfile.getTrangThai())) {
                return existingProfile;
            }
            return approveExistingSellerProfile(existingProfile.getMaDangKySeller());
        }

        String sql = "INSERT INTO dangkyseller(maNguoiDung, hoTen, soDienThoai, email, soCCCD, "
                + "anhCCCDMatTruoc, anhCCCDMatSau, maTinh, diaChiChiTiet, ghiChuNguoiDangKy, "
                + "trangThai, lyDoTuChoi, nguoiPheDuyet, ngayPheDuyet, ngayTao, ngayCapNhat) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?, NULL, NULL, NOW(), NOW(), NOW())";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                DangKySeller profileCreatedByAnotherRequest = getLatestNonDeletedByUser(conn, maNguoiDung);
                if (profileCreatedByAnotherRequest != null) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return profileCreatedByAnotherRequest;
                }

                String normalizedName = trimToEmpty(hoTen);
                String normalizedEmail = normalizeEmail(email);
                String normalizedPhone = normalizePhone(soDienThoai);
                if (normalizedName.isEmpty()) {
                    normalizedName = "Seller #" + maNguoiDung;
                }
                if (normalizedEmail.isEmpty()) {
                    normalizedEmail = "seller" + maNguoiDung + "@autopro.local";
                }
                if (normalizedPhone.isEmpty()) {
                    normalizedPhone = "Chua cap nhat";
                }

                ps.setInt(1, maNguoiDung);
                ps.setString(2, normalizedName);
                ps.setString(3, normalizedPhone);
                ps.setString(4, normalizedEmail);
                ps.setString(5, buildLegacyCccd(maNguoiDung));
                ps.setString(6, "uploads/seller-verify/chua-cap-nhat-mat-truoc.svg");
                ps.setString(7, "uploads/seller-verify/chua-cap-nhat-mat-sau.svg");
                ps.setInt(8, getFallbackTinhId(conn));
                ps.setString(9, "Ho so seller duoc tao tu tai khoan da co quyen seller truoc do. Nguoi ban co the bo sung thong tin trong trang Ho so cua toi.");
                ps.setString(10, DA_DUYET);

                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return null;
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return getByUserId(maNguoiDung);
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public List<DangKySeller> getHistoryByUserId(int maNguoiDung) {
        List<DangKySeller> list = new ArrayList<>();
        String sql = getBaseSql() + "WHERE d.maNguoiDung = ? ORDER BY d.ngayTao DESC, d.maDangKySeller DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDangKySeller(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public boolean syncLatestContactInfo(int maNguoiDung, String hoTen, String email, String soDienThoai) {
        String sql = "UPDATE dangkyseller d "
                + "JOIN (SELECT maDangKySeller FROM dangkyseller "
                + "WHERE maNguoiDung = ? AND trangThai <> ? "
                + "ORDER BY ngayTao DESC, maDangKySeller DESC LIMIT 1) latest "
                + "ON latest.maDangKySeller = d.maDangKySeller "
                + "SET d.hoTen = ?, d.email = ?, d.soDienThoai = ?, d.ngayCapNhat = NOW()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, DA_XOA);
            ps.setString(3, trimToEmpty(hoTen));
            ps.setString(4, normalizeEmail(email));
            ps.setString(5, normalizePhone(soDienThoai));
            if (ps.executeUpdate() > 0) {
                return true;
            }
            return getLatestNonDeletedByUser(maNguoiDung) != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateLatestSellerExtraInfo(int maNguoiDung, int maTinh, String diaChiChiTiet, String ghiChuNguoiDangKy) {
        String sql = "UPDATE dangkyseller d "
                + "JOIN (SELECT maDangKySeller FROM dangkyseller "
                + "WHERE maNguoiDung = ? AND trangThai <> ? "
                + "ORDER BY ngayTao DESC, maDangKySeller DESC LIMIT 1) latest "
                + "ON latest.maDangKySeller = d.maDangKySeller "
                + "SET d.maTinh = ?, d.diaChiChiTiet = ?, d.ghiChuNguoiDangKy = ?, d.ngayCapNhat = NOW()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, DA_XOA);
            ps.setInt(3, maTinh);
            bindNullableString(ps, 4, diaChiChiTiet);
            bindNullableString(ps, 5, ghiChuNguoiDangKy);
            if (ps.executeUpdate() > 0) {
                return true;
            }
            return getLatestNonDeletedByUser(maNguoiDung) != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateLatestSellerIdentityInfo(int maNguoiDung, String soCCCD,
            String anhCCCDMatTruoc, String anhCCCDMatSau) {
        try {
            return util.DbWork.transaction(conn -> {
                util.DbWork.activeUser(conn,maNguoiDung);
                DangKySeller current = getLatestNonDeletedByUser(conn,maNguoiDung);
                util.DbWork.require(current != null && util.DbWork.list(conn,"SELECT maDangKySeller FROM dangkyseller WHERE maNguoiDung=? AND trangThai='CHO_DUYET'",maNguoiDung).isEmpty(), "Đã có hồ sơ chờ duyệt.");
                util.DbWork.require(!existsCccdForOtherUser(conn,normalizeCccd(soCCCD),maNguoiDung), "CCCD đã được sử dụng.");
                String front=trimToEmpty(anhCCCDMatTruoc).isEmpty()?current.getAnhCCCDMatTruoc():anhCCCDMatTruoc;
                String back=trimToEmpty(anhCCCDMatSau).isEmpty()?current.getAnhCCCDMatSau():anhCCCDMatSau;
                int id=util.DbWork.insert(conn,
                        "INSERT INTO dangkyseller(maNguoiDung,hoTen,soDienThoai,email,soCCCD,anhCCCDMatTruoc,anhCCCDMatSau,maTinh,diaChiChiTiet,ghiChuNguoiDangKy,trangThai) VALUES(?,?,?,?,?,?,?,?,?,?,'CHO_DUYET')",
                        maNguoiDung,current.getHoTen(),current.getSoDienThoai(),current.getEmail(),normalizeCccd(soCCCD),
                        front,back,current.getMaTinh(),current.getDiaChiChiTiet(),"Cập nhật CCCD; thay thế hồ sơ #" + current.getMaDangKySeller());
                util.DbWork.audit(conn,"SELLER",id,maNguoiDung,"Gửi thay đổi CCCD",null,CHO_DUYET,"Hồ sơ trước #"+current.getMaDangKySeller(),null);
                insertNotification(conn,maNguoiDung,"SELLER_REVIEW","Đã gửi CCCD để xét duyệt","Thông tin mới đang chờ xét duyệt. Hồ sơ đã xác minh trước đó được giữ lại.","seller-register/status");
                for (java.util.Map<String,Object> admin : util.DbWork.list(conn,"SELECT maNguoiDung FROM nguoidung WHERE vaiTro='admin' AND trangThai='active'"))
                    insertNotification(conn,util.DbWork.id(admin,"maNguoiDung"),"SELLER_REVIEW","Có thay đổi CCCD chờ duyệt","Vui lòng kiểm tra hồ sơ cập nhật.","admin/seller-request-detail?id="+id);
                return true;
            });
        } catch (Exception e) { LOGGER.log(Level.WARNING,"Cannot submit identity revision",e); return false; }
    }

    public List<DangKySeller> getPendingList() {
        return getListByStatus(CHO_DUYET);
    }

    public List<DangKySeller> getListByStatus(String trangThai) {
        return searchRequests("", trangThai);
    }

    public List<DangKySeller> searchRequests(String keyword, String trangThai) {
        List<DangKySeller> list = new ArrayList<>();
        String normalizedKeyword = trimToEmpty(keyword).toLowerCase();
        String normalizedStatus = trimToEmpty(trangThai);

        StringBuilder sql = new StringBuilder(getBaseSql())
                .append("WHERE 1 = 1 ");

        if (normalizedStatus.isEmpty() || "ALL".equalsIgnoreCase(normalizedStatus)) {
            sql.append("AND d.trangThai <> ? ");
        } else {
            sql.append("AND d.trangThai = ? ");
        }

        if (!normalizedKeyword.isEmpty()) {
            sql.append("AND (")
                    .append("LOWER(COALESCE(d.hoTen, '')) LIKE ? ")
                    .append("OR LOWER(COALESCE(d.email, '')) LIKE ? ")
                    .append("OR COALESCE(d.soDienThoai, '') LIKE ? ")
                    .append("OR COALESCE(d.soCCCD, '') LIKE ?")
                    .append(") ");
        }

        sql.append("ORDER BY CASE d.trangThai ")
                .append("WHEN 'CHO_DUYET' THEN 0 ")
                .append("WHEN 'TU_CHOI' THEN 1 ")
                .append("WHEN 'DA_DUYET' THEN 2 ")
                .append("ELSE 3 END, d.ngayTao DESC, d.maDangKySeller DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (normalizedStatus.isEmpty() || "ALL".equalsIgnoreCase(normalizedStatus)) {
                ps.setString(index++, DA_XOA);
            } else {
                ps.setString(index++, normalizedStatus);
            }

            if (!normalizedKeyword.isEmpty()) {
                String likeValue = "%" + normalizedKeyword + "%";
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
                ps.setString(index++, likeValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDangKySeller(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public boolean approveSeller(int maDangKySeller, int adminId) {
        String sql = "UPDATE dangkyseller SET trangThai = ?, lyDoTuChoi = NULL, nguoiPheDuyet = ?, "
                + "ngayPheDuyet = NOW() WHERE maDangKySeller = ?";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                DangKySeller request = getById(conn, maDangKySeller, true);
                if (request == null || !CHO_DUYET.equals(request.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }
                if (existsEmailForOtherUser(conn, request.getEmail(), request.getMaNguoiDung())
                        || existsPhoneForOtherUser(conn, request.getSoDienThoai(), request.getMaNguoiDung())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                ps.setString(1, DA_DUYET);
                ps.setInt(2, adminId);
                ps.setInt(3, maDangKySeller);
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                util.DbWork.activeUser(conn,adminId);
                util.DbWork.require(!util.DbWork.list(conn,"SELECT maNguoiDung FROM nguoidung WHERE maNguoiDung=? AND vaiTro='admin'",adminId).isEmpty(),"Không có quyền quản trị.");
                util.DbWork.audit(conn,"SELLER",maDangKySeller,adminId,"Phê duyệt hồ sơ",CHO_DUYET,DA_DUYET,null,null);
                updateUserFromApprovedRequest(conn, request);
                insertNotification(conn, request.getMaNguoiDung(),
                        "SELLER_APPROVED",
                        "Hồ sơ seller đã được phê duyệt",
                        "Admin đã phê duyệt hồ sơ người bán của bạn. Tài khoản của bạn hiện có thể đăng tin bán xe.",
                        "seller-register/status");
                insertNotification(conn, request.getMaNguoiDung(),
                        "SELLER_ROLE_GRANTED",
                        "Bạn đã có quyền đăng tin bán xe",
                        "Quyền seller đã được kích hoạt trên tài khoản. Bạn có thể vào ngay trang đăng tin để tạo bài bán xe.",
                        "post-car");

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

    public boolean rejectSeller(int maDangKySeller, int adminId, String lyDoTuChoi) {
        String sql = "UPDATE dangkyseller SET trangThai = ?, lyDoTuChoi = ?, nguoiPheDuyet = ?, "
                + "ngayPheDuyet = NOW() WHERE maDangKySeller = ?";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                DangKySeller request = getById(conn, maDangKySeller, true);
                if (request == null || !CHO_DUYET.equals(request.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                util.DbWork.activeUser(conn,adminId);
                util.DbWork.require(!util.DbWork.list(conn,"SELECT maNguoiDung FROM nguoidung WHERE maNguoiDung=? AND vaiTro='admin'",adminId).isEmpty(),"Không có quyền quản trị.");
                util.DbWork.require(lyDoTuChoi!=null && !lyDoTuChoi.isBlank(),"Vui lòng nhập lý do từ chối.");
                util.DbWork.audit(conn,"SELLER",maDangKySeller,adminId,"Từ chối hồ sơ",CHO_DUYET,TU_CHOI,lyDoTuChoi,null);
                ps.setString(1, TU_CHOI);
                bindNullableString(ps, 2, lyDoTuChoi);
                ps.setInt(3, adminId);
                ps.setInt(4, maDangKySeller);
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                insertNotification(conn, request.getMaNguoiDung(),
                        "SELLER_REJECTED",
                        "Hồ sơ seller chưa được duyệt",
                        "Hồ sơ người bán của bạn đã bị từ chối. Vui lòng xem lý do và nộp lại hồ sơ mới nếu cần.",
                        "seller-register/status");

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

    public boolean deleteSellerRequest(int maDangKySeller, int adminId) {
        String sql = "UPDATE dangkyseller SET trangThai = ? WHERE maDangKySeller = ?";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                DangKySeller request = getById(conn, maDangKySeller, true);
                if (request == null || DA_XOA.equals(request.getTrangThai())) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                ps.setString(1, DA_XOA);
                ps.setInt(2, maDangKySeller);
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                if (request.isDaDuyet()) {
                    try (PreparedStatement revokePs = conn.prepareStatement(
                            "UPDATE nguoidung SET vaiTro = 'buyer' "
                            + "WHERE maNguoiDung = ? AND LOWER(COALESCE(vaiTro, '')) = 'seller' "
                            + "AND NOT EXISTS ("
                            + "    SELECT 1 FROM dangkyseller "
                            + "    WHERE maNguoiDung = ? AND trangThai = ? AND maDangKySeller <> ?"
                            + ")")) {
                        revokePs.setInt(1, request.getMaNguoiDung());
                        revokePs.setInt(2, request.getMaNguoiDung());
                        revokePs.setString(3, DA_DUYET);
                        revokePs.setInt(4, maDangKySeller);
                        revokePs.executeUpdate();
                    }
                }

                String message = request.isDaDuyet()
                        ? "Admin đã xóa hồ sơ seller đã duyệt của bạn. Quyền seller sẽ bị thu hồi nếu tài khoản không còn hồ sơ seller đã duyệt khác."
                        : "Admin đã xóa hồ sơ đăng ký seller của bạn khỏi danh sách xử lý.";
                insertNotification(conn, request.getMaNguoiDung(),
                        "SELLER_DELETED",
                        "Hồ sơ seller đã bị xóa",
                        message,
                        "seller-register/status");

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

    public boolean hasPendingRequest(int maNguoiDung) {
        try (Connection conn = getConnection()) {
            return hasPendingRequest(conn, maNguoiDung);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean hasApprovedSellerRole(int maNguoiDung) {
        try (Connection conn = getConnection()) {
            return hasApprovedSellerRole(conn, maNguoiDung);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateUserToSellerIfApproved(int maNguoiDung) {
        String sql = "SELECT * FROM dangkyseller WHERE maNguoiDung = ? AND trangThai = ? "
                + "ORDER BY ngayPheDuyet DESC, maDangKySeller DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setString(2, DA_DUYET);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                DangKySeller approvedRequest = mapDangKySeller(rs);
                updateUserFromApprovedRequest(conn, approvedRequest);
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean emailInUseByOtherUser(String email, int excludeUserId) {
        try (Connection conn = getConnection()) {
            return existsEmailForOtherUser(conn, email, excludeUserId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean phoneInUseByOtherUser(String phone, int excludeUserId) {
        try (Connection conn = getConnection()) {
            return existsPhoneForOtherUser(conn, phone, excludeUserId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean cccdInUseByOtherUser(String soCCCD, int excludeUserId) {
        try (Connection conn = getConnection()) {
            return existsCccdForOtherUser(conn, soCCCD, excludeUserId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }
}
