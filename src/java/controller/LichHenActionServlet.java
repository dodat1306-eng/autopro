package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/lich-hen/action")
public class LichHenActionServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        if(req.getSession().getAttribute("user")==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        try {
            int id=Integer.parseInt(req.getParameter("maYeuCau"));
            req.getSession().setAttribute("appointmentError","Vui lòng kiểm tra thời gian mới nhất và thao tác tại chi tiết lịch hẹn.");
            resp.sendRedirect(req.getContextPath()+"/lich-hen/chi-tiet?id="+id);
        } catch(NumberFormatException e) { resp.sendError(400); }
    }
}
