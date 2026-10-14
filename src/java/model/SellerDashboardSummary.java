package model;

public class SellerDashboardSummary {

    private int totalPosts;
    private int activePosts;
    private int tradingPosts;
    private int pendingPosts;
    private int warningPosts;
    private int rejectedPosts;
    private int soldPosts;
    private int totalViews;
    private int totalMessengers;
    private int totalRequests;
    private int totalAppointments;
    private int totalTransactions;
    private int completedTransactions;

    public void addItem(SellerDashboardItem item) {
        totalPosts++;
        String status = item.getTrangThai();
        if ("da_duyet".equals(status)) {
            activePosts++;
        } else if ("dang_giao_dich".equals(status)) {
            tradingPosts++;
        } else if ("cho_duyet".equals(status)) {
            pendingPosts++;
        } else if ("canh_bao".equals(status)) {
            warningPosts++;
        } else if ("tu_choi".equals(status)) {
            rejectedPosts++;
        } else if ("da_ban".equals(status)) {
            soldPosts++;
        }

        totalViews += item.getLuotXem();
        totalMessengers += item.getSoNguoiNhanTin();
        totalRequests += item.getSoYeuCau();
        totalAppointments += item.getSoLichHen();
        totalTransactions += item.getSoGiaoDich();
        completedTransactions += item.getSoGiaoDichHoanTat();
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public int getActivePosts() {
        return activePosts;
    }

    public int getTradingPosts() {
        return tradingPosts;
    }

    public int getPendingPosts() {
        return pendingPosts;
    }

    public int getWarningPosts() {
        return warningPosts;
    }

    public int getRejectedPosts() {
        return rejectedPosts;
    }

    public int getSoldPosts() {
        return soldPosts;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public int getTotalMessengers() {
        return totalMessengers;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public int getCompletedTransactions() {
        return completedTransactions;
    }
}
