package cuatrovientos.voluntariado.model;

public class Volunteer {
    private String id;
    private String name;
    private String surname1; 
    private String surname2; 
    private String email;
    private String phone;
    private String dni;      
    private String birthDate;
    private String description; 
    private String role;     
    private java.util.List<String> preferences; 
    private String status;
    private String avatarUrl;
    private String course; 
    private java.util.List<String> availability; 

    public Volunteer(String id, String name, String surname1, String surname2, String email, String phone, String dni, String birthDate, String description, String role, java.util.List<String> preferences, String status, String avatarUrl, String course, java.util.List<String> availability) {
        this.id = id;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.email = email;
        this.phone = phone;
        this.dni = dni;
        this.birthDate = birthDate;
        this.description = description;
        this.role = role;
        this.preferences = preferences;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.course = course;
        this.availability = availability;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname1() { return surname1; }
    public String getSurname2() { return surname2; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDni() { return dni; }
    public String getBirthDate() { return birthDate; }
    public String getDescription() { return description; }
    public String getRole() { return role; }
    public java.util.List<String> getPreferences() { return preferences; }
    public String getStatus() { return status; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCourse() { return course; }
    public java.util.List<String> getAvailability() { return availability; }

}