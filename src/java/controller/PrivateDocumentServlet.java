package controller;

import context.DBContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.util.Map;
import model.NguoiDung;
import util.PrivateFiles;
import static util.DbWork.*;

@WebServlet("/private-document")
public class PrivateDocumentServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        NguoiDung user=(NguoiDung)req.getSession().getAttribute("user");
        if(user==null) { resp.sendError(401); return; }
        try(Connection c=new DBContext().getConnection()) {
            Map<String,Object> current=one(c,"SELECT vaiTro,trangThai FROM nguoidung WHERE maNguoiDung=?",user.getMaNguoiDung());
            require("active".equals(current.get("trangThai")),"Tài khoản bị khóa.");
            boolean admin="admin".equals(current.get("vaiTro"));
            int id=Integer.parseInt(req.getParameter("id"));
            String key;
            if("seller".equals(req.getParameter("type"))) {
                Map<String,Object> d=one(c,"SELECT * FROM dangkyseller WHERE maDangKySeller=?",id);
                require(admin || id(d,"maNguoiDung")==user.getMaNguoiDung(),"Không có quyền xem ảnh.");
                require(!"DA_XOA".equals(d.get("trangThai")),"Hồ sơ đã xóa.");
                require("front".equals(req.getParameter("side")) || "back".equals(req.getParameter("side")),"Mặt ảnh không hợp lệ.");
                key=(String)d.get("front".equals(req.getParameter("side"))?"anhCCCDMatTruoc":"anhCCCDMatSau");
            } else {
                Map<String,Object> d=one(c,"SELECT n.tepDinhKem,g.maNguoiMua,g.maNguoiBan FROM nhatkyhoatdong n JOIN giaodichxe g ON g.maGiaoDich=n.maDoiTuong WHERE n.maNhatKy=? AND n.doiTuong='GIAO_DICH'",id);
                require(admin || id(d,"maNguoiMua")==user.getMaNguoiDung() || id(d,"maNguoiBan")==user.getMaNguoiDung(),"Không có quyền xem bằng chứng.");
                key=(String)d.get("tepDinhKem");
            }
            Path path=PrivateFiles.resolve(key,getServletContext());
            resp.setHeader("Cache-Control","private, no-store");
            resp.setHeader("X-Content-Type-Options","nosniff");
            String mime=Files.probeContentType(path);
            if(mime==null || !mime.startsWith("image/")) { resp.sendError(404); return; }
            resp.setContentType(mime);
            Files.copy(path,resp.getOutputStream());
        } catch(IllegalArgumentException e) { resp.sendError(403); }
        catch(FileNotFoundException e) { resp.sendError(404); }
        catch(Exception e) { log("Cannot read private document",e); resp.sendError(404); }
    }
}
