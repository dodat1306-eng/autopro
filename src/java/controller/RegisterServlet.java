package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^[0-9+]{9,15}$");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String hoTen = trimToEmpty(request.getParameter("hoTen"));
        String contactType = trimToEmpty(request.getParameter("contactType"));
        String email = trimToEmpty(request.getParameter("email"));
        String soDienThoai = trimToEmpty(request.getParameter("soDienThoai"));
        String matKhau = trimToEmpty(request.getParameter("matKhau"));
        String confirmMatKhau = trimToEmpty(request.getParameter("confirmMatKhau"));

        if (hoTen.isEmpty() || matKhau.isEmpty() || confirmMatKhau.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin bắt buộc.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (!matKhau.equals(confirmMatKhau)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (matKhau.length() < 8 || matKhau.length() > 128) {
            request.setAttribute("error", "Mật khẩu cần từ 8 đến 128 ký tự.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        NguoiDungDAO dao = new NguoiDungDAO();
        if ("phone".equals(contactType)) {
            if (!isValidPhone(soDienThoai)) {
                request.setAttribute("error", "Số điện thoại không hợp lệ.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            if (dao.checkPhoneExists(soDienThoai)) {
                request.setAttribute("error", "Số điện thoại đã tồn tại.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            email = "";
        } else {
            contactType = "email";
            if (!isValidEmail(email)) {
                request.setAttribute("error", "Email không hợp lệ.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            if (dao.checkEmailExists(email)) {
                request.setAttribute("error", "Email đã tồn tại.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            soDienThoai = "";
        }

        try {
            String type = "phone".equals(contactType) ? "phone" : "email";
            String challenge = util.VerificationFlow.start(request.getSession(), null, "DANG_KY", type,
                    "phone".equals(type) ? soDienThoai : email, hoTen, util.PasswordUtil.hashPassword(matKhau));
            WebAppUrlUtil.redirect(request, response, "verify-contact?id=" + challenge);
        } catch (Exception e) {
            log("Cannot start registration verification", e);
            request.setAttribute("error", e instanceof IllegalArgumentException ? e.getMessage() : "Chưa thể gửi mã xác minh. Vui lòng thử lại.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}
