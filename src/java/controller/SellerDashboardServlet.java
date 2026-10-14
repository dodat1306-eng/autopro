package controller;

import dao.DangKySellerDAO;
import dao.GiaoDichXeDAO;
import dao.NguoiDungDAO;
import dao.SellerDashboardDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.NguoiDung;
import model.SellerDashboardItem;
import model.SellerDashboardSummary;
import util.WebAppUrlUtil;

@WebServlet(name = "SellerDashboardServlet", urlPatterns = {"/seller/dashboard", "/seller-dashboard"})
public class SellerDashboardServlet extends HttpServlet {

    private NguoiDung refreshCurrentUser(HttpServletRequest request) {
        NguoiDung sessionUser = (NguoiDung) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return null;
        }

        NguoiDung freshUser = new NguoiDungDAO().getUserById(sessionUser.getMaNguoiDung());
        if (freshUser == null) {
            request.getSession().removeAttribute("user");
            return null;
        }

        request.getSession().setAttribute("user", freshUser);
        return freshUser;
    }

    private boolean ensureSellerAccess(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws IOException {
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return false;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return false;
        }
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        if (user.isSeller()) {
            dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
            return true;
        }

        if (dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung())
                && dangKySellerDAO.updateUserToSellerIfApproved(user.getMaNguoiDung())) {
            NguoiDung refreshedUser = refreshCurrentUser(request);
            if (refreshedUser != null && refreshedUser.isSeller()) {
                return true;
            }
        }

        WebAppUrlUtil.redirect(request, response, "seller-register?needSeller=1");
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = refreshCurrentUser(request);
        if (!ensureSellerAccess(request, response, user)) {
            return;
        }

        SellerDashboardDAO dashboardDAO = new SellerDashboardDAO();
        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        List<SellerDashboardItem> items = dashboardDAO.getItemsBySeller(user.getMaNguoiDung());
        SellerDashboardSummary summary = dashboardDAO.buildSummary(items);

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "seller/dashboard");

        request.setAttribute("sellerDashboardItems", items);
        request.setAttribute("sellerDashboardSummary", summary);
        request.getRequestDispatcher("/seller-dashboard.jsp").forward(request, response);
    }
}
