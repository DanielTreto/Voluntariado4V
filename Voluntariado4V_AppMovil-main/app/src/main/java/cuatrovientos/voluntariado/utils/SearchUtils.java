package cuatrovientos.voluntariado.utils;

import java.text.Normalizer;
import java.util.Locale;

public class SearchUtils {

    /**
     * Normaliza una cadena eliminando acentos y convirtiéndola a minúsculas.
     */
    public static String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove accents (diacritical marks)
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.getDefault());
    }

    /**
     * Comprueba si la cadena objetivo normalizada comienza con la cadena de consulta normalizada.
     * @param target La cadena completa a comprobar (ej. título de actividad).
     * @param query La consulta de búsqueda introducida por el usuario.
     * @return true si el objetivo comienza con la consulta (ignorando mayúsculas y acentos), false en caso contrario.
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
