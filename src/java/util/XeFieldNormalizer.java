package util;

import java.text.Normalizer;
import java.util.Locale;

public final class XeFieldNormalizer {

    private XeFieldNormalizer() {
    }

    public static String normalizeTinhTrangForStorage(String value) {
        return normalizeTinhTrang(value, "Cũ");
    }

    public static String normalizeTinhTrang(String value, String fallback) {
        String normalized = compact(value);
        if (normalized.isEmpty()) {
            return fallback;
        }
        switch (normalized) {
            case "cu":
            case "used":
                return "Cũ";
            case "moi":
            case "new":
                return "Mới";
            case "luot":
            case "likenew":
                return "Lướt";
            default:
                return fallback == null ? safeTrim(value) : fallback;
        }
    }

    public static String normalizeNhienLieuForStorage(String value) {
        return normalizeNhienLieu(value, "Xăng");
    }

    public static String normalizeNhienLieu(String value, String fallback) {
        String normalized = compact(value);
        if (normalized.isEmpty()) {
            return fallback;
        }
        switch (normalized) {
            case "xang":
            case "gasoline":
            case "petrol":
                return "Xăng";
            case "dau":
            case "diesel":
                return "Dầu (Diesel)";
            case "dien":
            case "electric":
            case "ev":
                return "Điện";
            case "hybrid":
                return "Hybrid";
            default:
                return fallback == null ? safeTrim(value) : fallback;
        }
    }

    public static String normalizeHopSoForStorage(String value) {
        return normalizeHopSo(value, "Tự động");
    }

    public static String normalizeHopSo(String value, String fallback) {
        String normalized = compact(value);
        if (normalized.isEmpty()) {
            return fallback;
        }
        switch (normalized) {
            case "tudong":
            case "automatic":
            case "auto":
                return "Tự động";
            case "sosan":
            case "manual":
                return "Số sàn";
            default:
                return fallback == null ? safeTrim(value) : fallback;
        }
    }

    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private static String compact(String value) {
        String normalized = safeTrim(value);
        if (normalized == null || normalized.isEmpty()) {
            return "";
        }

        String ascii = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
        return ascii;
    }
}
