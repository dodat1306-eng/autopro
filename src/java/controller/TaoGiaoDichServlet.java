package controller;

import dao.GiaoDichXeDAO;
import dao.YeuCauKetNoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import model.GiaoDichXe;
import model.NguoiDung;
import model.YeuCauKetNoi;

@WebServlet("/giao-dich/tao")
public class TaoGiaoDichServlet extends HttpServlet {

    private static final Set<String> CREATABLE_REQUEST_STATUSES = Set.of("DA_XEM_XE", "DA_CHOT");

    private void flash(HttpServletRequest request, String key, String message) {
        request.getSession().setAttribute(key, message);
    }

    private void redirectAppointments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/lich-hen-cua-toi");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!user.isThanhVien()) {
            flash(request, "requestError", "Chỉ tài khoản thành viên mới có thể tạo hồ sơ giao dịch.");
            redirectAppointments(request, response);
            return;
        }

        int maYeuCau;
        try {
            maYeuCau = Integer.parseInt(request.getParameter("maYeuCau"));
        } catch (NumberFormatException ex) {
            flash(request, "requestError", "Thiếu mã yêu cầu kết nối để tạo giao dịch.");
            redirectAppointments(request, response);
            return;
        }

        YeuCauKetNoiDAO yeuCauKetNoiDAO = new YeuCauKetNoiDAO();
        YeuCauKetNoi yeuCauKetNoi = yeuCauKetNoiDAO.getYeuCauKetNoiById(maYeuCau);
        if (yeuCauKetNoi == null) {
            flash(request, "requestError", "Yêu cầu kết nối không còn tồn tại.");
            redirectAppointments(request, response);
            return;
        }

        boolean isBuyer = yeuCauKetNoi.getMaNguoiMua() == user.getMaNguoiDung();
        boolean isSeller = yeuCauKetNoi.getMaNguoiBan() != null && yeuCauKetNoi.getMaNguoiBan() == user.getMaNguoiDung();
        if (!isBuyer && !isSeller) {
            flash(request, "requestError", "Bạn không có quyền tạo giao dịch cho yêu cầu này.");
            redirectAppointments(request, response);
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        GiaoDichXe existing = giaoDichXeDAO.getByYeuCau(maYeuCau);
        if (existing != null) {
            flash(request, "transactionSuccess", "Hồ sơ giao dịch đã tồn tại trước đó. Bạn có thể tiếp tục xác nhận tại trang chi tiết.");
            response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + existing.getMaGiaoDich());
            return;
        }

        if (!CREATABLE_REQUEST_STATUSES.contains(yeuCauKetNoi.getTrangThai())) {
            flash(request, "requestError", "Chỉ có thể tạo hồ sơ giao dịch sau khi hai bên đã xác nhận lịch hẹn xem xe.");
            redirectAppointments(request, response);
            return;
        }

        GiaoDichXe created = giaoDichXeDAO.createFromYeuCau(maYeuCau);
        if (created == null) {
            flash(request, "requestError", "Không thể tạo hồ sơ giao dịch lúc này. Xe có thể đã được bán hoặc dữ liệu yêu cầu không còn hợp lệ.");
            redirectAppointments(request, response);
            return;
        }

        flash(request, "transactionSuccess", "Đã tạo hồ sơ giao dịch #" + created.getMaGiaoDich() + ". Người bán cần tiếp nhận tại trang chi tiết trong 48 giờ.");
        response.sendRedirect(request.getContextPath() + "/giao-dich/chi-tiet?id=" + created.getMaGiaoDich());
    }
}
