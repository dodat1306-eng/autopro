package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import dao.HangXeDAO;
import dao.TinhThanhDAO;
import dao.XeDAO;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BaiDang;
import model.NguoiDung;
import model.TinhThanh;
import model.Xe;
import util.WebAppUrlUtil;

@WebServlet(name = "EditPostServlet", urlPatterns = {"/edit-post"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class EditPostServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EditPostServlet.class.getName());
    private static final String UPLOAD_DIR = "uploads";
    private static final int MAX_IMAGES = 5;

    private void prepareForm(HttpServletRequest request, BaiDang baiDang, Xe xe) {
        List<TinhThanh> listTinhThanh = new TinhThanhDAO().getAllTinhThanh();
        request.setAttribute("listTinhThanh", listTinhThanh);
        request.setAttribute("listHangXe", new HangXeDAO().getAllHangXe());
        request.setAttribute("baiDang", baiDang);
        request.setAttribute("xe", xe);
        request.setAttribute("maxImages", MAX_IMAGES);
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

    private List<String> parseExistingImages(String rawValue, List<String> allowedImages) {
        List<String> images = new ArrayList<>();
        String normalized = trimToEmpty(rawValue);
        if (normalized.isEmpty()) {
            return images;
        }

        Set<String> allowed = new LinkedHashSet<>(allowedImages == null ? List.of() : allowedImages);
        for (String image : normalized.split(",")) {
            String cleaned = trimToEmpty(image);
            if (!cleaned.isEmpty() && allowed.contains(cleaned) && !images.contains(cleaned)) {
                images.add(cleaned);
            }
        }
        return images;
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

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            BaiDang baiDang, Xe xe, String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        prepareForm(request, baiDang, xe);
        request.getRequestDispatcher("edit-post.jsp").forward(request, response);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BaiDang baiDang = new BaiDangDAO().getBaiDangById(id, user.getMaNguoiDung());
            if (baiDang == null) {
                WebAppUrlUtil.redirect(request, response, "my-posts");
                return;
            }
            GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
            if (baiDang.isBiKhoaGiaoDich()
                    || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe())
                    || giaoDichXeDAO.hasActiveTransactionForCar(baiDang.getMaXe())) {
                WebAppUrlUtil.redirect(request, response, "my-posts?soldLocked=1");
                return;
            }

            Xe xe = new XeDAO().getXeById(baiDang.getMaXe());
            if (xe == null) {
                WebAppUrlUtil.redirect(request, response, "my-posts");
                return;
            }

            prepareForm(request, baiDang, xe);
            request.getRequestDispatcher("edit-post.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            WebAppUrlUtil.redirect(request, response, "my-posts");
        }
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

        BaiDangDAO baiDangDAO = new BaiDangDAO();
        XeDAO xeDAO = new XeDAO();

        BaiDang baiDang;
        Xe xe;
        try {
            int maBaiDang = parseRequiredInt(request.getParameter("maBaiDang"), "Bài đăng");
            baiDang = baiDangDAO.getBaiDangById(maBaiDang, user.getMaNguoiDung());
            if (baiDang == null) {
                WebAppUrlUtil.redirect(request, response, "my-posts");
                return;
            }
            GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
            if (baiDang.isBiKhoaGiaoDich()
                    || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe())
                    || giaoDichXeDAO.hasActiveTransactionForCar(baiDang.getMaXe())) {
                WebAppUrlUtil.redirect(request, response, "my-posts?soldLocked=1");
                return;
            }

            xe = xeDAO.getXeById(baiDang.getMaXe());
            if (xe == null) {
                WebAppUrlUtil.redirect(request, response, "my-posts");
                return;
            }
        } catch (IllegalArgumentException e) {
            WebAppUrlUtil.redirect(request, response, "my-posts");
            return;
        }

        // Track riêng file mới upload để có thể xóa khi lỗi
        List<String> newImages = new ArrayList<>();
        try {
            baiDang.setTieuDe(trimToEmpty(request.getParameter("tieuDe")));
            baiDang.setMoTa(trimToEmpty(request.getParameter("moTa")));
            baiDang.setMaTinh(parseOptionalInt(request.getParameter("maTinh"), 0, "Tỉnh thành"));
            baiDang.setTrangThai("cho_duyet");

            xe.setMaHang(parseRequiredInt(request.getParameter("maHang"), "Hãng xe"));
            xe.setTenXe(trimToEmpty(request.getParameter("tenXe")));
            xe.setGia(parseRequiredDouble(request.getParameter("giaBan"), "Giá bán"));
            xe.setNamSanXuat(parseRequiredInt(request.getParameter("namSanXuat"), "Năm sản xuất"));
            xe.setHopSo(trimToEmpty(request.getParameter("hopSo")));
            xe.setNhienLieu(trimToEmpty(request.getParameter("nhienLieu")));
            xe.setSoKmDaDi(parseOptionalInt(request.getParameter("soKmDaDi"), 0, "Số km đã đi"));
            xe.setKieuDang(trimToEmpty(request.getParameter("kieuDang")));
            xe.setSoCho(parseOptionalInt(request.getParameter("soCho"), 5, "Số chỗ"));
            xe.setMauXe(trimToEmpty(request.getParameter("mauXe")));
            xe.setXuatXu(trimToEmpty(request.getParameter("xuatXu")));
            xe.setTinhTrang(trimToEmpty(request.getParameter("tinhTrang")));
            xe.setMoTa(baiDang.getMoTa());

            if (!new HangXeDAO().exists(xe.getMaHang())) {
                forwardWithError(request, response, baiDang, xe, "Hãng xe được chọn không hợp lệ.");
                return;
            }
            if (baiDang.getMaTinh() <= 0 || !new TinhThanhDAO().exists(baiDang.getMaTinh())) {
                forwardWithError(request, response, baiDang, xe, "Tỉnh/Thành được chọn không hợp lệ.");
                return;
            }
            if (baiDang.getTieuDe().isEmpty()) {
                forwardWithError(request, response, baiDang, xe, "Vui lòng nhập tiêu đề bài đăng.");
                return;
            }
            if (xe.getTenXe().isEmpty()) {
                forwardWithError(request, response, baiDang, xe, "Vui lòng nhập tên xe.");
                return;
            }
            if (xe.getGia() <= 0) {
                forwardWithError(request, response, baiDang, xe, "Giá bán phải lớn hơn 0.");
                return;
            }
            if (xe.getNamSanXuat() < 1900) {
                forwardWithError(request, response, baiDang, xe, "Năm sản xuất không hợp lệ.");
                return;
            }

            // Upload file ảnh SAU khi text đã validate xong
            newImages = saveUploadedImages(request);
            List<String> allImages = new ArrayList<>(
                    parseExistingImages(request.getParameter("existingImages"), xe.getDanhSachHinhAnh())
            );
            allImages.addAll(newImages);

            if (allImages.isEmpty()) {
                deleteUploadedFiles(newImages, request);
                newImages = new ArrayList<>();
                forwardWithError(request, response, baiDang, xe, "Bài đăng cần có ít nhất 1 ảnh của xe.");
                return;
            }
            if (allImages.size() > MAX_IMAGES) {
                deleteUploadedFiles(newImages, request);
                newImages = new ArrayList<>();
                forwardWithError(request, response, baiDang, xe, "Bạn chỉ có thể lưu tối đa " + MAX_IMAGES + " ảnh.");
                return;
            }
            xe.setDanhSachHinhAnh(allImages);

            boolean updated = baiDangDAO.updateBaiDangVaXe(baiDang, xe);
            if (!updated) {
                deleteUploadedFiles(newImages, request);
                newImages = new ArrayList<>();
                forwardWithError(request, response, baiDang, xe, "Không thể cập nhật bài đăng. Vui lòng thử lại.");
                return;
            }

            WebAppUrlUtil.redirect(request, response, "my-posts?updated=1");
        } catch (IllegalArgumentException e) {
            deleteUploadedFiles(newImages, request);
            forwardWithError(request, response, baiDang, xe, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot update post", e);
            deleteUploadedFiles(newImages, request);
            forwardWithError(request, response, baiDang, xe, "Có lỗi hệ thống xảy ra khi cập nhật bài đăng.");
        }
    }
}
