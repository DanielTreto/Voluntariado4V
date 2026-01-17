package cuatrovientos.voluntariado.model;

public class Volunteer {
    private String id;
    private String name;
    private String role;
    private String avatarUrl;
    private String email;
    private String phone;
    private String date; // Registration date or similar
    private String status;

    public Volunteer(String id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        // Defaults for compatibility
        this.role = "Voluntario";
        this.email = "";
        this.phone = "";
        this.date = "";
        this.status = "Active"; 
    }

    public Volunteer(String id, String name, String role, String email, String phone, String date, String status, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.date = date;
        this.status = status;
        this.avatarUrl = avatarUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}