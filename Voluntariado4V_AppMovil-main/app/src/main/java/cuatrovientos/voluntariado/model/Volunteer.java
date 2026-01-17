package cuatrovientos.voluntariado.model;

public class Volunteer {
    private String id;
    private String name;
    private String surname1; // Added based on new constructor
    private String surname2; // New
    private String email;
    private String phone;
    private String dni;      // New
    private String birthDate;// New
    private String description; // New
    private String role;     
    private java.util.List<String> preferences; // New
    private String status;
    private String avatarUrl;

    public Volunteer(String id, String name, String surname1, String surname2, String email, String phone, String dni, String birthDate, String description, String role, java.util.List<String> preferences, String status, String avatarUrl) {
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
    public String getRole() { return role; } // Kept from original, as it's a field and in new constructor
    public java.util.List<String> getPreferences() { return preferences; }
    public String getStatus() { return status; }
    public String getAvatarUrl() { return avatarUrl; } // Kept from original, as it's a field and in new constructor
    // The 'getDate()' method was in the provided new getters, but the 'date' field was removed.
    // This method is removed to maintain consistency with the fields.
}