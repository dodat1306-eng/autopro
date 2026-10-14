package controller;

import dao.DangKySellerDAO;
import dao.NguoiDungDAO;
import dao.ThongBaoDAO;
import dao.TinhThanhDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DangKySeller;
import model.NguoiDung;
import model.TinhThanh;
import util.WebAppUrlUtil;

@WebServlet(urlPatterns = {"/seller-register", "/seller-register/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 30
)
public class DangKySellerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DangKySellerServlet.class.getName());
    private static final String UPLOAD_DIR = "uploads/seller-verify";

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private boolean isImagePart(Part part) {
        if (part == null || part.getSize() <= 0) {
            return false;
        }
        String fileName = sanitizeFileName(part.getSubmittedFileName()).toLowerCase(Locale.ROOT);
        String contentType = part.getContentType() == null ? "" : part.getContentType().toLowerCase(Locale.ROOT);
        boolean allowedExtension = fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".webp")
                || fileName.endsWith(".gif");
        return contentType.startsWith("image/") && allowedExtension;
    }

    private boolean isValidEmail(String email) {
        return trimToEmpty(email).matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isValidPhone(String phone) {
        return trimToEmpty(phone).matches("^[0-9+]{9,15}$");
    }

    private boolean isValidCccd(String soCCCD) {
        return trimToEmpty(soCCCD).matches("^[0-9]{9,12}$");
    }

    private Integer parseInteger(String rawValue) {
        try {
            return Integer.parseInt(trimToEmpty(rawValue));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean redirectIfNonCanonicalPath(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.isBlank() && !"/".equals(pathInfo)) {
            WebAppUrlUtil.redirect(request, response, "seller-register");
            return true;
        }
        return false;
    }

    private NguoiDung refreshCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        NguoiDung sessionUser = (NguoiDung) session.getAttribute("user");
        if (sessionUser == null) {
            return null;
        }

        NguoiDung freshUser = new NguoiDungDAO().getUserById(sessionUser.getMaNguoiDung());
        if (freshUser == null) {
            session.removeAttribute("user");
            return null;
        }
        session.setAttribute("user", freshUser);
        return freshUser;
    }

    private void preparePage(HttpServletRequest request, NguoiDung user, DangKySeller formData) {
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        DangKySeller latestRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
        boolean approvedSeller = dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung()) || user.isSeller();
        if (latestRequest == null && approvedSeller) {
            latestRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
        }
        List<DangKySeller> requestHistory = dangKySellerDAO.getHistoryByUserId(user.getMaNguoiDung());
        List<TinhThanh> listTinhThanh = new TinhThanhDAO().getAllTinhThanh();
        boolean pendingRequest = latestRequest != null && latestRequest.isChoDuyet();
        boolean canSubmitNewRequest = !approvedSeller && !pendingRequest;

        request.setAttribute("currentUser", user);
        request.setAttribute("listTinhThanh", listTinhThanh);
        request.setAttribute("latestSellerRequest", latestRequest);
        request.setAttribute("sellerRequestHistory", requestHistory);
        request.setAttribute("isApprovedSeller", approvedSeller);
        request.setAttribute("hasPendingSellerRequest", pendingRequest);
        request.setAttribute("canSubmitSellerRequest", canSubmitNewRequest);
        request.setAttribute("formDangKySeller", formData);
    }

    private String saveImage(Part part, HttpServletRequest request, String prefix) throws IOException {
        return util.PrivateFiles.save(part);
    }

    private void deleteSavedImage(String relativePath, HttpServletRequest request) {
        String normalized = trimToEmpty(relativePath);
        if (normalized.isEmpty()) {
            return;
        }

        if (normalized.startsWith("private/")) {
            try { util.PrivateFiles.removeNew(normalized); } catch (IOException e) { LOGGER.log(Level.WARNING,"Cannot remove upload",e); }
            return;
        }
        String realPath = getServletContext().getRealPath("") + File.separator + normalized.replace("/", File.separator);
        File file = new File(realPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private DangKySeller buildFormData(HttpServletRequest request, NguoiDung user) {
        DangKySeller formData = new DangKySeller();
        formData.setMaNguoiDung(user.getMaNguoiDung());
        formData.setHoTen(trimToEmpty(request.getParameter("hoTen")));
        formData.setSoDienThoai(trimToEmpty(request.getParameter("soDienThoai")));
        formData.setEmail(trimToEmpty(request.getParameter("email")));
        formData.setSoCCCD(trimToEmpty(request.getParameter("soCCCD")));
        formData.setDiaChiChiTiet(trimToEmpty(request.getParameter("diaChiChiTiet")));
        formData.setGhiChuNguoiDangKy(trimToEmpty(request.getParameter("ghiChuNguoiDangKy")));
        Integer maTinh = parseInteger(request.getParameter("maTinh"));
        formData.setMaTinh(maTinh == null ? 0 : maTinh);
        return formData;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            NguoiDung user, DangKySeller formData, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        preparePage(request, user, formData);
        request.getRequestDispatcher("/seller-register.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (redirectIfNonCanonicalPath(request, response)) {
            return;
        }

        NguoiDung user = refreshCurrentUser(request);
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "seller-register");
        preparePage(request, user, null);
        request.getRequestDispatcher("/seller-register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (redirectIfNonCanonicalPath(request, response)) {
            return;
        }

        NguoiDung user = refreshCurrentUser(request);
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        if (dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung()) || user.isSeller()) {
            WebAppUrlUtil.redirect(request, response, "seller-register/status?alreadySeller=1");
            return;
        }
        if (dangKySellerDAO.hasPendingRequest(user.getMaNguoiDung())) {
            WebAppUrlUtil.redirect(request, response, "seller-register/status?pending=1");
            return;
        }

        DangKySeller formData = buildFormData(request, user);
        Part frontImage = request.getPart("anhCCCDMatTruoc");
        Part backImage = request.getPart("anhCCCDMatSau");

        if (trimToEmpty(formData.getHoTen()).isEmpty()) {
            forwardWithError(request, response, user, formData, "Vui lòng nhập họ tên.");
            return;
        }
        if (!isValidPhone(formData.getSoDienThoai())) {
            forwardWithError(request, response, user, formData, "Số điện thoại không hợp lệ.");
            return;
        }
        if (!isValidEmail(formData.getEmail())) {
            forwardWithError(request, response, user, formData, "Email không hợp lệ.");
            return;
        }
        if (!isValidCccd(formData.getSoCCCD())) {
            forwardWithError(request, response, user, formData, "Số CCCD phải gồm 9 đến 12 chữ số.");
            return;
        }
        if (formData.getMaTinh() <= 0 || !new TinhThanhDAO().exists(formData.getMaTinh())) {
            forwardWithError(request, response, user, formData, "Vui lòng chọn tỉnh/thành hợp lệ.");
            return;
        }
        if (frontImage == null || frontImage.getSize() <= 0) {
            forwardWithError(request, response, user, formData, "Bạn phải tải lên ảnh CCCD mặt trước.");
            return;
        }
        if (backImage == null || backImage.getSize() <= 0) {
            forwardWithError(request, response, user, formData, "Bạn phải tải lên ảnh CCCD mặt sau.");
            return;
        }
        if (!isImagePart(frontImage)) {
            forwardWithError(request, response, user, formData, "Ảnh CCCD mặt trước phải là JPG, PNG, WEBP hoặc GIF.");
            return;
        }
        if (!isImagePart(backImage)) {
            forwardWithError(request, response, user, formData, "Ảnh CCCD mặt sau phải là JPG, PNG, WEBP hoặc GIF.");
            return;
        }
        if (dangKySellerDAO.emailInUseByOtherUser(formData.getEmail(), user.getMaNguoiDung())) {
            forwardWithError(request, response, user, formData, "Email này đang được tài khoản khác sử dụng.");
            return;
        }
        if (dangKySellerDAO.phoneInUseByOtherUser(formData.getSoDienThoai(), user.getMaNguoiDung())) {
            forwardWithError(request, response, user, formData, "Số điện thoại này đang được tài khoản khác sử dụng.");
            return;
        }
        if (dangKySellerDAO.cccdInUseByOtherUser(formData.getSoCCCD(), user.getMaNguoiDung())) {
            forwardWithError(request, response, user, formData, "Số CCCD này đã tồn tại trong một hồ sơ seller khác.");
            return;
        }

        String savedFrontImage = "";
        String savedBackImage = "";
        try {
            savedFrontImage = saveImage(frontImage, request, "cccd_truoc");
            savedBackImage = saveImage(backImage, request, "cccd_sau");

            formData.setAnhCCCDMatTruoc(savedFrontImage);
            formData.setAnhCCCDMatSau(savedBackImage);
            formData.setTrangThai(DangKySellerDAO.CHO_DUYET);

            if (!dangKySellerDAO.createRegistration(formData)) {
                deleteSavedImage(savedFrontImage, request);
                deleteSavedImage(savedBackImage, request);
                forwardWithError(request, response, user, formData,
                        "Không thể gửi hồ sơ seller lúc này. Có thể bạn đang có hồ sơ chờ duyệt hoặc thông tin bị trùng.");
                return;
            }

            WebAppUrlUtil.redirect(request, response, "seller-register/status?submitted=1");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot submit seller registration", e);
            deleteSavedImage(savedFrontImage, request);
            deleteSavedImage(savedBackImage, request);
            forwardWithError(request, response, user, formData, "Có lỗi hệ thống khi gửi hồ sơ seller.");
        }
    }
}
