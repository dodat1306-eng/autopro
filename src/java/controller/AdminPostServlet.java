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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import model.BaiDang;
import model.NguoiDung;
import util.EmailDeliveryService;

@WebServlet(name = "AdminPostServlet", urlPatterns = {"/admin/posts"})
public class AdminPostServlet extends HttpServlet {

    private boolean canApprove(BaiDang baiDang) {
        return baiDang != null
                && ("cho_duyet".equals(baiDang.getTrangThai()) || "canh_bao".equals(baiDang.getTrangThai()));
    }

    private boolean canReject(BaiDang baiDang) {
        return baiDang != null
                && ("cho_duyet".equals(baiDang.getTrangThai()) || "canh_bao".equals(baiDang.getTrangThai()));
    }

    private boolean canWarn(BaiDang baiDang) {
        return baiDang != null && "cho_duyet".equals(baiDang.getTrangThai());
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        NguoiDung user = session == null ? null : (NguoiDung) session.getAttribute("user");
        return user != null && user.isAdmin();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String buildAdminListRedirect(HttpServletRequest request, String result, String sellerKeyword) {
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath())
                .append("/admin/posts?result=")
                .append(result);
        String normalizedKeyword = trimToEmpty(sellerKeyword);
        if (!normalizedKeyword.isEmpty()) {
            redirectUrl.append("&sellerKeyword=")
                    .append(URLEncoder.encode(normalizedKeyword, StandardCharsets.UTF_8));
        }
        return redirectUrl.toString();
    }

    private void sendModerationEmail(BaiDang baiDang, String subject, String actionLabel, String ghiChuAdmin) {
        if (baiDang == null || baiDang.getEmailNguoiDang() == null || baiDang.getEmailNguoiDang().isBlank()) {
            return;
        }

        String safeTitle = (baiDang.getTieuDe() == null || baiDang.getTieuDe().isBlank()
                ? "bài đăng #" + baiDang.getMaBaiDang()
                : baiDang.getTieuDe())
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        String safeNote = ghiChuAdmin == null ? "" : ghiChuAdmin
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        String noteBlock = safeNote.isBlank() ? "" : "<p><strong>Ghi chú từ admin:</strong><br>" + safeNote + "</p>";
        String htmlBody = "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#111827\">"
                + "<h2 style=\"margin-bottom:12px\">AutoPro</h2>"
                + "<p>Bài đăng <strong>" + safeTitle + "</strong> của bạn vừa được admin xử lý với trạng thái: <strong>" + actionLabel + "</strong>.</p>"
                + noteBlock
                + "<p>Bạn có thể đăng nhập AutoPro để xem chi tiết trong mục Tin của tôi.</p>"
                + "</div>";

        new EmailDeliveryService().sendHtmlEmail(baiDang.getEmailNguoiDang(), subject, htmlBody);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String sellerKeyword = trimToEmpty(request.getParameter("sellerKeyword"));
        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        BaiDangDAO baiDangDAO = new BaiDangDAO();
        List<BaiDang> pendingPosts = baiDangDAO.getPendingBaiDangForAdmin(sellerKeyword);
        List<BaiDang> otherPosts = baiDangDAO.getNonPendingBaiDangForAdmin(sellerKeyword);

        request.setAttribute("pendingPosts", pendingPosts);
        request.setAttribute("otherPosts", otherPosts);
        request.setAttribute("sellerKeyword", sellerKeyword);
        request.setAttribute("pendingPostCount", pendingPosts.size());
        request.setAttribute("otherPostCount", otherPosts.size());
        request.setAttribute("matchedPostCount", pendingPosts.size() + otherPosts.size());
        request.getRequestDispatcher("/admin-posts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");
        String idRaw = request.getParameter("id");
        String action = trimToEmpty(request.getParameter("action"));
        String ghiChuAdmin = trimToEmpty(request.getParameter("ghiChuAdmin"));
        String sellerKeyword = trimToEmpty(request.getParameter("sellerKeyword"));
        boolean returnToDetail = "1".equals(request.getParameter("returnToDetail"));

        int maBaiDang;
        try {
            maBaiDang = Integer.parseInt(idRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(buildAdminListRedirect(request, "invalid", sellerKeyword));
            return;
        }

        BaiDangDAO baiDangDAO = new BaiDangDAO();
        BaoCaoDAO baoCaoDAO = new BaoCaoDAO();
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        BaiDang baiDang = baiDangDAO.getBaiDangByIdForAdmin(maBaiDang);
        boolean success = false;
        String result = "unknown";

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        if (baiDang != null && (baiDang.isDaBan()
                || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe()))) {
            result = "invalid_state";
            if (returnToDetail) {
                response.sendRedirect(request.getContextPath() + "/admin/post-detail?id=" + maBaiDang + "&result=" + result);
                return;
            }
            response.sendRedirect(buildAdminListRedirect(request, result, sellerKeyword));
            return;
        }

        switch (action) {
            case "approve":
                if (!canApprove(baiDang)) {
                    result = "invalid_state";
                    break;
                }
                success = baiDangDAO.updateTrangThaiVaGhiChu(maBaiDang, "da_duyet", null);
                if (success) {
                    baoCaoDAO.resolvePendingReportsForPost(maBaiDang, "BO_QUA", admin.getMaNguoiDung(), "Admin đã rà soát và tiếp tục duyệt bài đăng.");
                    if (baiDang != null) {
                        thongBaoDAO.createNotification(
                                baiDang.getMaNguoiDung(),
                                "POST_MODERATION",
                                "Bài đăng đã được duyệt",
                                "Bài \"" + baiDang.getTieuDe() + "\" đã được admin duyệt và hiển thị công khai.",
                                "my-posts"
                        );
                        sendModerationEmail(baiDang, "Bài đăng đã được duyệt trên AutoPro", "Đã duyệt", null);
                    }
                    result = "approved";
                } else {
                    result = "error";
                }
                break;
            case "reject":
                if (!canReject(baiDang)) {
                    result = "invalid_state";
                    break;
                }
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Bài đăng chưa đáp ứng yêu cầu duyệt. Vui lòng rà soát và bổ sung lại nội dung.";
                }
                success = baiDangDAO.updateTrangThaiVaGhiChu(maBaiDang, "tu_choi", ghiChuAdmin);
                if (success) {
                    if (baiDang != null) {
                        thongBaoDAO.createNotification(
                                baiDang.getMaNguoiDung(),
                                "POST_MODERATION",
                                "Bài đăng bị từ chối",
                                "Admin đã từ chối bài \"" + baiDang.getTieuDe() + "\". Hãy kiểm tra ghi chú để chỉnh sửa.",
                                "my-posts"
                        );
                        sendModerationEmail(baiDang, "Bài đăng bị từ chối trên AutoPro", "Từ chối", ghiChuAdmin);
                    }
                    result = "rejected";
                } else {
                    result = "error";
                }
                break;
            case "warn":
                if (!canWarn(baiDang)) {
                    result = "invalid_state";
                    break;
                }
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Admin yêu cầu người bán cập nhật lại bài đăng trước khi tiếp tục hiển thị.";
                }
                success = baiDangDAO.updateTrangThaiVaGhiChu(maBaiDang, "canh_bao", ghiChuAdmin);
                if (success) {
                    baoCaoDAO.resolvePendingReportsForPost(maBaiDang, "DA_CANH_BAO", admin.getMaNguoiDung(), ghiChuAdmin);
                    if (baiDang != null) {
                        thongBaoDAO.createNotification(
                                baiDang.getMaNguoiDung(),
                                "POST_MODERATION",
                                "Bài đăng bị cảnh báo",
                                "Admin đã cảnh báo bài \"" + baiDang.getTieuDe() + "\". Vui lòng xem ghi chú và chỉnh sửa lại nội dung.",
                                "my-posts"
                        );
                        sendModerationEmail(baiDang, "Bài đăng bị cảnh báo trên AutoPro", "Cảnh báo", ghiChuAdmin);
                    }
                    result = "warned";
                } else {
                    result = "error";
                }
                break;
            case "delete":
                if (ghiChuAdmin.isEmpty()) {
                    ghiChuAdmin = "Bài đăng đã bị xóa bởi admin do vi phạm tiêu chuẩn cộng đồng hoặc nội quy hiển thị.";
                }
                success = baiDangDAO.deleteBaiDangByAdmin(maBaiDang);
                if (success) {
                    baoCaoDAO.resolvePendingReportsForPost(maBaiDang, "DA_XOA_BAI", admin.getMaNguoiDung(), ghiChuAdmin);
                    if (baiDang != null) {
                        thongBaoDAO.createNotification(
                                baiDang.getMaNguoiDung(),
                                "POST_MODERATION",
                                "Bài đăng đã bị xóa",
                                "Admin đã xóa bài \"" + baiDang.getTieuDe() + "\" khỏi hệ thống.",
                                "my-posts"
                        );
                        sendModerationEmail(baiDang, "Bài đăng đã bị xóa khỏi AutoPro", "Xóa bài đăng", ghiChuAdmin);
                    }
                    result = "deleted";
                    returnToDetail = false;
                } else {
                    result = "error";
                }
                break;
            default:
                response.sendRedirect(buildAdminListRedirect(request, "invalid", sellerKeyword));
                return;
        }

        if (returnToDetail && !"delete".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/post-detail?id=" + maBaiDang + "&result=" + result);
            return;
        }

        response.sendRedirect(buildAdminListRedirect(request, result, sellerKeyword));
    }
}
