package controller;

import dao.YeuCauKetNoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import model.NguoiDung;
import model.YeuCauKetNoi;

@WebServlet("/admin/lich-hen/cap-nhat")
public class AdminCapNhatLichHenServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "CHO_LIEN_HE",
            "DA_LIEN_HE",
            "DA_XEM_XE",
            "DA_CHOT",
            "HOAN_TAT",
            "DA_HUY"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
            return;
        }

        try {
            int maYeuCau = Integer.parseInt(idParam);
            YeuCauKetNoi yeuCauKetNoi = new YeuCauKetNoiDAO().getYeuCauKetNoiById(maYeuCau);
            if (yeuCauKetNoi == null) {
                response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
                return;
            }

            request.setAttribute("yeuCauKetNoi", yeuCauKetNoi);
            request.getRequestDispatcher("/admin_edit_appointment.jsp").forward(request, response);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null || !"admin".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("maYeuCau");
        String trangThaiMoi = request.getParameter("trangThai");

        if (idParam == null || idParam.isBlank() || trangThaiMoi == null || !ALLOWED_STATUSES.contains(trangThaiMoi)) {
            response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
            return;
        }

        try {
            int maYeuCau = Integer.parseInt(idParam);
            // Appointment attendance and transaction completion belong to the actual participants.
            if (!"DA_HUY".equals(trangThaiMoi)
                    || new dao.GiaoDichXeDAO().getByYeuCau(maYeuCau) != null) {
                response.sendError(409, "Admin chỉ được hủy lịch chưa có giao dịch; kết quả xem xe do hai bên xác nhận.");
                return;
            }
            boolean success = new YeuCauKetNoiDAO().updateTrangThaiYeuCau(maYeuCau, trangThaiMoi);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
                return;
            }
        } catch (NumberFormatException ex) {
            // Fall through to redirect below.
        }

        response.sendRedirect(request.getContextPath() + "/admin/lich-hen");
    }
}
