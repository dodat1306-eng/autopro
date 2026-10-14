package controller;

import dao.BaiDangDAO;
import dao.DangKySellerDAO;
import dao.GiaoDichXeDAO;
import dao.NguoiDungDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.BaiDang;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet(name = "MyPostServlet", urlPatterns = {"/my-posts"})
public class MyPostServlet extends HttpServlet {

    private NguoiDung refreshCurrentUser(HttpServletRequest request) {
        NguoiDung sessionUser = (NguoiDung) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return null;
        }

        NguoiDung freshUser = new NguoiDungDAO().getUserById(sessionUser.getMaNguoiDung());
        if (freshUser == null) {
            request.getSession().removeAttribute("user");
            return null;
        }

        request.getSession().setAttribute("user", freshUser);
        return freshUser;
    }

    private boolean ensureSellerAccess(HttpServletRequest request, HttpServletResponse response, NguoiDung user)
            throws IOException {
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return false;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/posts");
            return false;
        }
        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        if (user.isSeller()) {
            dangKySellerDAO.ensureApprovedSellerProfile(
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getSoDienThoai()
            );
            return true;
        }

        if (dangKySellerDAO.hasApprovedSellerRole(user.getMaNguoiDung())
                && dangKySellerDAO.updateUserToSellerIfApproved(user.getMaNguoiDung())) {
            NguoiDung refreshedUser = refreshCurrentUser(request);
            if (refreshedUser != null && refreshedUser.isSeller()) {
                return true;
            }
        }

        WebAppUrlUtil.redirect(request, response, "seller-register?needSeller=1");
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = refreshCurrentUser(request);
        if (!ensureSellerAccess(request, response, user)) {
            return;
        }

        BaiDangDAO dao = new BaiDangDAO();
        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        List<BaiDang> rawPosts = dao.getBaiDangByUser(user.getMaNguoiDung());
        List<BaiDang> activePosts = new ArrayList<>();
        List<BaiDang> soldPosts = new ArrayList<>();
        for (BaiDang post : rawPosts) {
            if (post != null && "da_ban".equalsIgnoreCase(post.getTrangThai())) {
                soldPosts.add(post);
            } else {
                activePosts.add(post);
            }
        }

        List<BaiDang> listBaiDang = new ArrayList<>(activePosts);
        listBaiDang.addAll(soldPosts);

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "my-posts");

        request.setAttribute("listBaiDang", listBaiDang);
        request.setAttribute("activePostCount", activePosts.size());
        request.setAttribute("soldPostCount", soldPosts.size());
        request.getRequestDispatcher("my-posts.jsp").forward(request, response);
    }
}
