package controller;

import dao.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.NguoiDung;
import util.*;

@WebServlet({"/account-security","/verify-contact"})
public class AccountSecurityServlet extends HttpServlet {
    private Integer currentId(HttpServletRequest req) {
        NguoiDung u=(NguoiDung)req.getSession().getAttribute("user");
        return u==null?null:u.getMaNguoiDung();
    }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        Integer user=currentId(req);
        if("/account-security".equals(req.getServletPath()) && user==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        try {
            if("/verify-contact".equals(req.getServletPath())) req.setAttribute("challenge",new XacMinhLienHeDAO().get(req.getParameter("id"),VerificationFlow.sessionKey(req.getSession()),user));
            req.getRequestDispatcher("/account-security.jsp").forward(req,resp);
        } catch(Exception e) { log("Cannot read contact challenge",e); resp.sendError(404); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        Integer user=currentId(req);
        String redirect="account-security";
        try {
            String action=req.getParameter("action");
            XacMinhLienHeDAO dao=new XacMinhLienHeDAO();
            if("verify".equals(action)) {
                String id=req.getParameter("id");
                redirect="verify-contact?id="+java.net.URLEncoder.encode(id==null?"":id,java.nio.charset.StandardCharsets.UTF_8);
                dao.verify(id,VerificationFlow.sessionKey(req.getSession()),user,req.getParameter("otp"));
                if(user==null) redirect="login?registered=1";
                else {
                    req.getSession().setAttribute("user",new NguoiDungDAO().getUserById(user));
                    redirect="account-security";
                }
                req.getSession().setAttribute("securitySuccess","Xác minh thành công.");
            } else {
                if(user==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
                NguoiDung fresh=new NguoiDungDAO().getUserById(user);
                if(fresh==null || !PasswordUtil.verifyPassword(req.getParameter("currentPassword"),fresh.getMatKhau())) throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
                if("contact".equals(action)) {
                    String id=VerificationFlow.start(req.getSession(),user,"LIEN_HE",req.getParameter("type"),req.getParameter("contact"),null,null);
                    redirect="verify-contact?id="+id;
                } else if("password".equals(action)) {
                    String password=req.getParameter("newPassword");
                    if(password==null || !password.equals(req.getParameter("confirmPassword"))) throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
                    dao.changePassword(user,req.getParameter("currentPassword"),password);
                    req.getSession().invalidate();
                    redirect="login";
                } else throw new IllegalArgumentException("Thao tác không hợp lệ.");
            }
        } catch(IllegalArgumentException e) { req.getSession().setAttribute("securityError",e.getMessage()); }
        catch(Exception e) { log("Account verification failed",e); req.getSession().setAttribute("securityError","Không thể xử lý lúc này. Vui lòng thử lại."); }
        resp.sendRedirect(req.getContextPath()+"/"+redirect);
    }
}
