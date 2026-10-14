package controller;

import dao.BaiDangDAO;
import dao.DangKySellerDAO;
import dao.HangXeDAO;
import dao.NguoiDungDAO;
import dao.TinhThanhDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BaiDang;
import model.NguoiDung;
import model.TinhThanh;
import model.Xe;
import util.WebAppUrlUtil;

@WebServlet("/add-post")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class AddPostServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddPostServlet.class.getName());
    private static final String UPLOAD_DIR = "uploads";
    private static final int MAX_IMAGES = 5;

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private boolean isAllowedImagePart(Part part, String fileName) {
        if (part == null || part.getSize() <= 0 || fileName == null || fileName.isBlank()) {
            return false;
        }
        String contentType = part.getContentType();
        String normalizedType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        String normalizedName = fileName.toLowerCase(Locale.ROOT);
        boolean allowedExtension = normalizedName.endsWith(".jpg")
                || normalizedName.endsWith(".jpeg")
                || normalizedName.endsWith(".png")
                || normalizedName.endsWith(".webp")
                || normalizedName.endsWith(".gif");
        return normalizedType.startsWith("image/") && allowedExtension;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseRequiredInt(String rawValue, String fieldName) {
        try {
            return Integer.parseInt(trimToEmpty(rawValue));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " không hợp lệ.");
        }
    }

    private int parseOptionalInt(String rawValue, int defaultValue, String fieldName) {
        String normalized = trimToEmpty(rawValue);
        if (normalized.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " không hợp lệ.");
        }
    }

    private double parseRequiredDouble(String rawValue, String fieldName) {
        try {
            return Double.parseDouble(trimToEmpty(rawValue));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " không hợp lệ.");
        }
    }

    private void prepareForm(HttpServletRequest request) {
        TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();
        List<TinhThanh> listTinhThanh = tinhThanhDAO.getAllTinhThanh();
        request.setAttribute("listTinhThanh", listTinhThanh);
        request.setAttribute("listMien", tinhThanhDAO.getAllMien());
        request.setAttribute("listHangXe", new HangXeDAO().getAllHangXe());
        request.setAttribute("maxImages", MAX_IMAGES);
    }

    private List<String> saveUploadedImages(HttpServletRequest request) throws IOException, ServletException {
        List<String> imagePaths = new ArrayList<>();
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (Part part : request.getParts()) {
            if (!"hinhAnh".equals(part.getName()) || part.getSize() <= 0) {
                continue;
            }

            String submittedName = sanitizeFileName(part.getSubmittedFileName());
            if (submittedName.isEmpty()) {
                continue;
            }
            if (!isAllowedImagePart(part, submittedName)) {
                deleteUploadedFiles(imagePaths, request);
                throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG, WEBP hoặc GIF.");
            }

            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + submittedName;
            String filePath = uploadPath + File.separator + fileName;
            part.write(filePath);
            imagePaths.add(UPLOAD_DIR + "/" + fileName);
        }

        return imagePaths;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        prepareForm(request);
        request.getRequestDispatcher("add-post.jsp").forward(request, response);
    }

    private NguoiDung refreshCurrentUser(HttpServletRequest request) {
        NguoiDung sessionUser = (NguoiDung) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return null;
        }

        NguoiDung freshUser = new NguoiDungDAO().getUserById(sessionUser.getMaNguoiDung());
        if (freshUser == null) {
            request.getSession().removeAttribute("user");
            return null;
        }

        request.getSession().setAttribute("user", freshUser);
        return freshUser;
    }

    private boolean ensureSellerAccess(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws IOException {
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return false;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return false;
        }
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        if (user.isSeller()) {
            dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
            return true;
        }

        if (dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung())
                && dangKySellerDAO.updateUserToSellerIfApproved(user.getMaNguoiDung())) {
            NguoiDung refreshedUser = refreshCurrentUser(request);
            if (refreshedUser != null && refreshedUser.isSeller()) {
                return true;
            }
        }

        WebAppUrlUtil.redirect(request, response, "seller-register?needSeller=1");
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = refreshCurrentUser(request);
        if (!ensureSellerAccess(request, response, user)) {
            return;
        }

        prepareForm(request);
        request.getRequestDispatcher("add-post.jsp").forward(request, response);
    }

    private void deleteUploadedFiles(List<String> imagePaths, HttpServletRequest request) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return;
        }
        String basePath = getServletContext().getRealPath("") + File.separator;
        for (String relativePath : imagePaths) {
            if (relativePath == null || relativePath.isBlank()) {
                continue;
            }
            File file = new File(basePath + relativePath.replace("/", File.separator));
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        NguoiDung user = refreshCurrentUser(request);
        if (!ensureSellerAccess(request, response, user)) {
            return;
        }

        List<String> newImagePaths = new ArrayList<>();
        try {
            // Bước 1: Parse và validate tất cả field text TRƯỚC khi lưu file
            String tieuDe = trimToEmpty(request.getParameter("tieuDe"));
            String tenXe = trimToEmpty(request.getParameter("tenXe"));
            String moTa = trimToEmpty(request.getParameter("moTa"));
            String hopSo = trimToEmpty(request.getParameter("hopSo"));
            String nhienLieu = trimToEmpty(request.getParameter("nhienLieu"));
            String tinhTrang = trimToEmpty(request.getParameter("tinhTrang"));
            String kieuDang = trimToEmpty(request.getParameter("kieuDang"));
            String mauXe = trimToEmpty(request.getParameter("mauXe"));
            String xuatXu = trimToEmpty(request.getParameter("xuatXu"));

            double giaBan = parseRequiredDouble(request.getParameter("giaBan"), "Giá bán");
            int maHang = parseRequiredInt(request.getParameter("maHang"), "Hãng xe");
            int namSanXuat = parseRequiredInt(request.getParameter("namSanXuat"), "Năm sản xuất");
            int soKmDaDi = parseOptionalInt(request.getParameter("soKmDaDi"), 0, "Số km đã đi");
            int soCho = parseOptionalInt(request.getParameter("soCho"), 5, "Số chỗ");
            int maTinh = parseRequiredInt(request.getParameter("maTinh"), "Tỉnh/Thành");

            if (!new HangXeDAO().exists(maHang)) {
                forwardWithError(request, response, "Hãng xe được chọn không hợp lệ.");
                return;
            }
            if (!new TinhThanhDAO().exists(maTinh)) {
                forwardWithError(request, response, "Tỉnh/Thành được chọn không hợp lệ.");
                return;
            }
            if (tieuDe.isEmpty()) {
                forwardWithError(request, response, "Vui lòng nhập tiêu đề bài đăng.");
                return;
            }
            if (tenXe.isEmpty()) {
                forwardWithError(request, response, "Vui lòng nhập tên xe.");
                return;
            }
            if (giaBan <= 0) {
                forwardWithError(request, response, "Giá bán phải lớn hơn 0.");
                return;
            }
            if (namSanXuat < 1900) {
                forwardWithError(request, response, "Năm sản xuất không hợp lệ.");
                return;
            }

            // Bước 2: Lưu ảnh SAU khi text hợp lệ
            newImagePaths = saveUploadedImages(request);
            if (newImagePaths.isEmpty()) {
                forwardWithError(request, response, "Vui lòng tải lên ít nhất 1 ảnh của xe.");
                return;
            }
            if (newImagePaths.size() > MAX_IMAGES) {
                deleteUploadedFiles(newImagePaths, request);
                newImagePaths = new ArrayList<>();
                forwardWithError(request, response, "Bạn chỉ được tải lên tối đa " + MAX_IMAGES + " ảnh.");
                return;
            }

            // Bước 3: Xây dựng object và insert atomic
            Xe xe = new Xe();
            xe.setMaHang(maHang);
            xe.setTenXe(tenXe);
            xe.setGia(giaBan);
            xe.setNamSanXuat(namSanXuat);
            xe.setHopSo(hopSo);
            xe.setNhienLieu(nhienLieu);
            xe.setSoKmDaDi(Math.max(soKmDaDi, 0));
            xe.setMoTa(moTa);
            xe.setTinhTrang(tinhTrang);
            xe.setKieuDang(kieuDang);
            xe.setSoCho(Math.max(soCho, 1));
            xe.setMauXe(mauXe);
            xe.setXuatXu(xuatXu);
            xe.setDanhSachHinhAnh(newImagePaths);

            BaiDang bd = new BaiDang();
            bd.setMaNguoiDung(user.getMaNguoiDung());
            bd.setMaTinh(maTinh);
            bd.setTieuDe(tieuDe);
            bd.setMoTa(moTa);
            bd.setTrangThai("cho_duyet");

            // Insert xe và baidang trong 1 transaction — không còn orphaned record
            int maXe = new BaiDangDAO().insertXeVaBaiDang(xe, bd);
            if (maXe <= 0) {
                deleteUploadedFiles(newImagePaths, request);
                newImagePaths = new ArrayList<>();
                forwardWithError(request, response, "Không thể tạo bài đăng. Vui lòng thử lại.");
                return;
            }

            WebAppUrlUtil.redirect(request, response, "my-posts?success=1");
        } catch (IllegalArgumentException e) {
            deleteUploadedFiles(newImagePaths, request);
            forwardWithError(request, response, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot create post", e);
            deleteUploadedFiles(newImagePaths, request);
            forwardWithError(request, response, "Lỗi hệ thống, vui lòng thử lại.");
        }
    }
}
