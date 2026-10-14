package filter;

import dao.NguoiDungDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import model.NguoiDung;

@WebFilter("/*")
public class RequestSafetyFilter implements Filter {
    private static final SecureRandom RANDOM=new SecureRandom();
    private static final Map<String,Window> WINDOWS=new HashMap<>();
    private static class Window { long until; int count; Window(long t) { until=t; } }
    private static synchronized boolean allowed(String key,int limit,long period) {
        long now=System.currentTimeMillis();
        WINDOWS.entrySet().removeIf(e -> e.getValue().until<=now);
        if(WINDOWS.size()>10000 && !WINDOWS.containsKey(key)) return false;
        Window w=WINDOWS.computeIfAbsent(key,k->new Window(now+period));
        return ++w.count<=limit;
    }
    @Override public void doFilter(ServletRequest input,ServletResponse output,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest req=(HttpServletRequest)input;
        HttpServletResponse resp=(HttpServletResponse)output;
        req.setCharacterEncoding("UTF-8");
        String path=req.getServletPath();
        if(path.startsWith("/assets/") || path.startsWith("/logo/") || path.startsWith("/uploads/")) { chain.doFilter(req,resp); return; }
        HttpSession session=req.getSession();
        if(session.getAttribute("csrfToken")==null) {
            byte[] token=new byte[32]; RANDOM.nextBytes(token);
            session.setAttribute("csrfToken",Base64.getUrlEncoder().withoutPadding().encodeToString(token));
        }
        if("POST".equals(req.getMethod())) {
            String supplied=req.getHeader("X-CSRF-Token");
            if(supplied==null) supplied=req.getParameter("csrfToken");
            if(!session.getAttribute("csrfToken").equals(supplied)) { resp.sendError(403,"Phiên thao tác đã hết hạn. Vui lòng tải lại trang."); return; }
            if(Set.of("/login","/register","/forgot-password","/verify-otp","/verify-contact","/account-security").contains(path)
                    && !allowed(req.getRemoteAddr()+":"+path,15,60000)) {
                resp.setHeader("Retry-After","60"); resp.sendError(429,"Vui lòng chờ một phút rồi thử lại."); return;
            }
            NguoiDung cached=(NguoiDung)session.getAttribute("user");
            if ("/forgot-password".equals(path)) {
                String target=req.getParameter("identifier");
                if (target!=null && !allowed("reset:"+target.trim().toLowerCase(Locale.ROOT),5,3600000)) {
                    resp.setHeader("Retry-After","3600"); resp.sendError(429,"Đã đạt giới hạn yêu cầu OTP. Vui lòng thử lại sau."); return;
                }
            }
            if(cached!=null) {
                NguoiDung fresh=new NguoiDungDAO().getUserById(cached.getMaNguoiDung());
                if(fresh==null || !"active".equals(fresh.getTrangThai())) { session.invalidate(); resp.sendError(403,"Tài khoản không còn hoạt động."); return; }
                session.setAttribute("user",fresh);
            }
        }
        resp.setHeader("X-Content-Type-Options","nosniff");
        resp.setHeader("Referrer-Policy","same-origin");
        chain.doFilter(req,resp);
    }
}
