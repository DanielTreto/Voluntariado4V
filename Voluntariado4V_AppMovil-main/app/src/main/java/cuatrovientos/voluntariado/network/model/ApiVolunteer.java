package cuatrovientos.voluntariado.network.model;

public class ApiVolunteer {
    private String id;
    private String name;
    private String surname1;
    private String surname2;
    private String email;
    private String phone;
    private String dni;
    private String dateOfBirth;
    private String description;
    private String course;
    private String status;
    private String avatar; // Restored
    private java.util.List<String> preferences; 
    private java.util.List<ApiAvailability> availability;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname1() { return surname1; }
    public String getSurname2() { return surname2; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDni() { return dni; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getDescription() { return description; }
    public String getCourse() { return course; }
    public String getStatus() { return status; }
    public String getAvatar() { return avatar; }
    public java.util.List<String> getPreferences() { return preferences; }
    public java.util.List<ApiAvailability> getAvailability() { return availability; }
}
