package controller;

import dao.BaiDangDAO;
import dao.BaoCaoDAO;
import dao.GiaoDichXeDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.BaiDang;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/report-post")
public class ReportPostServlet extends HttpServlet {

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void putFlash(HttpServletRequest request, String key, String value) {
        request.getSession().setAttribute(key, value);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }

        if (!user.isThanhVien() || "blocked".equalsIgnoreCase(user.getTrangThai())) {
            putFlash(request, "detailError", "Chỉ tài khoản thành viên đang hoạt động mới có thể gửi báo cáo bài đăng.");
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        int maBaiDang;
        int maXe;
        try {
            maBaiDang = Integer.parseInt(request.getParameter("maBaiDang"));
            maXe = Integer.parseInt(request.getParameter("maXe"));
        } catch (NumberFormatException e) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        BaiDang baiDang = new BaiDangDAO().getApprovedBaiDangById(maBaiDang);
        if (baiDang == null || baiDang.getMaXe() != maXe) {
            putFlash(request, "detailError", "Bài đăng bạn muốn báo cáo không còn khả dụng.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        if (baiDang.getMaNguoiDung() == user.getMaNguoiDung()) {
            putFlash(request, "detailError", "Bạn không thể tự báo cáo bài đăng của chính mình.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        if (baiDang.isBiKhoaGiaoDich()
                || giaoDichXeDAO.hasCompletedTransactionForCar(maXe)
                || giaoDichXeDAO.hasActiveTransactionForCar(maXe)) {
            putFlash(request, "detailError", "Bài đăng này đang được giao dịch hoặc đã bán nên thao tác báo cáo từ trang mua xe đã được khóa.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        String lyDo = trimToEmpty(request.getParameter("lyDo"));
        String chiTiet = trimToEmpty(request.getParameter("chiTiet"));
        if (lyDo.isEmpty()) {
            putFlash(request, "detailError", "Vui lòng chọn lý do báo cáo bài đăng.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        BaoCaoDAO baoCaoDAO = new BaoCaoDAO();
        if (baoCaoDAO.hasPendingReportByUser(maBaiDang, user.getMaNguoiDung())) {
            putFlash(request, "detailError", "Bạn đã có một báo cáo đang chờ xử lý cho bài đăng này.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        if (!baoCaoDAO.createReport(maBaiDang, user.getMaNguoiDung(), lyDo, chiTiet)) {
            putFlash(request, "detailError", "Không thể gửi báo cáo lúc này. Vui lòng thử lại.");
            WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
            return;
        }

        new ThongBaoDAO().createNotificationForAdmins(
                "REPORT",
                "Có báo cáo bài đăng mới",
                "Bài \"" + baiDang.getTieuDe() + "\" vừa bị người dùng báo cáo với lý do: " + lyDo,
                "admin/reports"
        );

        putFlash(request, "detailSuccess", "Đã gửi báo cáo tới admin. Cảm ơn bạn đã giúp giữ AutoPro an toàn hơn.");
        WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
    }
}
