package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.TinNhan;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TinNhanDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(TinNhanDAO.class.getName());

    private String resolveSenderColumn(Connection conn) throws Exception {
        return hasColumn(conn, "tinnhan", "maNguoiGui") ? "maNguoiGui" : "nguoiGui";
    }

    public List<TinNhan> getTinNhanByHoiThoai(int maHoiThoai) {
        List<TinNhan> list = new ArrayList<>();
        String sql = "SELECT * FROM tinnhan WHERE maHoiThoai=? ORDER BY thoiGian ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String senderColumn = resolveSenderColumn(conn);
            boolean hasTrangThai = hasColumn(conn, "tinnhan", "trangThai");

            ps.setInt(1, maHoiThoai);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TinNhan tn = new TinNhan();
                tn.setMaTinNhan(rs.getInt("maTinNhan"));
                tn.setMaHoiThoai(rs.getInt("maHoiThoai"));
                tn.setMaNguoiGui(rs.getInt(senderColumn));
                tn.setNoiDung(rs.getString("noiDung"));
                tn.setThoiGian(rs.getTimestamp("thoiGian"));
                tn.setTrangThai(hasTrangThai ? rs.getString("trangThai") : "ChuaDoc");
                list.add(tn);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        return list;
    }

    public boolean sendTinNhan(int maHoiThoai, int maNguoiGui, String noiDung) {
        try (Connection conn = getConnection()) {
            String senderColumn = resolveSenderColumn(conn);
            boolean hasTrangThai = hasColumn(conn, "tinnhan", "trangThai");
            String sql;

            if (hasTrangThai) {
                sql = "INSERT INTO tinnhan(maHoiThoai, " + senderColumn + ", noiDung, thoiGian, trangThai) VALUES(?,?,?,NOW(),'ChuaDoc')";
            } else {
                sql = "INSERT INTO tinnhan(maHoiThoai, " + senderColumn + ", noiDung, thoiGian) VALUES(?,?,?,NOW())";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, maHoiThoai);
                ps.setInt(2, maNguoiGui);
                ps.setString(3, noiDung);
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        return false;
    }
}
