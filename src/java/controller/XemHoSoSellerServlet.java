package controller;

import dao.DangKySellerDAO;
import dao.NguoiDungDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.DangKySeller;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet(urlPatterns = {"/seller-register/status", "/seller-register/status/*"})
public class XemHoSoSellerServlet extends HttpServlet {

    private boolean redirectIfNonCanonicalPath(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.isBlank() && !"/".equals(pathInfo)) {
            WebAppUrlUtil.redirect(request, response, "seller-register/status");
            return true;
        }
        return false;
    }

    private NguoiDung refreshCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        NguoiDung sessionUser = (NguoiDung) session.getAttribute("user");
        if (sessionUser == null) {
            return null;
        }

        NguoiDung freshUser = new NguoiDungDAO().getUserById(sessionUser.getMaNguoiDung());
        if (freshUser == null) {
            session.removeAttribute("user");
            return null;
        }
        session.setAttribute("user", freshUser);
        return freshUser;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectIfNonCanonicalPath(request, response)) {
            return;
        }

        NguoiDung user = refreshCurrentUser(request);
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        DangKySeller latestRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
        boolean approvedSeller = dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung()) || user.isSeller();
        if (latestRequest == null && approvedSeller) {
            latestRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
        }

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "seller-register/status");

        request.setAttribute("currentUser", user);
        request.setAttribute("latestSellerRequest", latestRequest);
        request.setAttribute("sellerRequestHistory", dangKySellerDAO.getHistoryByUserId(user.getMaNguoiDung()));
        request.setAttribute("isApprovedSeller", approvedSeller);
        request.getRequestDispatcher("/seller-register-status.jsp").forward(request, response);
    }
}
