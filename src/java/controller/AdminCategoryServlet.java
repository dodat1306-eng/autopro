package controller;

import dao.HangXeDAO;
import dao.TinhThanhDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import model.HangXe;
import model.NguoiDung;
import model.TinhThanh;

@WebServlet("/admin/categories")
public class AdminCategoryServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        return user != null && user.isAdmin();
    }

    private void moveFlashMessage(HttpServletRequest request, String sessionKey, String requestKey) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object message = session.getAttribute(sessionKey);
        if (message != null) {
            request.setAttribute(requestKey, message);
            session.removeAttribute(sessionKey);
        }
    }

    private void setFlash(HttpServletRequest request, boolean success, String message) {
        request.getSession().setAttribute(success ? "adminCategorySuccess" : "adminCategoryError", message);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String slugify(String raw) {
        String normalized = Normalizer.normalize(trimToEmpty(raw), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? null : normalized;
    }

    private void loadData(HttpServletRequest request) {
        request.setAttribute("hangXeList", new HangXeDAO().getAllHangXe());
        request.setAttribute("tinhThanhList", new TinhThanhDAO().getAllTinhThanh());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        moveFlashMessage(request, "adminCategorySuccess", "adminCategorySuccess");
        moveFlashMessage(request, "adminCategoryError", "adminCategoryError");
        loadData(request);
        request.getRequestDispatcher("/admin_categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String entity = trimToEmpty(request.getParameter("entity")).toLowerCase(Locale.ROOT);
        String action = trimToEmpty(request.getParameter("action")).toLowerCase(Locale.ROOT);

        switch (entity) {
            case "hangxe":
                handleHangXe(request, action);
                break;
            case "tinhthanh":
                handleTinhThanh(request, action);
                break;
            default:
                setFlash(request, false, "Không xác định được loại danh mục cần xử lý.");
                break;
        }
        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }

    private void handleHangXe(HttpServletRequest request, String action) {
        HangXeDAO dao = new HangXeDAO();
        int maHang = parseInt(request.getParameter("maHang"), -1);
        String tenHang = trimToEmpty(request.getParameter("tenHang"));
        if ("delete".equals(action)) {
            if (maHang <= 0) {
                setFlash(request, false, "Mã hãng xe không hợp lệ.");
                return;
            }
            if (dao.countCarsByHang(maHang) > 0) {
                setFlash(request, false, "Không thể xóa hãng xe đang được xe trong hệ thống sử dụng.");
                return;
            }
            boolean success = dao.delete(maHang);
            setFlash(request, success, success
                    ? "Đã xóa hãng xe khỏi danh mục."
                    : "Không thể xóa hãng xe lúc này.");
            return;
        }

        if (maHang <= 0 || tenHang.isBlank()) {
            setFlash(request, false, "Vui lòng nhập đầy đủ mã hãng và tên hãng.");
            return;
        }

        HangXe hangXe = new HangXe();
        hangXe.setMaHang(maHang);
        hangXe.setTenHang(tenHang);
        hangXe.setSlug(trimToEmpty(request.getParameter("slug")).isBlank()
                ? slugify(tenHang)
                : trimToEmpty(request.getParameter("slug")));
        hangXe.setLogoUrl(trimToEmpty(request.getParameter("logoUrl")));
        hangXe.setTuKhoaDongBo(trimToEmpty(request.getParameter("tuKhoaDongBo")));
        hangXe.setThuTuSapXep(parseInt(request.getParameter("thuTuSapXep"), 0));

        boolean success = "create".equals(action) ? dao.insert(hangXe) : dao.update(hangXe);
        setFlash(request, success, success
                ? ("create".equals(action) ? "Đã thêm hãng xe mới." : "Đã cập nhật hãng xe.")
                : ("create".equals(action) ? "Không thể thêm hãng xe." : "Không thể cập nhật hãng xe."));
    }

    private void handleTinhThanh(HttpServletRequest request, String action) {
        TinhThanhDAO dao = new TinhThanhDAO();
        int maTinh = parseInt(request.getParameter("maTinh"), -1);
        String tenTinh = trimToEmpty(request.getParameter("tenTinh"));
        if ("delete".equals(action)) {
            if (maTinh <= 0) {
                setFlash(request, false, "Mã tỉnh/thành không hợp lệ.");
                return;
            }
            if (dao.countPostsByTinh(maTinh) > 0) {
                setFlash(request, false, "Không thể xóa tỉnh/thành đang được bài đăng sử dụng.");
                return;
            }
            boolean success = dao.delete(maTinh);
            setFlash(request, success, success
                    ? "Đã xóa tỉnh/thành khỏi danh mục."
                    : "Không thể xóa tỉnh/thành lúc này.");
            return;
        }

        if (maTinh <= 0 || tenTinh.isBlank()) {
            setFlash(request, false, "Vui lòng nhập đầy đủ mã tỉnh/thành và tên tỉnh/thành.");
            return;
        }

        TinhThanh tinhThanh = new TinhThanh();
        tinhThanh.setMaTinh(maTinh);
        tinhThanh.setTenTinh(tenTinh);
        String mien = trimToEmpty(request.getParameter("mien"));
        tinhThanh.setMien(mien.isBlank() ? "Miền Bắc" : mien);

        boolean success = "create".equals(action) ? dao.insert(tinhThanh) : dao.update(tinhThanh);
        setFlash(request, success, success
                ? ("create".equals(action) ? "Đã thêm tỉnh/thành mới." : "Đã cập nhật tỉnh/thành.")
                : ("create".equals(action) ? "Không thể thêm tỉnh/thành." : "Không thể cập nhật tỉnh/thành."));
    }
}
