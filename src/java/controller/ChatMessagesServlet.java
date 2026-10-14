package controller;

import com.google.gson.Gson;
import dao.HoiThoaiDAO;
import dao.TinNhanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.NguoiDung;
import model.TinNhan;

@WebServlet("/chat-messages")
public class ChatMessagesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        String maHoiThoaiStr = request.getParameter("maHoiThoai");
        if (maHoiThoaiStr == null) {
            response.getWriter().write("[]");
            return;
        }

        try {
            int maHoiThoai = Integer.parseInt(maHoiThoaiStr);
            if (!new HoiThoaiDAO().isParticipant(maHoiThoai, user.getMaNguoiDung())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("[]");
                return;
            }

            List<TinNhan> messages = new TinNhanDAO().getTinNhanByHoiThoai(maHoiThoai);
            String json = new Gson().toJson(messages);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (Exception e) {
            response.getWriter().write("[]");
        }
    }
}
