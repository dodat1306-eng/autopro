package controller;

import dao.HangXeDAO;
import dao.GiaoDichXeDAO;
import dao.TinhThanhDAO;
import dao.XeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Xe;
import util.PaginationUtil;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private void prepareFilterData(HttpServletRequest request) {
        TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();
        request.setAttribute("listTinhThanh", tinhThanhDAO.getAllTinhThanh());
        request.setAttribute("listMien", tinhThanhDAO.getAllMien());
        request.setAttribute("listHangXe", new HangXeDAO().getAllHangXe());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        List<Xe> allCars = new XeDAO().searchXe(keyword);
        int pageSize = 8;
        int currentPage = PaginationUtil.parsePage(request.getParameter("page"));
        int totalPages = PaginationUtil.totalPages(allCars.size(), pageSize);
        currentPage = Math.min(currentPage, totalPages);

        request.setAttribute("listXe", PaginationUtil.slice(allCars, currentPage, pageSize));
        request.setAttribute("totalItems", allCars.size());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("paginationBaseUrl", PaginationUtil.buildPageUrl(request, "/search"));
        request.setAttribute("keyword", keyword);
        request.setAttribute("maHang", "");
        request.setAttribute("giaRange", "");
        request.setAttribute("kieuDang", "");
        request.setAttribute("namSX", "");
        request.setAttribute("maTinh", "");
        request.setAttribute("mien", "");
        request.setAttribute("nhienLieu", "");
        request.setAttribute("hopSo", "");
        request.setAttribute("tinhTrang", "");
        request.setAttribute("soCho", "");
        request.setAttribute("kmRange", "");
        request.setAttribute("xuatXu", "");
        prepareFilterData(request);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
