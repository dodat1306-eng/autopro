package controller;

import com.google.gson.Gson;
import dao.AdminDAO;
import dao.GiaoDichXeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import model.BaiDang;
import model.NguoiDung;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        NguoiDung user = session == null ? null : (NguoiDung) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        AdminDAO dao = new AdminDAO();
        request.setAttribute("tongNguoiDung", dao.countUsers());
        request.setAttribute("tongXe", dao.countCars());
        request.setAttribute("tongBaiDangChoDuyet", dao.countPendingPosts());
        request.setAttribute("tongBaiDangDaDuyet", dao.countApprovedPosts());
        request.setAttribute("tongBaoCaoChoXuLy", dao.countPendingReports());
        request.setAttribute("tongYeuCauKetNoi", dao.countConnectionRequests());
        request.setAttribute("yeuCauDangXuLy", dao.countActiveRequests());
        request.setAttribute("tongGiaoDichXe", dao.countTransactions());
        request.setAttribute("giaoDichChoXacNhan", dao.countPendingTransactionConfirmations());
        request.setAttribute("giaoDichHoanTat", dao.countCompletedTransactions());
        request.setAttribute("giaoDichTranhChap", dao.countDisputedTransactions());
        request.setAttribute("tongGiaTriGiaoDich", dao.sumCompletedTransactionValue());
        double platformFeeRate = dao.getPlatformFeeRate();
        request.setAttribute("tyLePhiNenTang", platformFeeRate);
        request.setAttribute("tyLePhiNenTangPhanTram", platformFeeRate * 100d);
        request.setAttribute("loiNhuanUocTinh", dao.estimateCompletedTransactionProfit());
        request.setAttribute("loiNhuanThangNay", dao.estimateCurrentMonthTransactionProfit());

        request.setAttribute("loiNhuanTrungBinh", dao.getAverageProfitPerTransaction());
        request.setAttribute("tangTruongLoiNhuan", dao.getProfitGrowthRate());

        List<BaiDang> recentPosts = dao.getRecentPendingPosts();
        request.setAttribute("recentPosts", recentPosts);

        Gson gson = new Gson();
        List<Map<String, Object>> monthlyStats = dao.getMonthlyTransactionValueStats(6);
        List<Map<String, Object>> statusSummary = dao.getTransactionStatusSummary();
        List<Map<String, Object>> provinceSummary = dao.getTopProvinceStats(6);
        List<Map<String, Object>> dailyProfit = dao.getDailyProfitCurrentMonth();
        List<Map<String, Object>> profitByBrand = dao.getProfitByBrand(8);
        List<Map<String, Object>> recentCompleted = dao.getRecentCompletedTransactions(10);

        request.setAttribute("monthlyStatsJson", gson.toJson(monthlyStats));
        request.setAttribute("statusSummaryJson", gson.toJson(statusSummary));
        request.setAttribute("provinceSummaryJson", gson.toJson(provinceSummary));
        request.setAttribute("dailyProfitJson", gson.toJson(dailyProfit));
        request.setAttribute("profitByBrandJson", gson.toJson(profitByBrand));
        request.setAttribute("recentCompletedJson", gson.toJson(recentCompleted));

        request.getRequestDispatcher("/admin_dashboard.jsp").forward(request, response);
    }
}
