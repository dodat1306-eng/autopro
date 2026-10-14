package controller;

import dao.BaiDangDAO;
import dao.BaoCaoDAO;
import dao.GiaoDichXeDAO;
import dao.NguoiDungDAO;
import dao.XeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.BaiDang;
import model.NguoiDung;
import model.Xe;

@WebServlet("/admin/post-detail")
public class AdminPostDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        NguoiDung admin = session == null ? null : (NguoiDung) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }

            int maBaiDang = Integer.parseInt(idParam);
            new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
            BaiDangDAO bdDAO = new BaiDangDAO();
            BaiDang baiDang = bdDAO.getBaiDangByIdForAdmin(maBaiDang);
            if (baiDang == null) {
                response.sendRedirect(request.getContextPath() + "/admin/posts");
                return;
            }

            Xe xe = new XeDAO().getXeById(baiDang.getMaXe());
            NguoiDung nguoiDang = new NguoiDungDAO().getUserById(baiDang.getMaNguoiDung());

            request.setAttribute("baiDang", baiDang);
            request.setAttribute("xe", xe);
            request.setAttribute("nguoiDang", nguoiDang);
            request.setAttribute("baoCaoList", new BaoCaoDAO().getReportsByPost(maBaiDang));
            request.getRequestDispatcher("/admin-post-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/posts");
        }
    }
}
