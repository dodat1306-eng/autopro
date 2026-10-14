package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.BaiDang;
import model.NguoiDung;

@WebServlet(name = "DeletePostServlet", urlPatterns = {"/delete-post"})
public class DeletePostServlet extends HttpServlet {

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String redirectUrl = request.getContextPath() + "/my-posts";
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                response.sendRedirect(redirectUrl + "?deleteError=1");
                return;
            }

            int maBaiDang = Integer.parseInt(idParam);
            BaiDangDAO baiDangDAO = new BaiDangDAO();
            BaiDang baiDang = baiDangDAO.getBaiDangById(maBaiDang, user.getMaNguoiDung());
            if (baiDang == null) {
                response.sendRedirect(redirectUrl + "?deleteError=1");
                return;
            }
            GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
            if (baiDang.isBiKhoaGiaoDich()
                    || giaoDichXeDAO.hasCompletedTransactionForCar(baiDang.getMaXe())
                    || giaoDichXeDAO.hasActiveTransactionForCar(baiDang.getMaXe())) {
                response.sendRedirect(redirectUrl + "?soldLocked=1");
                return;
            }

            boolean deleted = baiDangDAO.deleteBaiDang(maBaiDang, user.getMaNguoiDung());
            response.sendRedirect(redirectUrl + (deleted ? "?deleted=1" : "?deleteError=1"));
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "?deleteError=1");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/my-posts");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleDelete(request, response);
    }
}
