package cuatrovientos.voluntariado.utils;

import java.util.ArrayList;
import java.util.List;

import cuatrovientos.voluntariado.model.VolunteerActivity;
import cuatrovientos.voluntariado.network.model.ApiActivity;
import cuatrovientos.voluntariado.network.model.ApiVolunteer;

public class ActivityMapper {

    public static VolunteerActivity mapApiToModel(ApiActivity apiAct) {
        if (apiAct == null) return null;

        // 1. Mapeo de Estado
        String rawStatus = apiAct.getStatus() != null ? apiAct.getStatus() : "ACTIVO";
        String status = "Active"; // Por defecto

        if (rawStatus.equalsIgnoreCase("ACTIVO")) status = "Active";
        else if (rawStatus.equalsIgnoreCase("PENDIENTE")) status = "Pending";
        else if (rawStatus.equalsIgnoreCase("SUSPENDIDO") || rawStatus.equalsIgnoreCase("SUSPENDIDA")) status = "Suspended";
        else if (rawStatus.equalsIgnoreCase("FINALIZADA")) status = "Finished";
        else if (rawStatus.equalsIgnoreCase("EN_PROGRESO")) status = "InProgress";
        else status = rawStatus; // Caso por defecto

        // 2. Mapeo de Color
        int color = getColorForType(apiAct.getType());

        // 3. Normalización de URL de Imagen
        String imageUrl = apiAct.getImagen();
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = "http://10.0.2.2:8000" + imageUrl;
        }

        // 4. Mapeo de Participantes
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
                        null, // FechaNac
                        null, // Descripción
                        "Voluntario", // Rol
                        null, // Preferencias
                        "Active", // Estado (Asumido)
                        avatarUrl,
                        apiVol.getCourse(), // Curso
                        null // Disponibilidad (No necesaria para lista de participantes)
                ));
            }
        }

        // 5. Mapeo de Organización
        String orgName = "Cuatrovientos"; // Por defecto
        String orgAvatar = null;
        String orgId = null;
        if (apiAct.getOrganization() != null) {
            orgName = apiAct.getOrganization().getName();
            orgId = apiAct.getOrganization().getId();
            String orgAvPath = apiAct.getOrganization().getAvatar();
            if (orgAvPath != null && !orgAvPath.startsWith("http")) {
                orgAvatar = "http://10.0.2.2:8000/" + orgAvPath;
            } else {
                orgAvatar = orgAvPath;
            }
        }

        // 6. Mapeo de ODS
        List<cuatrovientos.voluntariado.model.Ods> odsList = new ArrayList<>();
        if (apiAct.getOds() != null) {
            for (cuatrovientos.voluntariado.network.model.ApiOds apiOds : apiAct.getOds()) {
                odsList.add(new cuatrovientos.voluntariado.model.Ods(apiOds.getId(), apiOds.getDescription()));
            }
        }

        // 7. Construir Objeto
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
                participants,
                odsList
        );
        volAct.setId(apiAct.getId());
        volAct.setOrganizationId(orgId);
        
        return volAct;
    }

    public static int getColorForType(String type) {
        if (type == null) return 0xFF616161; // Gris Oscuro por defecto
        switch (type) {
            case "Ambiental": return 0xFF2E7D32; // Verde
            case "Social": return 0xFF1976D2; // Azul
            case "Digital": return 0xFFFFC107; // Ámbar
            case "Educativo": return 0xFF512DA8; // Púrpura Profundo
            case "Deportivo": return 0xFFFF9800; // Naranja
            case "Salud": return 0xFFF44336; // Rojo
            case "Cultural": return 0xFFF44336; // Rojo
            case "Tecnico": return 0xFF455A64; // Azul Oscuro
            case "General": return 0xFF616161; // Gris Oscuro
            default: return 0xFF616161; // Gris Oscuro por defecto
        }
    }

    public static int getStatusBackgroundColor(String status) {
        if ("Active".equalsIgnoreCase(status) || "ACTIVO".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#E8F5E9"); // Light Green
        } else if ("Pending".equalsIgnoreCase(status) || "PENDIENTE".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#FFF8E1"); // Light Orange
        } else if ("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#EEEEEE"); // Light Gray
        } else if ("InProgress".equalsIgnoreCase(status) || "EN_PROGRESO".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#E3F2FD"); // Light Blue
        } else if ("Suspended".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status) || "SUSPENDIDA".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#FFEBEE"); // Light Red
        } else {
            return android.graphics.Color.LTGRAY;
        }
    }

    public static int getStatusTextColor(String status) {
        if ("Active".equalsIgnoreCase(status) || "ACTIVO".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#4CAF50"); // Green
        } else if ("Pending".equalsIgnoreCase(status) || "PENDIENTE".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#FFA000"); // Orange
        } else if ("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#757575"); // Grey
        } else if ("InProgress".equalsIgnoreCase(status) || "EN_PROGRESO".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#2196F3"); // Blue
        } else if ("Suspended".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status) || "SUSPENDIDA".equalsIgnoreCase(status)) {
            return android.graphics.Color.parseColor("#D32F2F"); // Darker Red
        } else {
            return android.graphics.Color.DKGRAY;
        }
    }

    public static String getStatusLabel(String status) {
        if ("Active".equalsIgnoreCase(status) || "ACTIVO".equalsIgnoreCase(status)) {
            return "Activa";
        } else if ("Pending".equalsIgnoreCase(status) || "PENDIENTE".equalsIgnoreCase(status)) {
            return "Pendiente";
        } else if ("Finished".equalsIgnoreCase(status) || "FINALIZADA".equalsIgnoreCase(status)) {
            return "Finalizada";
        } else if ("InProgress".equalsIgnoreCase(status) || "EN_PROGRESO".equalsIgnoreCase(status)) {
            return "En Progreso";
        } else if ("Suspended".equalsIgnoreCase(status) || "SUSPENDIDO".equalsIgnoreCase(status) || "SUSPENDIDA".equalsIgnoreCase(status)) {
            return "Suspendida";
        } else {
            return status != null ? status : "Desconocido";
        }
    }
}
