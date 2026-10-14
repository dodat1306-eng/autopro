package util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static int parsePage(String rawPage) {
        try {
            int page = Integer.parseInt(rawPage == null ? "" : rawPage.trim());
            return Math.max(page, 1);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    public static <T> List<T> slice(List<T> items, int page, int pageSize) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        int safePageSize = Math.max(pageSize, 1);
        int totalPages = totalPages(items.size(), safePageSize);
        int safePage = Math.min(Math.max(page, 1), totalPages);
        int fromIndex = Math.min((safePage - 1) * safePageSize, items.size());
        int toIndex = Math.min(fromIndex + safePageSize, items.size());
        return new ArrayList<>(items.subList(fromIndex, toIndex));
    }

    public static int totalPages(int totalItems, int pageSize) {
        if (totalItems <= 0) {
            return 1;
        }
        return (int) Math.ceil(totalItems / (double) Math.max(pageSize, 1));
    }

    public static String buildPageUrl(HttpServletRequest request, String path) {
        StringBuilder builder = new StringBuilder(request.getContextPath()).append(path);
        List<String> params = new ArrayList<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if ("page".equals(name)) {
                continue;
            }
            String[] values = request.getParameterValues(name);
            if (values == null) {
                continue;
            }
            for (String value : values) {
                if (value == null || value.isBlank()) {
                    continue;
                }
                params.add(encode(name) + "=" + encode(value));
            }
        }
        if (!params.isEmpty()) {
            builder.append("?").append(String.join("&", params)).append("&page=");
        } else {
            builder.append("?page=");
        }
        return builder.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
