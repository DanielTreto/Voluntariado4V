package cuatrovientos.voluntariado.utils;

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.model.VolunteerActivity;
import cuatrovientos.voluntariado.network.model.ApiActivity;
import cuatrovientos.voluntariado.network.model.ApiVolunteer;

public class ActivityMapper {

    public static VolunteerActivity mapApiToModel(ApiActivity apiAct) {
        if (apiAct == null) return null;

        // 1. Status Mapping
        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
        String status = "Active"; // Default

        if (rawStatus.equalsIgnoreCase("ACTIVO")) status = "Active";
        else if (rawStatus.equalsIgnoreCase("PENDIENTE")) status = "Pending";
        else if (rawStatus.equalsIgnoreCase("SUSPENDIDO")) status = "Suspended";
        else if (rawStatus.equalsIgnoreCase("FINALIZADA")) status = "Finished";
        else if (rawStatus.equalsIgnoreCase("EN_PROGRESO")) status = "InProgress";
        else status = rawStatus; // Fallback

        // 2. Color Mapping
        int color = getColorForType(apiAct.getType());

        // 3. Image URL Normalization
        String imageUrl = apiAct.getImagen();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = "http://10.0.2.2:8000" + imageUrl;
        }

        // 4. Participants Mapping
        List<cuatrovientos.voluntariado.model.Volunteer> participants = new ArrayList<>();
        if (apiAct.getVolunteers() != null) {
            for (ApiVolunteer apiVol : apiAct.getVolunteers()) {
                String avatarUrl = apiVol.getAvatar();
                if (avatarUrl != null && !avatarUrl.startsWith("http")) {
                    avatarUrl = "http://10.0.2.2:8000/" + avatarUrl;
                }
                participants.add(new cuatrovientos.voluntariado.model.Volunteer(
                        apiVol.getId(),
                        apiVol.getName(),
                        apiVol.getSurname1(), 
                        apiVol.getSurname2(), 
                        apiVol.getEmail(),
                        apiVol.getPhone(),
                        null, // DNI
                        null, // BirthDate
                        null, // Description
                        "Voluntario", // Role
                        null, // Preferences
                        "Active", // Status (Assumed)
                        avatarUrl
                ));
            }
        }

        // 5. Organization Mapping
        String orgName = "Cuatrovientos"; // Default
        String orgAvatar = null;
        if (apiAct.getOrganization() != null) {
            orgName = apiAct.getOrganization().getName();
            String orgAvPath = apiAct.getOrganization().getAvatar();
            if (orgAvPath != null && !orgAvPath.startsWith("http")) {
                orgAvatar = "http://10.0.2.2:8000/" + orgAvPath;
            } else {
                orgAvatar = orgAvPath;
            }
        }

        // 6. Build Object
        VolunteerActivity volAct = new VolunteerActivity(
                apiAct.getTitle(),
                apiAct.getDescription() != null ? apiAct.getDescription() : "",
                apiAct.getLocation() != null ? apiAct.getLocation() : "Ubicación por definir",
                apiAct.getDate() != null ? apiAct.getDate() : "Fecha por definir",
                apiAct.getDuration() != null ? apiAct.getDuration() : "N/A",
                apiAct.getEndDate(),
                apiAct.getMaxVolunteers(),
                apiAct.getType(),
                status,
                orgName,
                orgAvatar,
                color,
                imageUrl,
                participants
        );
        volAct.setId(apiAct.getId());
        
        return volAct;
    }

    public static int getColorForType(String type) {
        if (type == null) return 0xFF9E9E9E; // Gray default
        switch (type) {
            case "Medio Ambiente": return 0xFF2E7D32; // Green
            case "Social": return 0xFF1976D2; // Blue
            case "Tecnológico": return 0xFFFFC107; // Amber
            case "Educativo": return 0xFF512DA8; // Deep Purple
            case "Deporte": return 0xFFFF9800; // Orange (Matching StudentHomeFragment original logic but standardized)
            case "Salud": return 0xFFF44336; // Red
            case "Cultura": return 0xFF9C27B0; // Purple
            default: return 0xFFEF5350; // Red default
        }
    }
}
