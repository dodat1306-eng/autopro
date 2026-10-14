package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import dao.NguoiDungDAO;
import dao.SavedPostDAO;
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
import util.WebAppUrlUtil;

@WebServlet("/detail")
public class CarDetailServlet extends HttpServlet {

    private void moveFlashMessage(HttpServletRequest request, String sessionKey, String requestKey) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        Object message = session.getAttribute(sessionKey);
        if (message != null) {
            request.setAttribute(requestKey, message);
            session.removeAttribute(sessionKey);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        moveFlashMessage(request, "detailError", "detailError");
        moveFlashMessage(request, "detailSuccess", "detailSuccess");

        String idRaw = request.getParameter("id");
        if (idRaw == null) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw);
            new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
            Xe xe = new XeDAO().getPublicXeById(id);
            BaiDang baiDang = null;
            NguoiDung currentUser = (NguoiDung) request.getSession().getAttribute("user");

            if (xe != null) {
                BaiDangDAO baiDangDAO = new BaiDangDAO();
                baiDang = baiDangDAO.getLatestPublicBaiDangByMaXe(id);
                Integer maNguoiBan = baiDangDAO.getPublicSellerIdByMaXe(id);

                if (maNguoiBan != null) {
                    xe.setMaNguoiBan(maNguoiBan);
                    NguoiDung seller = new NguoiDungDAO().getUserById(maNguoiBan);
                    request.setAttribute("seller", seller);

                    boolean isOwnPost = currentUser != null && currentUser.getMaNguoiDung() == maNguoiBan;
                    request.setAttribute("isOwnPost", isOwnPost);

                    boolean shouldCountView = baiDang != null
                            && !isOwnPost
                            && !xe.isDaBan()
                            && (currentUser == null || !currentUser.isAdmin());
                    if (shouldCountView) {
                        baiDangDAO.incrementLuotXem(baiDang.getMaBaiDang());
                        baiDang.setLuotXem(baiDang.getLuotXem() + 1);
                    }
                }

                if (currentUser != null && baiDang != null) {
                    boolean isSavedPost = new SavedPostDAO().isSaved(currentUser.getMaNguoiDung(), baiDang.getMaBaiDang());
                    request.setAttribute("isSavedPost", isSavedPost);
                }
            }

            request.setAttribute("xe", xe);
            request.setAttribute("baiDang", baiDang);
            request.getRequestDispatcher("detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            WebAppUrlUtil.redirect(request, response, "home");
        }
    }
}
