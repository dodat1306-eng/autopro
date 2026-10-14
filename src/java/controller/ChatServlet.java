package controller;

import dao.BaiDangDAO;
import dao.GiaoDichXeDAO;
import dao.HoiThoaiDAO;
import dao.ThongBaoDAO;
import dao.TinNhanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        HoiThoaiDAO hoiThoaiDAO = new HoiThoaiDAO();
        String sellerParam = request.getParameter("seller");
        String carParam = request.getParameter("car");
        String roomParam = request.getParameter("room");

        if (sellerParam != null && carParam != null) {
            if (!user.isThanhVien() || "blocked".equalsIgnoreCase(user.getTrangThai())) {
                WebAppUrlUtil.redirect(request, response, "detail?id=" + carParam);
                return;
            }
            try {
                int maNguoiBan = Integer.parseInt(sellerParam);
                int maXe = Integer.parseInt(carParam);

                if (maNguoiBan == user.getMaNguoiDung()) {
                    WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                    return;
                }

                GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
                if (giaoDichXeDAO.hasCompletedTransactionForCar(maXe)
                        || giaoDichXeDAO.hasActiveTransactionForCar(maXe)) {
                    request.getSession().setAttribute("detailError", "Xe này đang được giao dịch hoặc đã bán nên không thể mở cuộc trò chuyện mới từ bài đăng.");
                    WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                    return;
                }

                Integer approvedSeller = new BaiDangDAO().getApprovedSellerIdByMaXe(maXe);
                if (approvedSeller == null || !approvedSeller.equals(maNguoiBan)) {
                    WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                    return;
                }

                int maHoiThoai = hoiThoaiDAO.getOrCreateHoiThoai(user.getMaNguoiDung(), maNguoiBan, maXe);
                if (maHoiThoai <= 0) {
                    WebAppUrlUtil.redirect(request, response, "detail?id=" + maXe);
                    return;
                }

                WebAppUrlUtil.redirect(request, response, "chat?room=" + maHoiThoai);
                return;
            } catch (NumberFormatException e) {
                WebAppUrlUtil.redirect(request, response, "home");
                return;
            }
        }

        request.setAttribute("messages", Collections.emptyList());
        if (roomParam != null) {
            try {
                int maHoiThoai = Integer.parseInt(roomParam);
                if (!hoiThoaiDAO.isParticipant(maHoiThoai, user.getMaNguoiDung())) {
                    WebAppUrlUtil.redirect(request, response, "chat-list");
                    return;
                }

                new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "chat?room=" + maHoiThoai);

                request.setAttribute("maHoiThoai", maHoiThoai);
                request.setAttribute("chatInfo", hoiThoaiDAO.getThongTinHoiThoai(maHoiThoai, user.getMaNguoiDung()));
                request.setAttribute("messages", new TinNhanDAO().getTinNhanByHoiThoai(maHoiThoai));
            } catch (NumberFormatException e) {
                WebAppUrlUtil.redirect(request, response, "chat-list");
                return;
            }
        }

        request.getRequestDispatcher("chat.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        HoiThoaiDAO hoiThoaiDAO = new HoiThoaiDAO();
        try {
            int maHoiThoai = Integer.parseInt(request.getParameter("maHoiThoai"));
            if (!hoiThoaiDAO.isParticipant(maHoiThoai, user.getMaNguoiDung())) {
                WebAppUrlUtil.redirect(request, response, "chat-list");
                return;
            }

            String noiDung = request.getParameter("noiDung");
            if (noiDung != null && !noiDung.trim().isEmpty()) {
                if (new TinNhanDAO().sendTinNhan(maHoiThoai, user.getMaNguoiDung(), noiDung.trim())) {
                    Map<String, Object> chatInfo = hoiThoaiDAO.getThongTinHoiThoai(maHoiThoai, user.getMaNguoiDung());
                    if (chatInfo != null && chatInfo.get("doiTacId") != null) {
                        int doiTacId = (Integer) chatInfo.get("doiTacId");
                        String tenXe = chatInfo.get("tenXe") == null ? "xe đang trao đổi" : chatInfo.get("tenXe").toString();
                        new ThongBaoDAO().createNotification(
                                doiTacId,
                                "MESSAGE",
                                "Tin nhắn mới từ " + user.getHoTen(),
                                "Bạn có tin nhắn mới về " + tenXe + ".",
                                "chat?room=" + maHoiThoai
                        );
                    }
                }
            }

            WebAppUrlUtil.redirect(request, response, "chat?room=" + maHoiThoai);
        } catch (NumberFormatException e) {
            WebAppUrlUtil.redirect(request, response, "chat-list");
        }
    }
}
