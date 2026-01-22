package cuatrovientos.voluntariado.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SearchUtils {

    public static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.getDefault());
    }

    public static boolean matches(String target, String query) {
        if (target == null || query == null) return false;
        if (query.isEmpty()) return true;

        String normTarget = normalize(target);
        String normQuery = normalize(query);

        return normTarget.startsWith(normQuery);
    }
}
