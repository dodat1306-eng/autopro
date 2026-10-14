package controller;

import dao.GiaoDichXeDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.GiaoDichXe;
import model.NguoiDung;

@WebServlet({"/giao-dich/chi-tiet", "/giao-dich/xac-nhan"})
public class XacNhanGiaoDichServlet extends HttpServlet {

    private void flash(HttpServletRequest request, String key, String message) {
        request.getSession().setAttribute(key, message);
    }

    private void moveFlash(HttpServletRequest request, String sessionKey, String requestKey) {
        Object message = request.getSession().getAttribute(sessionKey);
        if (message != null) {
            request.setAttribute(requestKey, message);
            request.getSession().removeAttribute(sessionKey);
        }
    }

    private String sanitizeNote(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim().replace("<", "").replace(">", "");
        return sanitized.isEmpty() ? null : sanitized;
    }

    private Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void redirectAppointments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/lich-hen-cua-toi");
    }

    private void redirectDetail(HttpServletRequest request, HttpServletResponse response, int maGiaoDich) throws IOException {
        response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer maGiaoDich = parseId(request.getParameter("id"));
        if (maGiaoDich == null) {
            flash(request, "requestError", "Không thể xác định giao dịch cần xem.");
            redirectAppointments(request, response);
            return;
        }

        GiaoDichXe giaoDich = new GiaoDichXeDAO().getById(maGiaoDich);
        if (giaoDich == null) {
            flash(request, "requestError", "Giao dịch không tồn tại hoặc đã bị xóa.");
            redirectAppointments(request, response);
            return;
        }

        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/giao-dich?id=" + maGiaoDich);
            return;
        }

        boolean isBuyer = giaoDich.getMaNguoiMua() == user.getMaNguoiDung();
        boolean isSeller = giaoDich.getMaNguoiBan() == user.getMaNguoiDung();
        if (!isBuyer && !isSeller) {
            flash(request, "requestError", "Bạn không có quyền xem hồ sơ giao dịch này.");
            redirectAppointments(request, response);
            return;
        }

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "giao-dich/chi-tiet?id=" + maGiaoDich);
        moveFlash(request, "transactionSuccess", "transactionSuccess");
        moveFlash(request, "transactionError", "transactionError");

        request.setAttribute("giaoDich", giaoDich);
        try {
            request.setAttribute("transactionHistory", new dao.GiaoDichWorkflowDAO().history(giaoDich.getMaGiaoDich()));
        } catch (Exception e) { log("Cannot load transaction history", e); }
        request.getRequestDispatcher(isBuyer ? "/buyer-transaction-detail.jsp" : "/seller-transaction-detail.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/giao-dich");
            return;
        }

        Integer maGiaoDich = parseId(request.getParameter("maGiaoDich"));
        if (maGiaoDich == null) {
            flash(request, "transactionError", "Thiếu mã giao dịch cần xác nhận.");
            redirectAppointments(request, response);
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        GiaoDichXe giaoDich = giaoDichXeDAO.getById(maGiaoDich);
        if (giaoDich == null) {
            flash(request, "transactionError", "Giao dịch không tồn tại.");
            redirectAppointments(request, response);
            return;
        }

        String action = request.getParameter("action");
        String ghiChu = sanitizeNote(request.getParameter("ghiChu"));

        if ("buyer-confirm".equals(action)) {
            if (giaoDich.getMaNguoiMua() != user.getMaNguoiDung()) {
                flash(request, "transactionError", "Chỉ người mua của giao dịch mới được xác nhận đã thanh toán và nhận xe.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
            if (giaoDich.isXacNhanNguoiMua()) {
                flash(request, "transactionError", "Bạn đã xác nhận thanh toán và nhận xe trước đó.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
            if (!giaoDichXeDAO.buyerConfirmReceived(maGiaoDich, user.getMaNguoiDung(), ghiChu)) {
                flash(request, "transactionError", "Không thể ghi nhận xác nhận thanh toán và nhận xe lúc này.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
        } else if ("seller-confirm".equals(action)) {
            if (giaoDich.getMaNguoiBan() != user.getMaNguoiDung()) {
                flash(request, "transactionError", "Chỉ người bán của giao dịch mới được xác nhận đã giao xe.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
            if (giaoDich.isXacNhanNguoiBan()) {
                flash(request, "transactionError", "Bạn đã xác nhận giao xe trước đó.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
            if (!giaoDichXeDAO.sellerConfirmDelivered(maGiaoDich, user.getMaNguoiDung(), ghiChu)) {
                flash(request, "transactionError", "Không thể ghi nhận xác nhận giao xe lúc này.");
                redirectDetail(request, response, maGiaoDich);
                return;
            }
        } else {
            flash(request, "transactionError", "Thao tác xác nhận giao dịch không hợp lệ.");
            redirectDetail(request, response, maGiaoDich);
            return;
        }

        GiaoDichXe updated = giaoDichXeDAO.getById(maGiaoDich);
        if (updated != null && GiaoDichXeDAO.HOAN_TAT.equals(updated.getTrangThai())) {
            flash(request, "transactionSuccess", "Giao dịch đã được cả hai bên xác nhận và chuyển sang trạng thái hoàn tất.");
        } else if ("buyer-confirm".equals(action)) {
            flash(request, "transactionSuccess", "Đã ghi nhận xác nhận thanh toán và nhận xe của bạn. Hệ thống đang chờ người bán xác nhận còn lại.");
        } else {
            flash(request, "transactionSuccess", "Đã ghi nhận xác nhận giao xe của bạn. Hệ thống đang chờ người mua xác nhận còn lại.");
        }

        redirectDetail(request, response, maGiaoDich);
    }
}
