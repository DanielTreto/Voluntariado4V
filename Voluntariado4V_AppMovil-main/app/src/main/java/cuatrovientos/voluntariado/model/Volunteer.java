package cuatrovientos.voluntariado.model;

public class Volunteer {
    private String name;
    private String role;
    private String email;
    private String status; // "Pending" o "Active"
    private String date;
    private String phone;
    private String avatarUrl; // <--- NEW FIELD

    public Volunteer(String name, String role, String email, String phone, String status, String date, String avatarUrl) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.date = date;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
}