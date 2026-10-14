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

@WebServlet("/filter")
public class FilterServlet extends HttpServlet {

    private Integer parseInteger(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double[] parsePriceRange(String priceRange) {
        if (priceRange == null || priceRange.isBlank()) {
            return new Double[]{null, null};
        }

        if ("under-300".equals(priceRange)) {
            return new Double[]{0d, 300_000_000d};
        }
        if ("300-500".equals(priceRange)) {
            return new Double[]{300_000_000d, 500_000_000d};
        }
        if ("500-700".equals(priceRange)) {
            return new Double[]{500_000_000d, 700_000_000d};
        }
        if ("700-1000".equals(priceRange)) {
            return new Double[]{700_000_000d, 1_000_000_000d};
        }
        if ("1000-2000".equals(priceRange)) {
            return new Double[]{1_000_000_000d, 2_000_000_000d};
        }
        if ("over-2000".equals(priceRange)) {
            return new Double[]{2_000_000_000d, null};
        }
        return new Double[]{null, null};
    }

    private Integer[] parseKmRange(String kmRange) {
        if (kmRange == null || kmRange.isBlank()) {
            return new Integer[]{null, null};
        }

        if ("under-10000".equals(kmRange)) {
            return new Integer[]{0, 10_000};
        }
        if ("10000-50000".equals(kmRange)) {
            return new Integer[]{10_000, 50_000};
        }
        if ("50000-100000".equals(kmRange)) {
            return new Integer[]{50_000, 100_000};
        }
        if ("over-100000".equals(kmRange)) {
            return new Integer[]{100_000, null};
        }
        return new Integer[]{null, null};
    }

    private Integer[] parseSeatRange(String soChoRaw) {
        if (soChoRaw == null || soChoRaw.isBlank()) {
            return new Integer[]{null, null};
        }

        if ("8plus".equals(soChoRaw)) {
            return new Integer[]{8, null};
        }

        Integer exactSeat = parseInteger(soChoRaw);
        if (exactSeat == null) {
            return new Integer[]{null, null};
        }
        return new Integer[]{exactSeat, exactSeat};
    }

    private void prepareFilterData(HttpServletRequest request) {
        TinhThanhDAO tinhThanhDAO = new TinhThanhDAO();
        request.setAttribute("listTinhThanh", tinhThanhDAO.getAllTinhThanh());
        request.setAttribute("listMien", tinhThanhDAO.getAllMien());
        request.setAttribute("listHangXe", new HangXeDAO().getAllHangXe());
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = safeValue(request.getParameter("keyword"));
        String maHangRaw = safeValue(request.getParameter("maHang"));
        String giaRange = safeValue(request.getParameter("giaRange"));
        String kieuDang = safeValue(request.getParameter("kieuDang"));
        String yearRaw = safeValue(request.getParameter("namSX"));
        String maTinhRaw = safeValue(request.getParameter("maTinh"));
        String mien = safeValue(request.getParameter("mien"));
        String nhienLieu = safeValue(request.getParameter("nhienLieu"));
        String hopSo = safeValue(request.getParameter("hopSo"));
        String tinhTrang = safeValue(request.getParameter("tinhTrang"));
        String soChoRaw = safeValue(request.getParameter("soCho"));
        String kmRange = safeValue(request.getParameter("kmRange"));
        String xuatXu = safeValue(request.getParameter("xuatXu"));

        Integer maHang = parseInteger(maHangRaw);
        Integer namSX = parseInteger(yearRaw);
        Integer maTinh = parseInteger(maTinhRaw);
        Double[] priceRange = parsePriceRange(giaRange);
        Integer[] kmValues = parseKmRange(kmRange);
        Integer[] seatValues = parseSeatRange(soChoRaw);

        new GiaoDichXeDAO().syncCompletedCarsToSoldPostsOnce();
        List<Xe> allCars = new XeDAO().filterXe(
                keyword,
                maHang,
                priceRange[0],
                priceRange[1],
                namSX,
                kieuDang,
                maTinh,
                mien,
                nhienLieu,
                hopSo,
                tinhTrang,
                seatValues[0],
                seatValues[1],
                kmValues[0],
                kmValues[1],
                xuatXu
        );
        int pageSize = 8;
        int currentPage = PaginationUtil.parsePage(request.getParameter("page"));
        int totalPages = PaginationUtil.totalPages(allCars.size(), pageSize);
        currentPage = Math.min(currentPage, totalPages);

        request.setAttribute("listXe", PaginationUtil.slice(allCars, currentPage, pageSize));
        request.setAttribute("totalItems", allCars.size());
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("paginationBaseUrl", PaginationUtil.buildPageUrl(request, "/filter"));
        request.setAttribute("keyword", keyword);
        request.setAttribute("maHang", maHangRaw);
        request.setAttribute("giaRange", giaRange);
        request.setAttribute("kieuDang", kieuDang);
        request.setAttribute("namSX", yearRaw);
        request.setAttribute("maTinh", maTinhRaw);
        request.setAttribute("mien", mien);
        request.setAttribute("nhienLieu", nhienLieu);
        request.setAttribute("hopSo", hopSo);
        request.setAttribute("tinhTrang", tinhTrang);
        request.setAttribute("soCho", soChoRaw);
        request.setAttribute("kmRange", kmRange);
        request.setAttribute("xuatXu", xuatXu);
        prepareFilterData(request);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
