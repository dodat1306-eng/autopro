package dao;

import context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.BaiDang;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(AdminDAO.class.getName());
    private static final double DEFAULT_PLATFORM_FEE_RATE = 0.01d;

    private String getConfig(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String envValue = System.getenv(key);
        return envValue == null ? "" : envValue.trim();
    }

    public double getPlatformFeeRate() {
        String configuredRate = getConfig("AUTOPRO_PLATFORM_FEE_RATE");
        if (configuredRate.isEmpty()) {
            return DEFAULT_PLATFORM_FEE_RATE;
        }
        try {
            double parsedRate = Double.parseDouble(configuredRate);
            if (parsedRate <= 0) {
                return DEFAULT_PLATFORM_FEE_RATE;
            }
            return parsedRate > 1 ? parsedRate / 100d : parsedRate;
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid AUTOPRO_PLATFORM_FEE_RATE value: {0}", configuredRate);
            return DEFAULT_PLATFORM_FEE_RATE;
        }
    }

    public int countUsers() {
        return getCount("SELECT COUNT(*) FROM nguoidung");
    }

    public int countCars() {
        return getCount("SELECT COUNT(*) FROM xe");
    }

    public int countPendingPosts() {
        return getCount("SELECT COUNT(*) FROM baidang WHERE trangThai = 'cho_duyet'");
    }

    public int countApprovedPosts() {
        return getCount("SELECT COUNT(*) FROM baidang WHERE trangThai IN ('da_duyet', 'dang_giao_dich', 'da_ban')");
    }

    public int countPendingReports() {
        return getCount("SELECT COUNT(*) FROM baocao WHERE trangThai = 'CHO_XU_LY'");
    }

    public int countConnectionRequests() {
        return getCount("SELECT COUNT(*) FROM yeucaumuaxe");
    }

    public int countActiveRequests() {
        return getCount("SELECT COUNT(*) FROM yeucaumuaxe WHERE trangThai NOT IN ('DA_HUY', 'DA_CHOT', 'HOAN_TAT')");
    }

    public int countTransactions() {
        return getCount("SELECT COUNT(*) FROM giaodichxe");
    }

    public int countPendingTransactionConfirmations() {
        return getCount("SELECT COUNT(*) FROM giaodichxe WHERE trangThai IN ('MOI_TAO', 'CHO_XAC_NHAN_HAI_BEN', 'CHO_XAC_NHAN_CON_LAI')");
    }

    public int countCompletedTransactions() {
        return getCount("SELECT COUNT(*) FROM giaodichxe WHERE trangThai = 'HOAN_TAT'");
    }

    public int countDisputedTransactions() {
        return getCount("SELECT COUNT(*) FROM giaodichxe WHERE trangThai = 'TRANH_CHAP'");
    }

    public double sumCompletedTransactionValue() {
        String sql = "SELECT COALESCE(SUM(COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)), 0) "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "WHERE g.trangThai = 'HOAN_TAT'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public double estimateCompletedTransactionProfit() {
        return sumCompletedTransactionValue() * getPlatformFeeRate();
    }

    public double estimateCurrentMonthTransactionProfit() {
        String transactionDate = "COALESCE(g.ngayChot, g.ngayGiaoXe, g.ngayCapNhat, g.ngayTao)";
        String transactionValue = "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)";
        String sql = "SELECT COALESCE(SUM(" + transactionValue + "), 0) "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "WHERE g.trangThai = 'HOAN_TAT' AND " + transactionDate + " >= ?";
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startOfMonth.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1) * getPlatformFeeRate();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return 0;
    }

    public List<BaiDang> getRecentPendingPosts() {
        List<BaiDang> list = new ArrayList<>();
        String sql = "SELECT b.maBaiDang, b.tieuDe, b.ngayDang, x.tenXe, x.gia "
                + "FROM baidang b "
                + "JOIN xe x ON b.maXe = x.maXe "
                + "WHERE b.trangThai = 'cho_duyet' "
                + "ORDER BY b.ngayDang DESC LIMIT 5";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BaiDang b = new BaiDang();
                b.setMaBaiDang(rs.getInt("maBaiDang"));
                b.setTieuDe(rs.getString("tieuDe"));
                b.setGiaBan(rs.getDouble("gia"));
                b.setNgayDang(rs.getTimestamp("ngayDang"));
                b.setTenXe(rs.getString("tenXe"));
                list.add(b);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return list;
    }

    public List<Map<String, Object>> getMonthlyTransactionValueStats(int months) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> transactionByMonth = new LinkedHashMap<>();
        Map<String, Double> valueByMonth = new LinkedHashMap<>();

        LocalDate startMonth = LocalDate.now().withDayOfMonth(1).minusMonths(months - 1L);
        LocalDate currentMonth = startMonth;
        while (!currentMonth.isAfter(LocalDate.now().withDayOfMonth(1))) {
            String monthKey = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            transactionByMonth.put(monthKey, 0);
            valueByMonth.put(monthKey, 0d);
            currentMonth = currentMonth.plusMonths(1);
        }

        String transactionDate = "COALESCE(g.ngayChot, g.ngayGiaoXe, g.ngayCapNhat, g.ngayTao)";
        String transactionValue = "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)";
        String sql = "SELECT DATE_FORMAT(" + transactionDate + ", '%Y-%m') AS thang, COUNT(*) AS soGiaoDich, "
                + "COALESCE(SUM(" + transactionValue + "), 0) AS tongGiaTri "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "WHERE g.trangThai = 'HOAN_TAT' AND " + transactionDate + " >= ? "
                + "GROUP BY DATE_FORMAT(" + transactionDate + ", '%Y-%m') "
                + "ORDER BY thang";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startMonth.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String monthKey = rs.getString("thang");
                    transactionByMonth.put(monthKey, rs.getInt("soGiaoDich"));
                    valueByMonth.put(monthKey, rs.getDouble("tongGiaTri"));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        for (String monthKey : transactionByMonth.keySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            LocalDate monthDate = LocalDate.parse(monthKey + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            item.put("label", monthDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            item.put("sales", transactionByMonth.get(monthKey));
            item.put("revenue", valueByMonth.get(monthKey));
            item.put("profit", valueByMonth.get(monthKey) * getPlatformFeeRate());
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getTransactionStatusSummary() {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> statusLabels = new LinkedHashMap<>();
        statusLabels.put("MOI_TAO", "Mới tạo");
        statusLabels.put("CHO_XAC_NHAN_HAI_BEN", "Chờ hai bên xác nhận");
        statusLabels.put("CHO_XAC_NHAN_CON_LAI", "Chờ bên còn lại");
        statusLabels.put("HOAN_TAT", "Hoàn tất");
        statusLabels.put("HUY", "Đã hủy");
        statusLabels.put("TRANH_CHAP", "Tranh chấp");

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String status : statusLabels.keySet()) {
            counts.put(status, 0);
        }

        String sql = "SELECT trangThai, COUNT(*) AS soLuong FROM giaodichxe GROUP BY trangThai";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("trangThai"), rs.getInt("soLuong"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        for (Map.Entry<String, String> entry : statusLabels.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", entry.getKey());
            item.put("label", entry.getValue());
            item.put("count", counts.getOrDefault(entry.getKey(), 0));
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getTopProvinceStats(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT COALESCE(tt.tenTinh, 'Chưa rõ tỉnh') AS tenTinh, COUNT(*) AS soLuong "
                + "FROM baidang b "
                + "LEFT JOIN tinhthanh tt ON tt.maTinh = b.maTinh "
                + "WHERE b.trangThai IN ('da_duyet', 'dang_giao_dich', 'da_ban') "
                + "GROUP BY COALESCE(tt.tenTinh, 'Chưa rõ tỉnh') "
                + "ORDER BY soLuong DESC, tenTinh ASC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("label", rs.getString("tenTinh"));
                    item.put("count", rs.getInt("soLuong"));
                    result.add(item);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return result;
    }

    public List<Map<String, Object>> getDailyProfitCurrentMonth() {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> countByDay = new LinkedHashMap<>();
        Map<String, Double> valueByDay = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate current = startOfMonth;
        while (!current.isAfter(today)) {
            String dayKey = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            countByDay.put(dayKey, 0);
            valueByDay.put(dayKey, 0d);
            current = current.plusDays(1);
        }

        String transactionDate = "COALESCE(g.ngayChot, g.ngayGiaoXe, g.ngayCapNhat, g.ngayTao)";
        String transactionValue = "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)";
        String sql = "SELECT DATE_FORMAT(" + transactionDate + ", '%Y-%m-%d') AS ngay, COUNT(*) AS soGiaoDich, "
                + "COALESCE(SUM(" + transactionValue + "), 0) AS tongGiaTri "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "WHERE g.trangThai = 'HOAN_TAT' AND " + transactionDate + " >= ? "
                + "GROUP BY DATE_FORMAT(" + transactionDate + ", '%Y-%m-%d') "
                + "ORDER BY ngay";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startOfMonth.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dayKey = rs.getString("ngay");
                    countByDay.put(dayKey, rs.getInt("soGiaoDich"));
                    valueByDay.put(dayKey, rs.getDouble("tongGiaTri"));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        for (String dayKey : countByDay.keySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            LocalDate dayDate = LocalDate.parse(dayKey, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            item.put("label", dayDate.format(DateTimeFormatter.ofPattern("dd/MM")));
            item.put("revenue", valueByDay.get(dayKey));
            item.put("profit", valueByDay.get(dayKey) * getPlatformFeeRate());
            item.put("count", countByDay.get(dayKey));
            result.add(item);
        }
        return result;
    }

    public List<Map<String, Object>> getProfitByBrand(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String transactionValue = "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)";
        String sql = "SELECT COALESCE(h.tenHang, 'Khác') AS tenHang, COUNT(*) AS soLuong, "
                + "COALESCE(SUM(" + transactionValue + "), 0) AS tongGiaTri "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "LEFT JOIN hangxe h ON h.maHang = x.maHang "
                + "WHERE g.trangThai = 'HOAN_TAT' "
                + "GROUP BY COALESCE(h.tenHang, 'Khác') "
                + "ORDER BY tongGiaTri DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("label", rs.getString("tenHang"));
                    item.put("count", rs.getInt("soLuong"));
                    double revenue = rs.getDouble("tongGiaTri");
                    item.put("revenue", revenue);
                    item.put("profit", revenue * getPlatformFeeRate());
                    result.add(item);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return result;
    }

    public List<Map<String, Object>> getRecentCompletedTransactions(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT g.maGiaoDich, x.tenXe, buyer.hoTen AS tenNguoiMua, seller.hoTen AS tenNguoiBan, "
                + "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0) AS giaTriGiaoDich, "
                + "COALESCE(g.ngayChot, g.ngayGiaoXe, g.ngayCapNhat, g.ngayTao) AS ngayHoanTat "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "LEFT JOIN nguoidung buyer ON buyer.maNguoiDung = g.maNguoiMua "
                + "LEFT JOIN nguoidung seller ON seller.maNguoiDung = g.maNguoiBan "
                + "WHERE g.trangThai = 'HOAN_TAT' "
                + "ORDER BY ngayHoanTat DESC LIMIT ?";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("maGiaoDich", rs.getInt("maGiaoDich"));
                    item.put("tenXe", rs.getString("tenXe"));
                    item.put("tenNguoiMua", rs.getString("tenNguoiMua"));
                    item.put("tenNguoiBan", rs.getString("tenNguoiBan"));
                    double giaTriGiaoDich = rs.getDouble("giaTriGiaoDich");
                    item.put("giaTriGiaoDich", giaTriGiaoDich);
                    item.put("loiNhuan", giaTriGiaoDich * getPlatformFeeRate());
                    Timestamp ngayHoanTat = rs.getTimestamp("ngayHoanTat");
                    item.put("ngayHoanTat", ngayHoanTat != null ? sdf.format(ngayHoanTat) : "");
                    result.add(item);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return result;
    }

    public double getAverageProfitPerTransaction() {
        int count = countCompletedTransactions();
        if (count == 0) {
            return 0;
        }
        return estimateCompletedTransactionProfit() / count;
    }

    public double getProfitGrowthRate() {
        double currentMonthProfit = estimateCurrentMonthTransactionProfit();

        String transactionDate = "COALESCE(g.ngayChot, g.ngayGiaoXe, g.ngayCapNhat, g.ngayTao)";
        String transactionValue = "COALESCE(g.giaChot, y.giaThamKhao, x.gia, 0)";
        String sql = "SELECT COALESCE(SUM(" + transactionValue + "), 0) "
                + "FROM giaodichxe g "
                + "LEFT JOIN yeucaumuaxe y ON y.maYeuCau = g.maYeuCau "
                + "LEFT JOIN xe x ON x.maXe = g.maXe "
                + "WHERE g.trangThai = 'HOAN_TAT' "
                + "AND " + transactionDate + " >= ? AND " + transactionDate + " < ?";
        LocalDate startOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate startOfPrevMonth = startOfCurrentMonth.minusMonths(1);
        double previousMonthRevenue = 0;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startOfPrevMonth.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(startOfCurrentMonth.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    previousMonthRevenue = rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }

        double previousMonthProfit = previousMonthRevenue * getPlatformFeeRate();
        if (previousMonthProfit > 0) {
            return (currentMonthProfit - previousMonthProfit) / previousMonthProfit * 100;
        }
        return 0;
    }

    public boolean approvePost(int id) {
        String sql = "UPDATE baidang SET trangThai = 'da_duyet' WHERE maBaiDang = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DAO operation failed", e);
        }
        return false;
    }

    private int getCount(String sql) {
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
}
