package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoiThoaiDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(HoiThoaiDAO.class.getName());

    private String extractThumbnail(String imageList) {
        if (imageList == null || imageList.isBlank()) {
            return "https://via.placeholder.com/160x160?text=No+Image";
        }

        for (String image : imageList.split(",")) {
            if (image != null && !image.isBlank()) {
                return image.trim();
            }
        }
        return "https://via.placeholder.com/160x160?text=No+Image";
    }

    public int checkHoiThoaiTonTai(int maNguoiMua, int maNguoiBan, int maXe) {
        String sql = "SELECT maHoiThoai FROM hoithoai WHERE maNguoiMua = ? AND maNguoiBan = ? AND maXe = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maNguoiBan);
            ps.setInt(3, maXe);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maHoiThoai");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public int taoHoiThoaiMoi(int maNguoiMua, int maNguoiBan, int maXe) {
        String sql = "INSERT INTO hoithoai (maNguoiMua, maNguoiBan, maXe) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, maNguoiMua);
            ps.setInt(2, maNguoiBan);
            ps.setInt(3, maXe);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public boolean isParticipant(int maHoiThoai, int maNguoiDung) {
        String sql = "SELECT 1 FROM hoithoai WHERE maHoiThoai = ? AND (maNguoiMua = ? OR maNguoiBan = ?) LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maHoiThoai);
            ps.setInt(2, maNguoiDung);
            ps.setInt(3, maNguoiDung);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    public Map<String, Object> getThongTinHoiThoai(int maHoiThoai, int maNguoiDung) {
        String sql = "SELECT h.maHoiThoai, h.maXe, "
                + "CASE WHEN h.maNguoiMua = ? THEN h.maNguoiBan ELSE h.maNguoiMua END AS doiTacId, "
                + "CASE WHEN h.maNguoiMua = ? THEN seller.hoTen ELSE buyer.hoTen END AS doiTacTen, "
                + "x.tenXe, x.hinhAnh "
                + "FROM hoithoai h "
                + "JOIN nguoidung buyer ON buyer.maNguoiDung = h.maNguoiMua "
                + "JOIN nguoidung seller ON seller.maNguoiDung = h.maNguoiBan "
                + "LEFT JOIN xe x ON x.maXe = h.maXe "
                + "WHERE h.maHoiThoai = ? AND (h.maNguoiMua = ? OR h.maNguoiBan = ?) "
                + "LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, maNguoiDung);
            ps.setInt(3, maHoiThoai);
            ps.setInt(4, maNguoiDung);
            ps.setInt(5, maNguoiDung);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("maHoiThoai", rs.getInt("maHoiThoai"));
                    info.put("maXe", rs.getInt("maXe"));
                    info.put("doiTacId", rs.getInt("doiTacId"));
                    info.put("doiTacTen", rs.getString("doiTacTen"));
                    info.put("tenXe", rs.getString("tenXe"));
                    info.put("anhXe", extractThumbnail(rs.getString("hinhAnh")));
                    return info;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return null;
    }

    public List<Map<String, Object>> getDanhSachHoiThoai(int maNguoiDung) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT h.maHoiThoai, h.maNguoiMua, h.maNguoiBan, h.maXe, "
                + "CASE WHEN h.maNguoiMua = ? THEN h.maNguoiBan ELSE h.maNguoiMua END AS doiTacId, "
                + "CASE WHEN h.maNguoiMua = ? THEN seller.hoTen ELSE buyer.hoTen END AS doiTacTen, "
                + "x.tenXe, x.hinhAnh, "
                + "COALESCE(tn.noiDung, 'Chưa có tin nhắn nào') AS tinNhanCuoi, "
                + "tn.thoiGian AS thoiGianCuoi "
                + "FROM hoithoai h "
                + "JOIN nguoidung buyer ON buyer.maNguoiDung = h.maNguoiMua "
                + "JOIN nguoidung seller ON seller.maNguoiDung = h.maNguoiBan "
                + "LEFT JOIN xe x ON x.maXe = h.maXe "
                + "LEFT JOIN tinnhan tn ON tn.maTinNhan = ("
                + "    SELECT t2.maTinNhan FROM tinnhan t2 "
                + "    WHERE t2.maHoiThoai = h.maHoiThoai "
                + "    ORDER BY t2.thoiGian DESC, t2.maTinNhan DESC LIMIT 1"
                + ") "
                + "WHERE h.maNguoiMua = ? OR h.maNguoiBan = ? "
                + "ORDER BY COALESCE(tn.thoiGian, '1970-01-01 00:00:00') DESC, h.maHoiThoai DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNguoiDung);
            ps.setInt(2, maNguoiDung);
            ps.setInt(3, maNguoiDung);
            ps.setInt(4, maNguoiDung);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("maHoiThoai", rs.getInt("maHoiThoai"));
                    map.put("maNguoiMua", rs.getInt("maNguoiMua"));
                    map.put("maNguoiBan", rs.getInt("maNguoiBan"));
                    map.put("maXe", rs.getInt("maXe"));
                    map.put("doiTacId", rs.getInt("doiTacId"));
                    map.put("doiTacTen", rs.getString("doiTacTen"));
                    map.put("tenXe", rs.getString("tenXe"));
                    map.put("anhXe", extractThumbnail(rs.getString("hinhAnh")));
                    map.put("tinNhanCuoi", rs.getString("tinNhanCuoi"));
                    map.put("thoiGianCuoi", rs.getTimestamp("thoiGianCuoi"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public int getOrCreateHoiThoai(int maNguoiMua, int maNguoiBan, int maXe) {
        if (maNguoiMua == maNguoiBan) {
            return 0;
        }

        int existingRoom = checkHoiThoaiTonTai(maNguoiMua, maNguoiBan, maXe);
        if (existingRoom > 0) {
            return existingRoom;
        }

        return taoHoiThoaiMoi(maNguoiMua, maNguoiBan, maXe);
    }
}
