package controller;

import dao.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.NguoiDung;
import util.PrivateFiles;

@WebServlet("/giao-dich/xu-ly")
@MultipartConfig(maxFileSize=5*1024*1024,maxRequestSize=6*1024*1024)
public class GiaoDichWorkflowServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        NguoiDung user=(NguoiDung)req.getSession().getAttribute("user");
        if(user==null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        int id=0;
        String uploaded=null;
        try {
            id=Integer.parseInt(req.getParameter("maGiaoDich"));
            String action=req.getParameter("action");
            if("accept".equals(action)) new GiaoDichWorkflowDAO().accept(id,user.getMaNguoiDung());
            else if("dispute".equals(action)) {
                model.GiaoDichXe g=new GiaoDichXeDAO().getById(id);
                if(g==null || (g.getMaNguoiMua()!=user.getMaNguoiDung() && g.getMaNguoiBan()!=user.getMaNguoiDung())) throw new IllegalArgumentException("Không có quyền thao tác giao dịch.");
                uploaded=PrivateFiles.save(req.getPart("evidence"));
                new GiaoDichWorkflowDAO().dispute(id,user.getMaNguoiDung(),req.getParameter("reason"),uploaded);
                uploaded=null;
            } else throw new IllegalArgumentException("Thao tác không hợp lệ.");
            req.getSession().setAttribute("transactionSuccess","Đã ghi nhận và gửi thông báo tới các bên.");
        } catch(IllegalArgumentException e) { req.getSession().setAttribute("transactionError",e.getMessage()); }
        catch(Exception e) { log("Transaction workflow failed",e); req.getSession().setAttribute("transactionError","Không thể xử lý lúc này. Vui lòng thử lại."); }
        finally { if(uploaded!=null) PrivateFiles.removeNew(uploaded); }
        resp.sendRedirect(req.getContextPath()+(id>0?"/giao-dich/chi-tiet?id="+id:"/lich-hen-cua-toi"));
    }
}
