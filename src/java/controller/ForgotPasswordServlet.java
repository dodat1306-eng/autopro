package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import model.NguoiDung;
import util.OtpDeliveryService;
import util.WebAppUrlUtil;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SESSION_RESET_IDENTIFIER = "resetIdentifier";
    private static final String SESSION_RESET_VERIFIED_USER_ID = "resetVerifiedUserId";
    private static final String SESSION_RESET_OTP_MESSAGE = "resetOtpMessage";
    private static final String SESSION_RESET_OTP_DEBUG = "resetOtpDebug";

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String generateOtp() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identifier = trimToEmpty(request.getParameter("identifier"));
        if (identifier.isEmpty()) {
            Object sessionValue = request.getSession().getAttribute(SESSION_RESET_IDENTIFIER);
            identifier = sessionValue == null ? "" : String.valueOf(sessionValue).trim();
        }
        request.setAttribute("identifier", identifier);
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = trimToEmpty(request.getParameter("action"));
        String identifier = trimToEmpty(request.getParameter("identifier"));
        NguoiDungDAO dao = new NguoiDungDAO();

        if (!"request-otp".equals(action)) {
            WebAppUrlUtil.redirect(request, response, "forgot-password");
            return;
        }

        if (identifier.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email hoặc số điện thoại.");
            forward(request, response);
            return;
        }

        NguoiDung user = dao.findByIdentifier(identifier);
        if (user == null) {
            request.setAttribute("error", "Không tìm thấy tài khoản với thông tin bạn vừa nhập.");
            request.setAttribute("identifier", identifier);
            forward(request, response);
            return;
        }

        String otp = generateOtp();
        request.getSession().removeAttribute("resetOtpAttempts");
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000L);
        if (!dao.saveResetOtp(user.getMaNguoiDung(), otp, expiry)) {
            request.setAttribute("error", "Không thể tạo mã OTP lúc này. Vui lòng thử lại.");
            request.setAttribute("identifier", identifier);
            forward(request, response);
            return;
        }

        OtpDeliveryService.DeliveryResult deliveryResult = new OtpDeliveryService().sendOtp(user, otp, identifier);
        request.getSession().setAttribute(SESSION_RESET_IDENTIFIER, identifier);
        request.getSession().removeAttribute(SESSION_RESET_VERIFIED_USER_ID);
        request.getSession().setAttribute(SESSION_RESET_OTP_MESSAGE, deliveryResult.getUserMessage());
        if (deliveryResult.getDebugMessage() == null) {
            request.getSession().removeAttribute(SESSION_RESET_OTP_DEBUG);
        } else {
            request.getSession().setAttribute(SESSION_RESET_OTP_DEBUG, deliveryResult.getDebugMessage());
        }
        WebAppUrlUtil.redirect(request, response, "verify-otp");
    }
}
