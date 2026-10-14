package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.NguoiDung;

@WebServlet("/admin/user-action")
public class AdminUserActionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?result=invalid");
            return;
        }
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?result=invalid");
            return;
        }

        NguoiDungDAO dao = new NguoiDungDAO();
        NguoiDung target = dao.getUserById(id);
        if (target == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users?result=invalid");
            return;
        }

        String result = "updated";
        boolean selfAction = id == admin.getMaNguoiDung();
        boolean targetIsAdmin = target.isAdmin();

        switch (action) {
            case "delete":
                if (selfAction) {
                    result = "self-delete-denied";
                    break;
                }
                if (targetIsAdmin) {
                    result = "admin-protected";
                    break;
                }
                result = dao.deleteUser(id) ? "deleted" : "delete-error";
                break;
            case "block":
                if (selfAction || targetIsAdmin) {
                    result = "admin-protected";
                    break;
                }
                result = dao.updateTrangThai(id, "blocked") ? "updated" : "error";
                break;
            case "active":
                if (targetIsAdmin && !selfAction) {
                    result = "admin-protected";
                    break;
                }
                result = dao.updateTrangThai(id, "active") ? "updated" : "error";
                break;
            case "admin":
                result = dao.updateVaiTro(id, "admin") ? "updated" : "error";
                break;
            case "buyer":
                if (selfAction || targetIsAdmin) {
                    result = "admin-protected";
                    break;
                }
                result = dao.updateVaiTro(id, "buyer") ? "updated" : "error";
                break;
            case "seller":
                response.sendRedirect(request.getContextPath() + "/admin/seller-requests");
                return;
            case "user":
                if (selfAction || targetIsAdmin) {
                    result = "admin-protected";
                    break;
                }
                result = dao.updateVaiTro(id, "buyer") ? "updated" : "error";
                break;
            default:
                result = "invalid";
                break;
        }

        response.sendRedirect(request.getContextPath() + "/admin/users?result=" + result);
    }
}
