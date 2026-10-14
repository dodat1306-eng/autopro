package controller;

import dao.GiaoDichXeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.GiaoDichXe;
import model.NguoiDung;

@WebServlet("/admin/giao-dich")
public class AdminQuanLyGiaoDichServlet extends HttpServlet {

    private void flash(HttpServletRequest request, String key, String message) {
        request.getSession().setAttribute(key, message);
    }

    private void moveFlash(HttpServletRequest request, String sessionKey, String requestKey) {
        Object value = request.getSession().getAttribute(sessionKey);
        if (value != null) {
            request.setAttribute(requestKey, value);
            request.getSession().removeAttribute(sessionKey);
        }
    }

    private Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String sanitizeNote(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim().replace("<", "").replace(">", "");
        return sanitized.isEmpty() ? null : sanitized;
    }

    private boolean ensureAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        NguoiDung user = session == null ? null : (NguoiDung) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAdmin(request, response)) {
            return;
        }

        moveFlash(request, "adminTransactionSuccess", "adminTransactionSuccess");
        moveFlash(request, "adminTransactionError", "adminTransactionError");

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        Integer maGiaoDich = parseId(request.getParameter("id"));
        if (maGiaoDich != null) {
            GiaoDichXe giaoDich = giaoDichXeDAO.getById(maGiaoDich);
            if (giaoDich == null) {
                flash(request, "adminTransactionError", "Không tìm thấy giao dịch cần xem.");
                response.sendRedirect(request.getContextPath() + "/admin/giao-dich");
                return;
            }

            request.setAttribute("giaoDich", giaoDich);
        try {
            request.setAttribute("transactionHistory", new dao.GiaoDichWorkflowDAO().history(giaoDich.getMaGiaoDich()));
        } catch (Exception e) { log("Cannot load transaction history", e); }
            request.getRequestDispatcher("/admin-transaction-detail.jsp").forward(request, response);
            return;
        }

        String status = request.getParameter("status");
        if (status != null && status.isBlank()) {
            status = null;
        }
        request.setAttribute("selectedStatus", status);
        request.setAttribute("danhSachGiaoDich", giaoDichXeDAO.getAll(status));
        request.getRequestDispatcher("/admin-transaction-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAdmin(request, response)) {
            return;
        }

        Integer maGiaoDich = parseId(request.getParameter("maGiaoDich"));
        String trangThai = request.getParameter("trangThai");
        String ghiChuAdmin = sanitizeNote(request.getParameter("ghiChuAdmin"));
        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");

        if (maGiaoDich == null || trangThai == null || trangThai.isBlank()) {
            flash(request, "adminTransactionError", "Thiếu dữ liệu cập nhật giao dịch.");
            response.sendRedirect(request.getContextPath() + "/admin/giao-dich");
            return;
        }

        boolean success = new GiaoDichXeDAO().adminUpdateStatus(maGiaoDich, trangThai.trim(), admin.getMaNguoiDung(), ghiChuAdmin);
        if (!success) {
            flash(request, "adminTransactionError", "Không thể cập nhật trạng thái giao dịch lúc này.");
            response.sendRedirect(request.getContextPath() + "/admin/giao-dich?id=" + maGiaoDich);
            return;
        }

        flash(request, "adminTransactionSuccess", "Đã cập nhật giao dịch #" + maGiaoDich + " thành công.");
        response.sendRedirect(request.getContextPath() + "/admin/giao-dich?id=" + maGiaoDich);
    }
}
