package controller;

import dao.GiaoDichXeDAO;
import dao.ThongBaoDAO;
import dao.YeuCauKetNoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.GiaoDichXe;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet({"/lich-hen-cua-toi", "/lich-hen/lich-hen-cua-toi", "/my-orders"})
public class LichHenCuaToiServlet extends HttpServlet {

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

    private Map<Integer, GiaoDichXe> toTransactionMap(List<GiaoDichXe> danhSach) {
        Map<Integer, GiaoDichXe> map = new LinkedHashMap<>();
        if (danhSach == null) {
            return map;
        }
        for (GiaoDichXe item : danhSach) {
            map.put(item.getMaYeuCau(), item);
        }
        return map;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/lich-hen");
            return;
        }

        YeuCauKetNoiDAO yeuCauKetNoiDAO = new YeuCauKetNoiDAO();
        GiaoDichXeDAO giaoDichXeDAO = new GiaoDichXeDAO();
        moveFlashMessage(request, "requestSuccess", "requestSuccess");
        moveFlashMessage(request, "requestError", "requestError");
        moveFlashMessage(request, "transactionSuccess", "transactionSuccess");
        moveFlashMessage(request, "transactionError", "transactionError");

        new ThongBaoDAO().markByUrlAsRead(user.getMaNguoiDung(), "lich-hen-cua-toi");

        List<GiaoDichXe> outgoingTransactions = giaoDichXeDAO.getByBuyer(user.getMaNguoiDung());
        List<GiaoDichXe> incomingTransactions = giaoDichXeDAO.getBySeller(user.getMaNguoiDung());

        request.setAttribute("outgoingRequests", yeuCauKetNoiDAO.getYeuCauDaGuiByNguoiMua(user.getMaNguoiDung()));
        request.setAttribute("incomingRequests", yeuCauKetNoiDAO.getYeuCauNhanDuocByNguoiBan(user.getMaNguoiDung()));
        request.setAttribute("outgoingTransactions", outgoingTransactions);
        request.setAttribute("incomingTransactions", incomingTransactions);
        request.setAttribute("outgoingTransactionMap", toTransactionMap(outgoingTransactions));
        request.setAttribute("incomingTransactionMap", toTransactionMap(incomingTransactions));
        request.getRequestDispatcher("my-appointments.jsp").forward(request, response);
    }
}
