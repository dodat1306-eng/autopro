package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class WebAppUrlUtil {

    private WebAppUrlUtil() {
    }

    public static String toAppUrl(HttpServletRequest request, String path) {
        String contextPath = request.getContextPath();
        if (path == null || path.isBlank()) {
            return contextPath;
        }

        String normalized = path.trim();
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }
        if (normalized.equals(contextPath) || normalized.startsWith(contextPath + "/")) {
            return normalized;
        }
        if (normalized.startsWith("/")) {
            return contextPath + normalized;
        }
        return contextPath + "/" + normalized;
    }

    public static String toInternalAppUrl(HttpServletRequest request, String path) {
        if (path != null) {
            String normalized = path.trim().toLowerCase();
            if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
                return request.getContextPath();
            }
        }
        return toAppUrl(request, path);
    }

    public static void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(toAppUrl(request, path));
    }
}
