package util;

import context.DBContext;
import java.sql.*;
import java.util.*;

public final class DbWork {
    private DbWork() {}
    @FunctionalInterface public interface Work<T> { T run(Connection c) throws Exception; }

    public static <T> T transaction(Work<T> work) throws Exception {
        try (Connection c = new DBContext().getConnection()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        }
    }

    public static PreparedStatement prepare(Connection c, String sql, Object... args) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
        return ps;
    }

    public static int update(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = prepare(c, sql, args)) { return ps.executeUpdate(); }
    }

    public static int insert(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = prepare(c, sql, args)) {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) throw new SQLException("Missing generated key");
                return rs.getInt(1);
            }
        }
    }

    public static List<Map<String,Object>> list(Connection c, String sql, Object... args) throws SQLException {
        List<Map<String,Object>> rows = new ArrayList<>();
        try (PreparedStatement ps = prepare(c, sql, args); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Map<String,Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    Object value=rs.getObject(i);
                    if(value instanceof java.time.LocalDateTime) value=Timestamp.valueOf((java.time.LocalDateTime)value);
                    row.put(md.getColumnLabel(i), value);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    public static Map<String,Object> one(Connection c, String sql, Object... args) throws SQLException {
        List<Map<String,Object>> rows = list(c, sql, args);
        if (rows.isEmpty()) throw new IllegalArgumentException("Không tìm thấy dữ liệu yêu cầu.");
        return rows.get(0);
    }

    public static int id(Map<String,Object> row, String key) { return ((Number)row.get(key)).intValue(); }
    public static boolean flag(Map<String,Object> row, String key) {
        Object value = row.get(key);
        return Boolean.TRUE.equals(value) || value instanceof Number && ((Number)value).intValue() != 0;
    }

    public static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

    public static void activeUser(Connection c, int userId) throws SQLException {
        Map<String,Object> u = one(c,"SELECT trangThai FROM nguoidung WHERE maNguoiDung=? FOR UPDATE",userId);
        require("active".equals(u.get("trangThai")), "Tài khoản không còn hoạt động.");
    }

    public static void notify(Connection c, int userId, String title, String body, String url) throws SQLException {
        update(c,"INSERT INTO thongbao(maNguoiDung,loai,tieuDe,noiDung,urlDich,daDoc,ngayTao) VALUES(?,'WORKFLOW',?,?,?,0,NOW())",userId,title,body,url);
    }

    public static void audit(Connection c, String type, int id, Integer actor, String action,
            String before, String after, String note, String file) throws SQLException {
        update(c,"INSERT INTO nhatkyhoatdong(doiTuong,maDoiTuong,nguoiThucHien,hanhDong,trangThaiCu,trangThaiMoi,ghiChu,tepDinhKem) VALUES(?,?,?,?,?,?,?,?)",
                type,id,actor,action,before,after,note,file);
    }
}
