package controller;

import dao.LichHenDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.NguoiDung;

@WebServlet("/lich-hen/chi-tiet")
public class LichHenChiTietServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException,ServletException {
        NguoiDung u=(NguoiDung)req.getSession().getAttribute("user");
        if(u==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        try {
            req.setAttribute("appointment",new LichHenDAO().get(Integer.parseInt(req.getParameter("id")),u.getMaNguoiDung()));
            req.getRequestDispatcher("/appointment-detail.jsp").forward(req,resp);
        } catch(IllegalArgumentException e) { resp.sendError(404); }
        catch(Exception e) { log("Cannot read appointment",e); resp.sendError(503); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        NguoiDung u=(NguoiDung)req.getSession().getAttribute("user");
        if(u==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        int id=0;
        try {
            id=Integer.parseInt(req.getParameter("id"));
            String raw=req.getParameter("time");
            Timestamp time=raw==null || raw.isBlank()?null:Timestamp.valueOf(LocalDateTime.parse(raw));
            new LichHenDAO().act(id,u.getMaNguoiDung(),Integer.parseInt(req.getParameter("version")),req.getParameter("action"),time,req.getParameter("place"),req.getParameter("note"));
            req.getSession().setAttribute("appointmentSuccess","Đã cập nhật lịch hẹn và thông báo cho bên còn lại.");
        } catch(IllegalArgumentException | java.time.format.DateTimeParseException e) { req.getSession().setAttribute("appointmentError",e.getMessage()); }
        catch(Exception e) { log("Cannot change appointment",e); req.getSession().setAttribute("appointmentError","Chưa thể cập nhật. Vui lòng thử lại."); }
        resp.sendRedirect(req.getContextPath()+(id>0?"/lich-hen/chi-tiet?id="+id:"/lich-hen-cua-toi"));
    }
}
