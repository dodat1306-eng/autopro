package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.NguoiDung;
import util.PaginationUtil;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");

        if (user == null || !"admin".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        NguoiDungDAO dao = new NguoiDungDAO();
        List<NguoiDung> allUsers = dao.getAllUsers();
        int pageSize = 10;
        int currentPage = PaginationUtil.parsePage(request.getParameter("page"));
        int totalPages = PaginationUtil.totalPages(allUsers.size(), pageSize);
        currentPage = Math.min(currentPage, totalPages);
        request.setAttribute("users", PaginationUtil.slice(allUsers, currentPage, pageSize));
        request.setAttribute("totalItems", allUsers.size());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("paginationBaseUrl", PaginationUtil.buildPageUrl(request, "/admin/users"));

        request.getRequestDispatcher("/admin_users.jsp")
               .forward(request, response);
    }
}
