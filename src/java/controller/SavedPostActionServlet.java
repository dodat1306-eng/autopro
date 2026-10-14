package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import dao.SavedPostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.BaiDang;
import model.NguoiDung;

@WebServlet("/saved-posts/action")
public class SavedPostActionServlet extends HttpServlet {

    private void setFlash(HttpServletRequest request, String source, String message, boolean success) {
        HttpSession session = request.getSession();
        String normalizedSource = source == null ? "" : source.trim().toLowerCase();
        if ("saved-posts".equals(normalizedSource)) {
            session.setAttribute(success ? "savedPostSuccess" : "savedPostError", message);
        } else {
            session.setAttribute(success ? "detailSuccess" : "detailError", message);
        }
    }

    private String buildRedirect(HttpServletRequest request, String source, BaiDang baiDang) {
        String normalizedSource = source == null ? "" : source.trim().toLowerCase();
        if ("saved-posts".equals(normalizedSource)) {
            return request.getContextPath() + "/saved-posts";
        }
        if (baiDang != null) {
            return request.getContextPath() + "/detail?id=" + baiDang.getMaXe();
        }
        return request.getContextPath() + "/home";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!user.isThanhVien() || "blocked".equalsIgnoreCase(user.getTrangThai())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String action = request.getParameter("action");
        String source = request.getParameter("source");
        int maBaiDang;
        try {
            maBaiDang = Integer.parseInt(request.getParameter("maBaiDang"));
        } catch (NumberFormatException e) {
            setFlash(request, "saved-posts", "Không xác định được bài đăng cần lưu.", false);
            response.sendRedirect(request.getContextPath() + "/saved-posts");
            return;
        }

        BaiDangDAO baiDangDAO = new BaiDangDAO();
        BaiDang baiDang = "unsave".equalsIgnoreCase(action)
                ? baiDangDAO.getBaiDangByIdForAdmin(maBaiDang)
                : baiDangDAO.getApprovedBaiDangById(maBaiDang);
        if (baiDang == null) {
            setFlash(request, "saved-posts", "Bài đăng không tồn tại hoặc chưa được duyệt công khai.", false);
            response.sendRedirect(request.getContextPath() + "/saved-posts");
            return;
        }

        if (baiDang.getMaNguoiDung() == user.getMaNguoiDung()) {
            setFlash(request, source, "Bạn không cần lưu chính bài đăng của mình.", false);
            response.sendRedirect(buildRedirect(request, source, baiDang));
            return;
        }

        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        if (!"unsave".equalsIgnoreCase(action)
                && (baiDang.isBiKhoaGiaoDich()
                || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe())
                || giaoDichXeDAO.hasActiveTransactionForCar(baiDang.getMaXe()))) {
            setFlash(request, source, "Xe này đang được giao dịch hoặc đã bán nên không thể lưu thêm bài đăng.", false);
            response.sendRedirect(buildRedirect(request, source, baiDang));
            return;
        }

        SavedPostDAO savedPostDAO = new SavedPostDAO();
        boolean success;
        String message;
        if ("unsave".equalsIgnoreCase(action)) {
            success = savedPostDAO.unsave(user.getMaNguoiDung(), maBaiDang);
            message = success ? "Đã bỏ lưu bài đăng." : "Không thể bỏ lưu bài đăng lúc này.";
        } else {
            success = savedPostDAO.save(user.getMaNguoiDung(), maBaiDang);
            message = success ? "Đã lưu bài đăng vào danh sách yêu thích." : "Không thể lưu bài đăng lúc này.";
        }

        setFlash(request, source, message, success);
        response.sendRedirect(buildRedirect(request, source, baiDang));
    }
}
