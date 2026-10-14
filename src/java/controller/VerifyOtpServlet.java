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

@WebServlet("/verify-otp")
public class VerifyOtpServlet extends HttpServlet {

    private static final String SESSION_RESET_IDENTIFIER = "resetIdentifier";
    private static final String SESSION_RESET_VERIFIED_USER_ID = "resetVerifiedUserId";
    private static final String SESSION_RESET_OTP_MESSAGE = "resetOtpMessage";
    private static final String SESSION_RESET_OTP_DEBUG = "resetOtpDebug";

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String getSessionIdentifier(HttpServletRequest request) {
        Object value = request.getSession().getAttribute(SESSION_RESET_IDENTIFIER);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identifier = getSessionIdentifier(request);
        if (identifier.isEmpty()) {
            WebAppUrlUtil.redirect(request, response, "forgot-password");
            return;
        }

        request.setAttribute("identifier", identifier);
        request.setAttribute("info", request.getSession().getAttribute(SESSION_RESET_OTP_MESSAGE));
        request.setAttribute("debugOtp", request.getSession().getAttribute(SESSION_RESET_OTP_DEBUG));
        request.getSession().removeAttribute(SESSION_RESET_OTP_MESSAGE);
        request.getSession().removeAttribute(SESSION_RESET_OTP_DEBUG);
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String identifier = getSessionIdentifier(request);
        if (identifier.isEmpty()) {
            WebAppUrlUtil.redirect(request, response, "forgot-password");
            return;
        }

        String otp = trimToEmpty(request.getParameter("otp"));
        request.setAttribute("identifier", identifier);

        if (otp.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã OTP.");
            forward(request, response);
            return;
        }

        Integer attempts = (Integer) request.getSession().getAttribute("resetOtpAttempts");
        if (attempts != null && attempts >= 5) {
            request.setAttribute("error", "Bạn đã nhập sai 5 lần. Vui lòng yêu cầu mã mới.");
            forward(request, response);
            return;
        }
        NguoiDung user = new NguoiDungDAO().verifyResetOtp(identifier, otp);
        if (user == null) {
            request.getSession().setAttribute("resetOtpAttempts", attempts == null ? 1 : attempts + 1);
            request.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
            forward(request, response);
            return;
        }

        request.getSession().setAttribute(SESSION_RESET_VERIFIED_USER_ID, user.getMaNguoiDung());
        new NguoiDungDAO().clearResetOtp(user.getMaNguoiDung());
        WebAppUrlUtil.redirect(request, response, "reset-password");
    }
}
