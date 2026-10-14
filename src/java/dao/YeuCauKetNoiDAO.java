package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.YeuCauKetNoi;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YeuCauKetNoiDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(YeuCauKetNoiDAO.class.getName());

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private YeuCauKetNoi mapYeuCauKetNoi(ResultSet rs) throws Exception {
        YeuCauKetNoi item = new YeuCauKetNoi();
        item.setMaYeuCau(rs.getInt("maYeuCau"));
        item.setMaNguoiMua(rs.getInt("maNguoiMua"));
        item.setGiaThamKhao(rs.getDouble("giaThamKhao"));
        item.setNgayTao(rs.getTimestamp("ngayTao"));
        item.setTrangThai(rs.getString("trangThai"));

        if (hasColumn(rs, "maXe")) {
            int maXe = rs.getInt("maXe");
            if (!rs.wasNull()) {
                item.setMaXe(maXe);
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
        if (hasColumn(rs, "maNguoiBan")) {
            int maNguoiBan = rs.getInt("maNguoiBan");
            if (!rs.wasNull()) {
                item.setMaNguoiBan(maNguoiBan);
            }
        }
        if (hasColumn(rs, "maHoiThoai")) {
            int maHoiThoai = rs.getInt("maHoiThoai");
            if (!rs.wasNull()) {
                item.setMaHoiThoai(maHoiThoai);
            }
        }
        if (hasColumn(rs, "ghiChuNguoiMua")) {
            item.setGhiChuNguoiMua(rs.getString("ghiChuNguoiMua"));
        }
        if (hasColumn(rs, "maLichHen")) {
            int maLichHen = rs.getInt("maLichHen");
            if (!rs.wasNull()) {
                item.setMaLichHen(maLichHen);
            }
        }
        if (hasColumn(rs, "thoiGianHen")) {
            item.setThoiGianHen(rs.getTimestamp("thoiGianHen"));
        }
        if (hasColumn(rs, "diaDiemHen")) {
            item.setDiaDiemHen(rs.getString("diaDiemHen"));
        }
        if (hasColumn(rs, "ghiChuHen")) {
            item.setGhiChuHen(rs.getString("ghiChuHen"));
        }
        return item;
    }

    private String getBaseYeuCauSql() {
        return "SELECT y.maYeuCau, y.maNguoiMua, y.maNguoiBan, "
                + "y.giaThamKhao, y.ngayTao, y.trangThai, y.maXe, y.maHoiThoai, y.ghiChuNguoiMua, "
                + "x.tenXe, buyer.hoTen AS tenNguoiMua, buyer.email AS emailNguoiMua, "
                + "buyer.soDienThoai AS soDienThoaiNguoiMua, "
                + "seller.hoTen AS tenNguoiBan, seller.email AS emailNguoiBan, "
                + "seller.soDienThoai AS soDienThoaiNguoiBan, "
                + "lh.maLichHen, lh.thoiGianHen, lh.diaDiemHen, lh.ghiChuHen "
                + "FROM yeucaumuaxe y "
                + "LEFT JOIN xe x ON x.maXe = y.maXe "
                + "LEFT JOIN nguoidung buyer ON buyer.maNguoiDung = y.maNguoiMua "
                + "LEFT JOIN nguoidung seller ON seller.maNguoiDung = y.maNguoiBan "
                + "LEFT JOIN lichhenxemxe lh ON lh.maLichHen = ("
                + "    SELECT l2.maLichHen FROM lichhenxemxe l2 "
                + "    WHERE l2.maYeuCau = y.maYeuCau "
                + "    ORDER BY l2.ngayCapNhat DESC, l2.maLichHen DESC LIMIT 1"
                + ")";
    }

    private Integer getMaNguoiBanByMaXe(Connection conn, int maXe) throws SQLException {
        util.DbWork.one(conn, "SELECT maXe FROM xe WHERE maXe=? FOR UPDATE", maXe);
        String approvedSql = "SELECT maNguoiDung FROM baidang "
                + "WHERE maXe = ? AND trangThai = 'da_duyet' "
                + "ORDER BY ngayDang DESC, maBaiDang DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(approvedSql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maNguoiDung");
                }
            }
        }
        return null;
    }

    private boolean hasCompletedTransactionForCar(Connection conn, int maXe) throws SQLException {
        String sql = "SELECT 1 FROM giaodichxe WHERE maXe = ? AND trangThai = 'HOAN_TAT' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasActiveTransactionForCar(Connection conn, int maXe) throws SQLException {
        String sql = "SELECT 1 FROM giaodichxe "
                + "WHERE maXe = ? AND trangThai NOT IN ('HOAN_TAT', 'HUY', 'MOI_TAO') LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer getHoiThoaiHienCo(Connection conn, int maNguoiMua, int maNguoiBan, int maXe) throws SQLException {
        String sql = "SELECT maHoiThoai FROM hoithoai WHERE maNguoiMua = ? AND maNguoiBan = ? AND maXe = ? ORDER BY maHoiThoai DESC LIMIT 1";
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

    private Integer getOrCreateHoiThoai(Connection conn, int maNguoiMua, int maNguoiBan, int maXe) throws SQLException {
        Integer existing = getHoiThoaiHienCo(conn, maNguoiMua, maNguoiBan, maXe);
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
        return getHoiThoaiHienCo(conn, maNguoiMua, maNguoiBan, maXe);
    }

    private void ensureLichHen(Connection conn, int maYeuCau, String trangThai) throws SQLException {
        String checkSql = "SELECT 1 FROM lichhenxemxe WHERE maYeuCau = ? LIMIT 1";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, maYeuCau);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement updatePs = conn.prepareStatement(
                            "UPDATE lichhenxemxe SET trangThai = ?, ngayCapNhat = NOW() WHERE maYeuCau = ?")) {
                        updatePs.setString(1, trangThai);
                        updatePs.setInt(2, maYeuCau);
                        updatePs.executeUpdate();
                    }
                    return;
                }
            }
        }

        try (PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO lichhenxemxe(maYeuCau, thoiGianHen, diaDiemHen, ghiChuHen, trangThai, ngayTao, ngayCapNhat) "
                + "VALUES(?, NULL, NULL, NULL, ?, NOW(), NOW())")) {
            insertPs.setInt(1, maYeuCau);
            insertPs.setString(2, trangThai);
            insertPs.executeUpdate();
        }
    }

    public boolean hasOpenRequestForCar(int maNguoiMua, int maXe) {
        String sql = "SELECT 1 FROM yeucaumuaxe "
                + "WHERE maNguoiMua = ? AND maXe = ? AND trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT') "
                + "LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public int createYeuCauKetNoi(int maNguoiMua, int maXe, double giaThamKhao) {
        return createYeuCauKetNoi(maNguoiMua, maXe, giaThamKhao, null);
    }

    public int createYeuCauKetNoi(int maNguoiMua, int maXe, double giaThamKhao, String ghiChuNguoiMua) {
        return createYeuCauKetNoi(maNguoiMua, maXe, giaThamKhao, ghiChuNguoiMua, null, null, null);
    }

    public int createYeuCauKetNoi(int maNguoiMua, int maXe, double giaThamKhao, String ghiChuNguoiMua,
            Timestamp thoiGianHen, String diaDiemHen, String ghiChuHen) {
        boolean hasAppointment = thoiGianHen != null || (diaDiemHen != null && !diaDiemHen.isBlank());
        if (hasAppointment && (thoiGianHen == null || !thoiGianHen.after(new Timestamp(System.currentTimeMillis()))
                || diaDiemHen == null || diaDiemHen.isBlank() || diaDiemHen.length()>255)) return -1;
        String requestStatus = "CHO_LIEN_HE";
        String insertRequestSql = "INSERT INTO yeucaumuaxe(maNguoiMua, maNguoiBan, maXe, maHoiThoai, giaThamKhao, ghiChuNguoiMua, trangThai, ngayTao, ngayCapNhat) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        String insertAppointmentSql = "INSERT INTO lichhenxemxe(maYeuCau, thoiGianHen, diaDiemHen, ghiChuHen, trangThai, ngayTao, ngayCapNhat) "
                + "VALUES(?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                util.DbWork.one(conn,"SELECT maXe FROM xe WHERE maXe=? FOR UPDATE",maXe);
                util.DbWork.activeUser(conn,maNguoiMua);
                if (!util.DbWork.list(conn,"SELECT maYeuCau FROM yeucaumuaxe WHERE maNguoiMua=? AND maXe=? AND trangThai NOT IN ('DA_HUY','HOAN_TAT')",maNguoiMua,maXe).isEmpty()) {
                    conn.rollback();
                    return -1;
                }
                if (hasCompletedTransactionForCar(conn, maXe) || hasActiveTransactionForCar(conn, maXe)) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return -1;
                }

                Integer maNguoiBan = getMaNguoiBanByMaXe(conn, maXe);
                if (maNguoiBan == null || maNguoiBan <= 0 || maNguoiBan == maNguoiMua) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return -1;
                }

                Integer maHoiThoai = getOrCreateHoiThoai(conn, maNguoiMua, maNguoiBan, maXe);

                int maYeuCau = -1;
                try (PreparedStatement requestPs = conn.prepareStatement(insertRequestSql, Statement.RETURN_GENERATED_KEYS)) {
                    requestPs.setInt(1, maNguoiMua);
                    requestPs.setInt(2, maNguoiBan);
                    requestPs.setInt(3, maXe);
                    if (maHoiThoai == null) {
                        requestPs.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        requestPs.setInt(4, maHoiThoai);
                    }
                    requestPs.setDouble(5, giaThamKhao);
                    requestPs.setString(6, ghiChuNguoiMua == null || ghiChuNguoiMua.isBlank() ? null : ghiChuNguoiMua.trim());
                    requestPs.setString(7, requestStatus);
                    int inserted = requestPs.executeUpdate();
                    if (inserted <= 0) {
                        conn.rollback();
                        conn.setAutoCommit(originalAutoCommit);
                        return -1;
                    }

                    try (ResultSet rs = requestPs.getGeneratedKeys()) {
                        if (rs.next()) {
                            maYeuCau = rs.getInt(1);
                        }
                    }
                }

                if (maYeuCau <= 0) {
                    try (PreparedStatement fallbackPs = conn.prepareStatement("SELECT LAST_INSERT_ID()"); ResultSet rs = fallbackPs.executeQuery()) {
                        if (rs.next()) {
                            maYeuCau = rs.getInt(1);
                        }
                    }
                }

                if (maYeuCau <= 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return -1;
                }

                if (hasAppointment) {
                    try (PreparedStatement appointmentPs = conn.prepareStatement(insertAppointmentSql, Statement.RETURN_GENERATED_KEYS)) {
                        appointmentPs.setInt(1, maYeuCau);
                        if (thoiGianHen == null) {
                            appointmentPs.setNull(2, java.sql.Types.TIMESTAMP);
                        } else {
                            appointmentPs.setTimestamp(2, thoiGianHen);
                        }
                        appointmentPs.setString(3, diaDiemHen == null || diaDiemHen.isBlank() ? null : diaDiemHen.trim());
                        appointmentPs.setString(4, ghiChuHen == null || ghiChuHen.isBlank() ? null : ghiChuHen.trim());
                        appointmentPs.setString(5, requestStatus);
                        int inserted = appointmentPs.executeUpdate();
                        try (ResultSet keys = appointmentPs.getGeneratedKeys()) {
                            if (keys.next()) util.DbWork.update(conn,
                                    "INSERT INTO lichhenxacnhan(maLichHen,nguoiDeXuat) VALUES(?,?)",keys.getInt(1),maNguoiMua);
                        }
                        if (inserted <= 0) {
                            conn.rollback();
                            conn.setAutoCommit(originalAutoCommit);
                            return -1;
                        }
                    }
                }

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                return maYeuCau;
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

    public List<YeuCauKetNoi> getAllYeuCauKetNoi() {
        List<YeuCauKetNoi> list = new ArrayList<>();
        String sql = getBaseYeuCauSql() + " ORDER BY ngayTao DESC, maYeuCau DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapYeuCauKetNoi(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<YeuCauKetNoi> getYeuCauDaGuiByNguoiMua(int maNguoiMua) {
        List<YeuCauKetNoi> list = new ArrayList<>();
        String sql = getBaseYeuCauSql() + " WHERE y.maNguoiMua = ? ORDER BY ngayTao DESC, maYeuCau DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiMua);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapYeuCauKetNoi(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<YeuCauKetNoi> getYeuCauNhanDuocByNguoiBan(int maNguoiBan) {
        List<YeuCauKetNoi> list = new ArrayList<>();
        String sql = getBaseYeuCauSql() + " WHERE y.maNguoiBan = ? ORDER BY ngayTao DESC, maYeuCau DESC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapYeuCauKetNoi(rs));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public YeuCauKetNoi getYeuCauKetNoiById(int maYeuCau) {
        String sql = getBaseYeuCauSql() + " WHERE y.maYeuCau = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maYeuCau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapYeuCauKetNoi(rs);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public boolean updateTrangThaiYeuCau(int maYeuCau, String trangThaiMoi) {
        String updateRequestSql = "UPDATE yeucaumuaxe SET trangThai = ?, ngayCapNhat = NOW() WHERE maYeuCau = ?";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement requestPs = conn.prepareStatement(updateRequestSql)) {
                requestPs.setString(1, trangThaiMoi);
                requestPs.setInt(2, maYeuCau);
                boolean updated = requestPs.executeUpdate() > 0;
                if (!updated) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                ensureLichHen(conn, maYeuCau, trangThaiMoi);
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
}
