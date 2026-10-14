package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import model.NguoiDung;
import util.PasswordUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NguoiDungDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(NguoiDungDAO.class.getName());

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

    private String normalizeEmail(String email) {
        String normalized = trimToEmpty(email);
        return normalized.isEmpty() ? "" : normalized.toLowerCase();
    }

    private String normalizePhone(String phone) {
        String normalized = trimToEmpty(phone);
        if (normalized.isEmpty()) {
            return "";
        }
        return normalized.replaceAll("[^0-9+]", "");
    }

    private NguoiDung mapUser(ResultSet rs) throws Exception {
        NguoiDung user = new NguoiDung(
                rs.getInt("maNguoiDung"),
                rs.getString("hoTen"),
                rs.getString("email"),
                rs.getString("soDienThoai"),
                rs.getString("matKhau"),
                rs.getString("vaiTro"),
                rs.getString("trangThai")
        );
        user.setOtpReset(rs.getString("otpReset"));
        user.setOtpHetHan(rs.getTimestamp("otpHetHan"));
        if (hasColumn(rs, "anhDaiDien")) {
            user.setAnhDaiDien(rs.getString("anhDaiDien"));
        }
        return user;
    }

    private String buildIdentifierClause(String normalizedEmail, String normalizedPhone) {
        List<String> conditions = new ArrayList<>();
        if (!normalizedEmail.isEmpty()) {
            conditions.add("LOWER(COALESCE(email, '')) = ?");
        }
        if (!normalizedPhone.isEmpty()) {
            conditions.add("COALESCE(soDienThoai, '') = ?");
        }
        return String.join(" OR ", conditions);
    }

    private int bindIdentifierParams(PreparedStatement ps, int startIndex, String normalizedEmail, String normalizedPhone) throws SQLException {
        int index = startIndex;
        if (!normalizedEmail.isEmpty()) {
            ps.setString(index++, normalizedEmail);
        }
        if (!normalizedPhone.isEmpty()) {
            ps.setString(index++, normalizedPhone);
        }
        return index;
    }

    private NguoiDung findUserByIdentifier(Connection conn, String normalizedEmail, String normalizedPhone) throws Exception {
        String identifierClause = buildIdentifierClause(normalizedEmail, normalizedPhone);
        if (identifierClause.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM nguoidung WHERE " + identifierClause + " LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindIdentifierParams(ps, 1, normalizedEmail, normalizedPhone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    private void upgradeLegacyPassword(Connection conn, int maNguoiDung, String rawPassword) throws SQLException {
        String sql = "UPDATE nguoidung SET matKhau = ? WHERE maNguoiDung = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(trimToEmpty(rawPassword)));
            ps.setInt(2, maNguoiDung);
            ps.executeUpdate();
        }
    }

    public NguoiDung login(String identifier, String matKhau) {
        String normalizedEmail = normalizeEmail(identifier);
        String normalizedPhone = normalizePhone(identifier);
        String rawPassword = trimToEmpty(matKhau);
        try (Connection conn = getConnection()) {
            NguoiDung user = findUserByIdentifier(conn, normalizedEmail, normalizedPhone);
            if (user == null || "blocked".equalsIgnoreCase(user.getTrangThai())) {
                return null;
            }

            if (!PasswordUtil.verifyPassword(rawPassword, user.getMatKhau())) {
                return null;
            }

            if (!PasswordUtil.isHashed(user.getMatKhau())) {
                upgradeLegacyPassword(conn, user.getMaNguoiDung(), rawPassword);
                user.setMatKhau(PasswordUtil.hashPassword(rawPassword));
            }
            return user;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean register(NguoiDung user) {
        String sql = "INSERT INTO nguoidung(hoTen, email, soDienThoai, matKhau, vaiTro, trangThai, otpReset, otpHetHan) "
                + "VALUES(?, ?, ?, ?, ?, ?, NULL, NULL)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimToEmpty(user.getHoTen()));

            String email = normalizeEmail(user.getEmail());
            if (email.isEmpty()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, email);
            }

            String phone = normalizePhone(user.getSoDienThoai());
            if (phone.isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, phone);
            }

            ps.setString(4, PasswordUtil.hashPassword(trimToEmpty(user.getMatKhau())));
            ps.setString(5, trimToEmpty(user.getVaiTro()).isEmpty() ? "buyer" : user.getVaiTro());
            ps.setString(6, trimToEmpty(user.getTrangThai()).isEmpty() ? "active" : user.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public List<NguoiDung> getAllUsers() {
        List<NguoiDung> list = new ArrayList<>();
        String sql = "SELECT * FROM nguoidung ORDER BY maNguoiDung DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        return list;
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(conn.getCatalog(), null, tableName, null)) {
            return rs.next();
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

    private int bindIds(PreparedStatement ps, int startIndex, List<Integer> ids) throws SQLException {
        int index = startIndex;
        for (Integer id : ids) {
            ps.setInt(index++, id);
        }
        return index;
    }

    private List<Integer> getOwnedPostIds(Connection conn, int maNguoiDung) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        if (!tableExists(conn, "baidang")) {
            return ids;
        }

        String sql = "SELECT maBaiDang FROM baidang WHERE maNguoiDung = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("maBaiDang"));
                }
            }
        }
        return ids;
    }

    private List<Integer> getOwnedCarIds(Connection conn, int maNguoiDung) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        if (!tableExists(conn, "baidang")) {
            return ids;
        }

        String sql = "SELECT DISTINCT maXe FROM baidang WHERE maNguoiDung = ? AND maXe IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("maXe"));
                }
            }
        }
        return ids;
    }

    private Set<Integer> getRelatedRequestIds(Connection conn, int maNguoiDung, List<Integer> ownedCarIds) throws SQLException {
        Set<Integer> requestIds = new LinkedHashSet<>();
        if (!tableExists(conn, "yeucaumuaxe")) {
            return requestIds;
        }

        StringBuilder sql = new StringBuilder("SELECT maYeuCau FROM yeucaumuaxe WHERE maNguoiMua = ? OR maNguoiBan = ?");
        if (!ownedCarIds.isEmpty()) {
            sql.append(" OR maXe IN (").append(buildPlaceholders(ownedCarIds.size())).append(")");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, maNguoiDung);
            ps.setInt(index++, maNguoiDung);
            if (!ownedCarIds.isEmpty()) {
                bindIds(ps, index, ownedCarIds);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestIds.add(rs.getInt("maYeuCau"));
                }
            }
        }

        return requestIds;
    }

    private void deleteRelatedReports(Connection conn, int maNguoiDung, List<Integer> ownedPostIds) throws SQLException {
        if (!tableExists(conn, "baocao")) {
            return;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM baocao WHERE maNguoiBaoCao = ?");
        if (!ownedPostIds.isEmpty()) {
            sql.append(" OR maBaiDang IN (").append(buildPlaceholders(ownedPostIds.size())).append(")");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, maNguoiDung);
            if (!ownedPostIds.isEmpty()) {
                bindIds(ps, index, ownedPostIds);
            }
            ps.executeUpdate();
        }
    }

    private void deleteRelatedNotifications(Connection conn, int maNguoiDung) throws SQLException {
        if (!tableExists(conn, "thongbao")) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM thongbao WHERE maNguoiDung = ?")) {
            ps.setInt(1, maNguoiDung);
            ps.executeUpdate();
        }
    }

    private void deleteRelatedSavedPosts(Connection conn, int maNguoiDung, List<Integer> ownedPostIds) throws SQLException {
        if (!tableExists(conn, "baidang_yeuthich")) {
            return;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM baidang_yeuthich WHERE maNguoiDung = ?");
        if (!ownedPostIds.isEmpty()) {
            sql.append(" OR maBaiDang IN (").append(buildPlaceholders(ownedPostIds.size())).append(")");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, maNguoiDung);
            if (!ownedPostIds.isEmpty()) {
                bindIds(ps, index, ownedPostIds);
            }
            ps.executeUpdate();
        }
    }

    private void deleteRelatedSellerProfiles(Connection conn, int maNguoiDung) throws SQLException {
        if (!tableExists(conn, "dangkyseller")) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("UPDATE dangkyseller SET nguoiPheDuyet = NULL WHERE nguoiPheDuyet = ?")) {
            ps.setInt(1, maNguoiDung);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dangkyseller WHERE maNguoiDung = ?")) {
            ps.setInt(1, maNguoiDung);
            ps.executeUpdate();
        }
    }

    private void deleteRelatedMessagesAndRooms(Connection conn, int maNguoiDung, List<Integer> ownedCarIds) throws SQLException {
        if (!tableExists(conn, "hoithoai")) {
            return;
        }

        StringBuilder roomFilter = new StringBuilder("maNguoiMua = ? OR maNguoiBan = ?");
        if (!ownedCarIds.isEmpty()) {
            roomFilter.append(" OR maXe IN (").append(buildPlaceholders(ownedCarIds.size())).append(")");
        }

        if (tableExists(conn, "tinnhan")) {
            String senderColumn = hasColumn(conn, "tinnhan", "maNguoiGui") ? "maNguoiGui" : "nguoiGui";
            String deleteMessageSql = "DELETE FROM tinnhan WHERE " + senderColumn + " = ? "
                    + "OR maHoiThoai IN (SELECT maHoiThoai FROM hoithoai WHERE " + roomFilter + ")";
            try (PreparedStatement ps = conn.prepareStatement(deleteMessageSql)) {
                int index = 1;
                ps.setInt(index++, maNguoiDung);
                ps.setInt(index++, maNguoiDung);
                ps.setInt(index++, maNguoiDung);
                if (!ownedCarIds.isEmpty()) {
                    bindIds(ps, index, ownedCarIds);
                }
                ps.executeUpdate();
            }
        }

        String deleteRoomSql = "DELETE FROM hoithoai WHERE " + roomFilter;
        try (PreparedStatement ps = conn.prepareStatement(deleteRoomSql)) {
            int index = 1;
            ps.setInt(index++, maNguoiDung);
            ps.setInt(index++, maNguoiDung);
            if (!ownedCarIds.isEmpty()) {
                bindIds(ps, index, ownedCarIds);
            }
            ps.executeUpdate();
        }
    }

    private void deleteRelatedRequests(Connection conn, int maNguoiDung, List<Integer> ownedCarIds, Set<Integer> requestIds) throws SQLException {
        if (!tableExists(conn, "yeucaumuaxe")) {
            return;
        }

        if (tableExists(conn, "giaodichxe")) {
            StringBuilder transactionSql = new StringBuilder("DELETE FROM giaodichxe WHERE maNguoiMua = ? OR maNguoiBan = ?");
            if (!ownedCarIds.isEmpty()) {
                transactionSql.append(" OR maXe IN (").append(buildPlaceholders(ownedCarIds.size())).append(")");
            }
            if (!requestIds.isEmpty()) {
                transactionSql.append(" OR maYeuCau IN (").append(buildPlaceholders(requestIds.size())).append(")");
            }
            try (PreparedStatement ps = conn.prepareStatement(transactionSql.toString())) {
                int index = 1;
                ps.setInt(index++, maNguoiDung);
                ps.setInt(index++, maNguoiDung);
                if (!ownedCarIds.isEmpty()) {
                    index = bindIds(ps, index, ownedCarIds);
                }
                if (!requestIds.isEmpty()) {
                    bindIds(ps, index, new ArrayList<>(requestIds));
                }
                ps.executeUpdate();
            }
        }

        if (!requestIds.isEmpty() && tableExists(conn, "lichhenxemxe")) {
            String sql = "DELETE FROM lichhenxemxe WHERE maYeuCau IN (" + buildPlaceholders(requestIds.size()) + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bindIds(ps, 1, new ArrayList<>(requestIds));
                ps.executeUpdate();
            }
        }

        StringBuilder sql = new StringBuilder("DELETE FROM yeucaumuaxe WHERE maNguoiMua = ? OR maNguoiBan = ?");
        if (!ownedCarIds.isEmpty()) {
            sql.append(" OR maXe IN (").append(buildPlaceholders(ownedCarIds.size())).append(")");
        }
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, maNguoiDung);
            ps.setInt(index++, maNguoiDung);
            if (!ownedCarIds.isEmpty()) {
                bindIds(ps, index, ownedCarIds);
            }
            ps.executeUpdate();
        }
    }

    private void deleteOwnedPostsAndCars(Connection conn, int maNguoiDung, List<Integer> ownedCarIds) throws SQLException {
        if (tableExists(conn, "baidang")) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM baidang WHERE maNguoiDung = ?")) {
                ps.setInt(1, maNguoiDung);
                ps.executeUpdate();
            }
        }

        if (!ownedCarIds.isEmpty() && tableExists(conn, "xe") && tableExists(conn, "baidang")) {
            if (tableExists(conn, "xe_hinhanh")) {
                String deleteImagesSql = "DELETE FROM xe_hinhanh WHERE maXe IN (" + buildPlaceholders(ownedCarIds.size()) + ") "
                        + "AND NOT EXISTS (SELECT 1 FROM baidang WHERE baidang.maXe = xe_hinhanh.maXe)";
                try (PreparedStatement ps = conn.prepareStatement(deleteImagesSql)) {
                    bindIds(ps, 1, ownedCarIds);
                    ps.executeUpdate();
                }
            }

            String sql = "DELETE FROM xe WHERE maXe IN (" + buildPlaceholders(ownedCarIds.size()) + ") "
                    + "AND NOT EXISTS (SELECT 1 FROM baidang WHERE baidang.maXe = xe.maXe)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bindIds(ps, 1, ownedCarIds);
                ps.executeUpdate();
            }
        }
    }

    public boolean deleteUser(int id) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                boolean userExists;
                try (PreparedStatement checkPs = conn.prepareStatement("SELECT 1 FROM nguoidung WHERE maNguoiDung = ? LIMIT 1")) {
                    checkPs.setInt(1, id);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        userExists = rs.next();
                    }
                }

                if (!userExists) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                List<Integer> ownedPostIds = getOwnedPostIds(conn, id);
                List<Integer> ownedCarIds = getOwnedCarIds(conn, id);
                Set<Integer> requestIds = getRelatedRequestIds(conn, id, ownedCarIds);

                deleteRelatedNotifications(conn, id);
                deleteRelatedSavedPosts(conn, id, ownedPostIds);
                deleteRelatedReports(conn, id, ownedPostIds);
                deleteRelatedSellerProfiles(conn, id);
                deleteRelatedRequests(conn, id, ownedCarIds, requestIds);
                deleteRelatedMessagesAndRooms(conn, id, ownedCarIds);
                deleteOwnedPostsAndCars(conn, id, ownedCarIds);

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM nguoidung WHERE maNguoiDung = ?")) {
                    ps.setInt(1, id);
                    boolean deleted = ps.executeUpdate() > 0;
                    if (!deleted) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return false;
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

    public boolean updateTrangThai(int id, String trangThai) {
        String sql = "UPDATE nguoidung SET trangThai=? WHERE maNguoiDung=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateVaiTro(int id, String vaiTro) {
        String sql = "UPDATE nguoidung SET vaiTro=? WHERE maNguoiDung=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vaiTro);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public NguoiDung getUserById(int maNguoiDung) {
        String sql = "SELECT * FROM nguoidung WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean checkEmailExists(String email) {
        String sql = "SELECT 1 FROM nguoidung WHERE LOWER(COALESCE(email, '')) = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeEmail(email));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean checkPhoneExists(String phone) {
        String sql = "SELECT 1 FROM nguoidung WHERE COALESCE(soDienThoai, '') = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizePhone(phone));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean emailInUseByOtherUser(String email, int excludeUserId) {
        String sql = "SELECT 1 FROM nguoidung WHERE LOWER(COALESCE(email, '')) = ? AND maNguoiDung <> ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizeEmail(email));
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean phoneInUseByOtherUser(String phone, int excludeUserId) {
        String sql = "SELECT 1 FROM nguoidung WHERE COALESCE(soDienThoai, '') = ? AND maNguoiDung <> ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizePhone(phone));
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean updateProfile(int maNguoiDung, String hoTen, String email, String soDienThoai, String anhDaiDien) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPhone = normalizePhone(soDienThoai);
        String normalizedName = trimToEmpty(hoTen);
        boolean updateAvatar = anhDaiDien != null;
        String sql = updateAvatar
                ? "UPDATE nguoidung SET hoTen = ?, email = ?, soDienThoai = ?, anhDaiDien = ? WHERE maNguoiDung = ?"
                : "UPDATE nguoidung SET hoTen = ?, email = ?, soDienThoai = ? WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (updateAvatar && !hasColumn(conn, "nguoidung", "anhDaiDien")) {
                try (PreparedStatement alter = conn.prepareStatement("ALTER TABLE nguoidung ADD COLUMN anhDaiDien VARCHAR(255) NULL")) {
                    alter.executeUpdate();
                }
            }
            int index = 1;
            ps.setString(index++, normalizedName);
            if (normalizedEmail.isEmpty()) {
                ps.setNull(index++, java.sql.Types.VARCHAR);
            } else {
                ps.setString(index++, normalizedEmail);
            }
            if (normalizedPhone.isEmpty()) {
                ps.setNull(index++, java.sql.Types.VARCHAR);
            } else {
                ps.setString(index++, normalizedPhone);
            }
            if (updateAvatar) {
                if (trimToEmpty(anhDaiDien).isEmpty()) {
                    ps.setNull(index++, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(index++, trimToEmpty(anhDaiDien));
                }
            }
            ps.setInt(index, maNguoiDung);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
            try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM nguoidung WHERE maNguoiDung = ? LIMIT 1")) {
                check.setInt(1, maNguoiDung);
                try (ResultSet rs = check.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public NguoiDung findByIdentifier(String identifier) {
        String normalizedEmail = normalizeEmail(identifier);
        String normalizedPhone = normalizePhone(identifier);
        String identifierClause = buildIdentifierClause(normalizedEmail, normalizedPhone);
        if (identifierClause.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM nguoidung WHERE " + identifierClause + " LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindIdentifierParams(ps, 1, normalizedEmail, normalizedPhone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean saveResetOtp(int maNguoiDung, String otp, Timestamp expiry) {
        String sql = "UPDATE nguoidung SET otpReset = ?, otpHetHan = ? WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, otp);
            ps.setTimestamp(2, expiry);
            ps.setInt(3, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public NguoiDung verifyResetOtp(String identifier, String otp) {
        String normalizedEmail = normalizeEmail(identifier);
        String normalizedPhone = normalizePhone(identifier);
        String identifierClause = buildIdentifierClause(normalizedEmail, normalizedPhone);
        if (identifierClause.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM nguoidung "
                + "WHERE (" + identifierClause + ") "
                + "AND otpReset = ? AND otpHetHan IS NOT NULL AND otpHetHan >= NOW() "
                + "LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = bindIdentifierParams(ps, 1, normalizedEmail, normalizedPhone);
            ps.setString(index, trimToEmpty(otp));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean updatePassword(int maNguoiDung, String newPassword) {
        String sql = "UPDATE nguoidung SET matKhau = ?, otpReset = NULL, otpHetHan = NULL WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPassword(trimToEmpty(newPassword)));
            ps.setInt(2, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public boolean clearResetOtp(int maNguoiDung) {
        String sql = "UPDATE nguoidung SET otpReset = NULL, otpHetHan = NULL WHERE maNguoiDung = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }
}
