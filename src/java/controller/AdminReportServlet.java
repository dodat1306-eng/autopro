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
import java.util.List;
import model.BaiDang;
import model.BaoCao;
import model.NguoiDung;
import util.PaginationUtil;

@WebServlet("/admin/reports")
public class AdminReportServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        return user != null && "admin".equalsIgnoreCase(user.getVaiTro());
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        BaoCaoDAO baoCaoDAO = new BaoCaoDAO();
        List<BaoCao> allReports = baoCaoDAO.getAllReportsForAdmin();
        int pageSize = 5;
        int currentPage = PaginationUtil.parsePage(request.getParameter("page"));
        int totalPages = PaginationUtil.totalPages(allReports.size(), pageSize);
        currentPage = Math.min(currentPage, totalPages);
        request.setAttribute("listBaoCao", PaginationUtil.slice(allReports, currentPage, pageSize));
        request.setAttribute("totalItems", allReports.size());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("paginationBaseUrl", PaginationUtil.buildPageUrl(request, "/admin/reports"));
        request.setAttribute("pendingReportCount", baoCaoDAO.countPendingReports());
        request.getRequestDispatcher("/admin-reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");
        Integer maBaoCao;
        try {
            maBaoCao = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/reports?result=invalid");
            return;
        }

        String action = trimToEmpty(request.getParameter("action"));
        String ghiChuAdmin = trimToEmpty(request.getParameter("ghiChuAdmin"));

        BaoCaoDAO baoCaoDAO = new BaoCaoDAO();
        BaoCao baoCao = baoCaoDAO.getReportById(maBaoCao);
        if (baoCao == null) {
            response.sendRedirect(request.getContextPath() + "/admin/reports?result=missing");
            return;
        }

        BaiDangDAO baiDangDAO = new BaiDangDAO();
        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        giaoDichXeDAO.syncCompletedCarsToSoldPostsOnce();
        BaiDang baiDang = baiDangDAO.getBaiDangByIdForAdmin(baoCao.getMaBaiDang());
        boolean lockedPost = baiDang != null
                && (baiDang.isDaBan()
                || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe()));
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        boolean success = false;
        String result = "error";

        switch (action) {
            case "warn":
                if (lockedPost) {
                    response.sendRedirect(request.getContextPath() + "/admin/reports?result=sold_locked");
                    return;
                }
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Bài đăng có dấu hiệu vi phạm tiêu chuẩn cộng đồng. Vui lòng rà soát và chỉnh sửa trước khi hiển thị lại.";
                }
                success = baiDangDAO.updateTrangThaiVaGhiChu(baoCao.getMaBaiDang(), "canh_bao", ghiChuAdmin);
                if (success) {
                    baoCaoDAO.resolvePendingReportsForPost(baoCao.getMaBaiDang(), "DA_CANH_BAO", admin.getMaNguoiDung(), ghiChuAdmin);
                    if (baoCao.getMaNguoiDang() != null) {
                        thongBaoDAO.createNotification(
                                baoCao.getMaNguoiDang(),
                                "POST_MODERATION",
                                "Bài đăng bị cảnh báo",
                                "Admin đã cảnh báo bài \"" + baoCao.getTieuDeBaiDang() + "\". Vui lòng kiểm tra ghi chú và cập nhật lại nội dung.",
                                "my-posts"
                        );
                    }
                    result = "warned";
                }
                break;
            case "delete":
                if (lockedPost) {
                    response.sendRedirect(request.getContextPath() + "/admin/reports?result=sold_locked");
                    return;
                }
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Bài đăng đã bị gỡ do vi phạm tiêu chuẩn cộng đồng.";
                }
                success = baiDangDAO.deleteBaiDangByAdmin(baoCao.getMaBaiDang());
                if (success) {
                    baoCaoDAO.resolvePendingReportsForPost(baoCao.getMaBaiDang(), "DA_XOA_BAI", admin.getMaNguoiDung(), ghiChuAdmin);
                    if (baoCao.getMaNguoiDang() != null) {
                        thongBaoDAO.createNotification(
                                baoCao.getMaNguoiDang(),
                                "POST_MODERATION",
                                "Bài đăng đã bị xóa",
                                "Admin đã xóa bài \"" + baoCao.getTieuDeBaiDang() + "\" do vi phạm tiêu chuẩn cộng đồng.",
                                "my-posts"
                        );
                    }
                    result = "deleted";
                }
                break;
            case "dismiss":
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Admin đã rà soát và chưa ghi nhận vi phạm ở thời điểm hiện tại.";
                }
                success = baoCaoDAO.resolveReport(maBaoCao, "BO_QUA", admin.getMaNguoiDung(), ghiChuAdmin);
                if (success) {
                    result = "dismissed";
                }
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/reports?result=invalid");
                return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/reports?result=" + result);
    }
}
