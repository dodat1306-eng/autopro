package controller;

import dao.SavedPostDAO;
import dao.GiaoDichXeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.NguoiDung;

@WebServlet("/saved-posts")
public class SavedPostServlet extends HttpServlet {

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
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!user.isThanhVien()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        moveFlashMessage(request, "savedPostSuccess", "savedPostSuccess");
        moveFlashMessage(request, "savedPostError", "savedPostError");
        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        request.setAttribute("savedPosts", new SavedPostDAO().getSavedPostsByUser(user.getMaNguoiDung()));
        request.getRequestDispatcher("/saved-posts.jsp").forward(request, response);
    }
}
