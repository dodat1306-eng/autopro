package controller;

import dao.GiaoDichXeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.GiaoDichXe;
import model.NguoiDung;

@WebServlet("/giao-dich/huy")
public class HuyGiaoDichServlet extends HttpServlet {

    private void flash(HttpServletRequest request, String key, String message) {
        request.getSession().setAttribute(key, message);
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
            flash(request, "requestError", "Không xác định được giao dịch cần hủy.");
            response.sendRedirect(request.getContextPath() + "/lich-hen-cua-toi");
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        GiaoDichXe giaoDich = giaoDichXeDAO.getById(maGiaoDich);
        if (giaoDich == null) {
            flash(request, "transactionError", "Giao dịch không tồn tại.");
            response.sendRedirect(request.getContextPath() + "/lich-hen-cua-toi");
            return;
        }

        boolean isBuyer = giaoDich.getMaNguoiMua() == user.getMaNguoiDung();
        boolean isSeller = giaoDich.getMaNguoiBan() == user.getMaNguoiDung();
        if (!isBuyer && !isSeller) {
            flash(request, "transactionError", "Bạn không có quyền hủy giao dịch này.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
            return;
        }

        if (GiaoDichXeDAO.HOAN_TAT.equals(giaoDich.getTrangThai())) {
            flash(request, "transactionError", "Giao dịch đã hoàn tất nên không thể hủy bằng thao tác người dùng.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
            return;
        }

        if (GiaoDichXeDAO.HUY.equals(giaoDich.getTrangThai())) {
            flash(request, "transactionError", "Giao dịch đã ở trạng thái hủy.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
            return;
        }

        if (GiaoDichXeDAO.TRANH_CHAP.equals(giaoDich.getTrangThai())) {
            flash(request, "transactionError", "Giao dịch đang ở trạng thái tranh chấp. Vui lòng chờ admin xử lý.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
            return;
        }

        String lyDo = sanitizeNote(request.getParameter("lyDoHuy"));
        if (!giaoDichXeDAO.cancelTransaction(maGiaoDich, user.getMaNguoiDung(), lyDo)) {
            flash(request, "transactionError", "Không thể hủy giao dịch lúc này.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
            return;
        }

        flash(request, "transactionSuccess", "Đã hủy giao dịch thành công.");
        response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + maGiaoDich);
    }
}
