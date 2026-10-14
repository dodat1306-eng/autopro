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

@WebServlet("/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 16
)
public class ProfileServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars";
    private static final String SELLER_VERIFY_UPLOAD_DIR = "uploads/seller-verify";

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isValidEmail(String email) {
        String normalized = trimToEmpty(email);
        return normalized.isEmpty() || normalized.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isValidPhone(String phone) {
        String normalized = trimToEmpty(phone);
        return normalized.isEmpty() || normalized.matches("^[0-9+]{9,15}$");
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

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
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

    private String resolveAssetUrl(HttpServletRequest request, String rawPath) {
        String normalized = trimToEmpty(rawPath);
        if (normalized.isEmpty()) {
            NguoiDung currentUser = (NguoiDung) request.getAttribute("currentUser");
            String fullName = currentUser != null ? trimToEmpty(currentUser.getHoTen()) : "";
            if (fullName.isEmpty()) {
                fullName = "AutoPro";
            }
            return "https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+")
                    + "&background=FFC400&color=111827&size=256";
        }
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }
        if (normalized.startsWith("/")) {
            return request.getContextPath() + normalized;
        }
        return request.getContextPath() + "/" + normalized;
    }

    private String saveAvatar(Part part, HttpServletRequest request, int maNguoiDung) throws IOException {
        String submittedName = sanitizeFileName(part.getSubmittedFileName());
        if (submittedName.isEmpty()) {
            return "";
        }

        String uploadPath = getServletContext().getRealPath("") + File.separator + AVATAR_UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_user_" + maNguoiDung + "_" + submittedName;
        String filePath = uploadPath + File.separator + fileName;
        part.write(filePath);
        return AVATAR_UPLOAD_DIR + "/" + fileName;
    }

    private String saveSellerVerifyImage(Part part, HttpServletRequest request, int maNguoiDung, String prefix) throws IOException {
        return util.PrivateFiles.save(part);
    }

    private void deleteUploadedFile(String relativePath, HttpServletRequest request) {
        String normalized = trimToEmpty(relativePath);
        if (normalized.startsWith("private/")) {
            try { util.PrivateFiles.removeNew(normalized); } catch (IOException ex) { LOGGER.log(Level.WARNING,"Cannot remove upload",ex); }
            return;
        }
        if (normalized.isEmpty()
                || (!normalized.startsWith(AVATAR_UPLOAD_DIR + "/")
                && !normalized.startsWith(SELLER_VERIFY_UPLOAD_DIR + "/"))) {
            return;
        }

        String realPath = getServletContext().getRealPath("") + File.separator + normalized.replace("/", File.separator);
        File file = new File(realPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private boolean isMissingSellerDocument(String documentPath) {
        String normalized = trimToEmpty(documentPath).toLowerCase();
        return normalized.isEmpty() || normalized.contains("chua-cap-nhat");
    }

    private boolean isLegacyGeneratedCccd(String soCCCD, int maNguoiDung) {
        long normalizedId = Math.abs((long) maNguoiDung) % 1000000L;
        String legacyCccd = String.format("900000%06d", normalizedId);
        return legacyCccd.equals(trimToEmpty(soCCCD));
    }

    private void preparePage(HttpServletRequest request, NguoiDung user) {
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        DangKySeller latestSellerRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
        boolean approvedSeller = user.isSeller() || dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung());
        if (latestSellerRequest == null && approvedSeller) {
            latestSellerRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
        }
        List<TinhThanh> listTinhThanh = new TinhThanhDAO().getAllTinhThanh();

        request.setAttribute("currentUser", user);
        request.setAttribute("avatarUrl", resolveAssetUrl(request, user.getAnhDaiDien()));
        request.setAttribute("latestSellerRequest", latestSellerRequest);
        request.setAttribute("sellerRequestHistory", dangKySellerDAO.getHistoryByUserId(user.getMaNguoiDung()));
        request.setAttribute("listTinhThanh", listTinhThanh);
        request.setAttribute("isApprovedSeller", approvedSeller);
        request.setAttribute("hasPendingSellerRequest", latestSellerRequest != null && latestSellerRequest.isChoDuyet());
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            NguoiDung user, String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        preparePage(request, user);
        request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = refreshCurrentUser(request);
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "profile");
        preparePage(request, user);
        request.getRequestDispatcher("/user-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        NguoiDung user = refreshCurrentUser(request);
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }

        String action = trimToEmpty(request.getParameter("action"));
        if (action.isEmpty()) {
            WebAppUrlUtil.redirect(request, response, "profile");
            return;
        }

        if ("updateBasic".equals(action)) {
            handleBasicUpdate(request, response, user);
            return;
        }

        if ("updateSellerInfo".equals(action)) {
            handleSellerInfoUpdate(request, response, user);
            return;
        }

        if ("updateSellerIdentity".equals(action)) {
            handleSellerIdentityUpdate(request, response, user);
            return;
        }

        WebAppUrlUtil.redirect(request, response, "profile");
    }

    private void handleBasicUpdate(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws ServletException, IOException {
        String hoTen = trimToEmpty(request.getParameter("hoTen"));
        String email = trimToEmpty(request.getParameter("email"));
        String soDienThoai = trimToEmpty(request.getParameter("soDienThoai"));

        if (!email.equalsIgnoreCase(trimToEmpty(user.getEmail())) || !soDienThoai.equals(trimToEmpty(user.getSoDienThoai()))) {
            forwardWithError(request, response, user, "Để thay email hoặc số điện thoại, mở mục Bảo mật và xác minh bằng OTP.");
            return;
        }
        if (hoTen.isEmpty()) {
            forwardWithError(request, response, user, "Vui lòng nhập họ tên.");
            return;
        }
        if (email.isEmpty() && soDienThoai.isEmpty()) {
            forwardWithError(request, response, user, "Bạn cần cập nhật ít nhất email hoặc số điện thoại.");
            return;
        }
        if (!isValidEmail(email)) {
            forwardWithError(request, response, user, "Email không hợp lệ.");
            return;
        }
        if (!isValidPhone(soDienThoai)) {
            forwardWithError(request, response, user, "Số điện thoại không hợp lệ.");
            return;
        }

        NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
        if (!email.isEmpty() && nguoiDungDAO.emailInUseByOtherUser(email, user.getMaNguoiDung())) {
            forwardWithError(request, response, user, "Email này đang được tài khoản khác sử dụng.");
            return;
        }
        if (!soDienThoai.isEmpty() && nguoiDungDAO.phoneInUseByOtherUser(soDienThoai, user.getMaNguoiDung())) {
            forwardWithError(request, response, user, "Số điện thoại này đang được tài khoản khác sử dụng.");
            return;
        }

        String oldAvatar = trimToEmpty(user.getAnhDaiDien());
        String savedAvatar = null;
        try {
            Part avatarPart = request.getPart("anhDaiDien");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                if (!isImagePart(avatarPart)) {
                    forwardWithError(request, response, user, "Ảnh đại diện phải là file hình ảnh hợp lệ.");
                    return;
                }
                savedAvatar = saveAvatar(avatarPart, request, user.getMaNguoiDung());
            }

            if (!nguoiDungDAO.updateProfile(user.getMaNguoiDung(), hoTen, email, soDienThoai, savedAvatar)) {
                deleteUploadedFile(savedAvatar, request);
                forwardWithError(request, response, user, "Không thể cập nhật hồ sơ lúc này.");
                return;
            }

            DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
            DangKySeller latestSellerRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
            if (latestSellerRequest == null && user.isSeller()) {
                latestSellerRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                        user.getMaNguoiDung(),
                        hoTen,
                        email,
                        soDienThoai
                );
            }
            if (latestSellerRequest != null) {
                dangKySellerDAO.syncLatestContactInfo(user.getMaNguoiDung(), hoTen, email, soDienThoai);
            }

            if (savedAvatar != null && !oldAvatar.isEmpty() && !oldAvatar.equals(savedAvatar)) {
                deleteUploadedFile(oldAvatar, request);
            }

            refreshCurrentUser(request);
            WebAppUrlUtil.redirect(request, response, "profile?updated=1");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot update profile", e);
            deleteUploadedFile(savedAvatar, request);
            forwardWithError(request, response, user, "Có lỗi xảy ra khi cập nhật hồ sơ.");
        }
    }

    private void handleSellerInfoUpdate(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws ServletException, IOException {
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        DangKySeller latestSellerRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
        if (latestSellerRequest == null && user.isSeller()) {
            latestSellerRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
        }
        if (latestSellerRequest == null || latestSellerRequest.isDaXoa()) {
            forwardWithError(request, response, user, "Tài khoản của bạn chưa có hồ sơ seller để cập nhật.");
            return;
        }

        Integer maTinh = parseInteger(request.getParameter("maTinh"));
        String diaChiChiTiet = trimToEmpty(request.getParameter("diaChiChiTiet"));
        String ghiChuNguoiDangKy = trimToEmpty(request.getParameter("ghiChuNguoiDangKy"));

        if (maTinh == null || maTinh <= 0 || !new TinhThanhDAO().exists(maTinh)) {
            forwardWithError(request, response, user, "Vui lòng chọn tỉnh/thành hợp lệ cho hồ sơ người bán.");
            return;
        }

        if (!dangKySellerDAO.updateLatestSellerExtraInfo(user.getMaNguoiDung(), maTinh, diaChiChiTiet, ghiChuNguoiDangKy)) {
            forwardWithError(request, response, user, "Không thể cập nhật thông tin người bán lúc này.");
            return;
        }

        WebAppUrlUtil.redirect(request, response, "profile?sellerUpdated=1");
    }

    private void handleSellerIdentityUpdate(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws ServletException, IOException {
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        DangKySeller latestSellerRequest = dangKySellerDAO.getByUserId(user.getMaNguoiDung());
        if (latestSellerRequest == null && user.isSeller()) {
            latestSellerRequest = dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
        }
        if (latestSellerRequest == null || latestSellerRequest.isDaXoa()) {
            forwardWithError(request, response, user, "Tài khoản của bạn chưa có hồ sơ seller để cập nhật CCCD.");
            return;
        }

        if (dangKySellerDAO.hasPendingRequest(user.getMaNguoiDung())) {
            forwardWithError(request, response, user, "Bạn đã có hồ sơ đang chờ duyệt. Vui lòng chờ admin xử lý trước khi gửi thay đổi.");
            return;
        }
        String soCCCD = trimToEmpty(request.getParameter("soCCCD"));
        if (!isValidCccd(soCCCD)) {
            forwardWithError(request, response, user, "Số CCCD phải gồm 9 đến 12 chữ số.");
            return;
        }
        if (isLegacyGeneratedCccd(soCCCD, user.getMaNguoiDung())) {
            forwardWithError(request, response, user, "Vui lòng nhập số CCCD thật thay cho mã tạm của hồ sơ seller cũ.");
            return;
        }
        if (dangKySellerDAO.cccdInUseByOtherUser(soCCCD, user.getMaNguoiDung())) {
            forwardWithError(request, response, user, "Số CCCD này đang được dùng trong một hồ sơ seller khác.");
            return;
        }

        Part frontPart = request.getPart("anhCCCDMatTruoc");
        Part backPart = request.getPart("anhCCCDMatSau");
        boolean hasFrontUpload = frontPart != null && frontPart.getSize() > 0;
        boolean hasBackUpload = backPart != null && backPart.getSize() > 0;
        boolean missingFront = isMissingSellerDocument(latestSellerRequest.getAnhCCCDMatTruoc());
        boolean missingBack = isMissingSellerDocument(latestSellerRequest.getAnhCCCDMatSau());

        if (missingFront && !hasFrontUpload) {
            forwardWithError(request, response, user, "Bạn cần tải lên ảnh CCCD mặt trước.");
            return;
        }
        if (missingBack && !hasBackUpload) {
            forwardWithError(request, response, user, "Bạn cần tải lên ảnh CCCD mặt sau.");
            return;
        }
        if (hasFrontUpload && !isImagePart(frontPart)) {
            forwardWithError(request, response, user, "Ảnh CCCD mặt trước phải là file hình ảnh hợp lệ.");
            return;
        }
        if (hasBackUpload && !isImagePart(backPart)) {
            forwardWithError(request, response, user, "Ảnh CCCD mặt sau phải là file hình ảnh hợp lệ.");
            return;
        }

        String savedFrontImage = null;
        String savedBackImage = null;
        try {
            if (hasFrontUpload) {
                savedFrontImage = saveSellerVerifyImage(frontPart, request, user.getMaNguoiDung(), "front");
            }
            if (hasBackUpload) {
                savedBackImage = saveSellerVerifyImage(backPart, request, user.getMaNguoiDung(), "back");
            }

            if (!dangKySellerDAO.updateLatestSellerIdentityInfo(
                    user.getMaNguoiDung(), soCCCD, savedFrontImage, savedBackImage)) {
                deleteUploadedFile(savedFrontImage, request);
                deleteUploadedFile(savedBackImage, request);
                forwardWithError(request, response, user, "Không thể cập nhật CCCD lúc này.");
                return;
            }

            WebAppUrlUtil.redirect(request, response, "profile?identityUpdated=1");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot update seller identity", e);
            deleteUploadedFile(savedFrontImage, request);
            deleteUploadedFile(savedBackImage, request);
            forwardWithError(request, response, user, "Có lỗi xảy ra khi cập nhật CCCD.");
        }
    }
}
