package cuatrovientos.voluntariado.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SearchUtils {

    /**
     * Normalizes a string by removing accents and converting to lowercase.
     */
    public static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove accents (diacritical marks)
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.getDefault());
    }

    /**
     * Checks if the normalized target string starts with the normalized query string.
     * @param target The full string to check (e.g., an activity title).
     * @param query The search query entered by the user.
     * @return true if target starts with query (ignoring case and accents), false otherwise.
     */
    public static boolean matches(String target, String query) {
        if (target == null || query == null) return false;
        // If query is empty, it usually matches everything in typical search logic, 
        // but here we assume the caller handles empty query if they want to show all.
        // However, strictly adhering to "starts with empty string" -> true.
        if (query.isEmpty()) return true;

        String normTarget = normalize(target);
        String normQuery = normalize(query);

        return normTarget.startsWith(normQuery);
    }
}
