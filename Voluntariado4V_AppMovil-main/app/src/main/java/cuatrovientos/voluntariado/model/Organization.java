package cuatrovientos.voluntariado.model;

public class Organization {
    private String name;
    private String email;
    private String date;           // Para solicitudes (ej: "2025-11-20")
    private String volunteersCount;// Para registradas (ej: "12")
    private String status;         // "Pending" o "Active"

    public Organization(String name, String email, String date, String volunteersCount, String status) {
        this.name = name;
        this.email = email;
        this.date = date;
        this.volunteersCount = volunteersCount;
        this.status = status;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDate() { return date; }
    public String getVolunteersCount() { return volunteersCount; }
    public String getStatus() { return status; }
}