package cuatrovientos.voluntariado.network.model;

public class ApiActivity {
    private int id; // Backend likely returns int for Activity ID
    private String title;
    private String description;
    private String location;
    private String date;
    private String endDate;
    private String status;
    private String type;
    private String duration;
    private int maxVolunteers;
    private String imagen; // Matches backend field 'imagen'
    private ApiOrganization organization;
    
    private java.util.List<ApiVolunteer> volunteers;
    private java.util.List<ApiOds> ods;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getDuration() { return duration; }
    public int getMaxVolunteers() { return maxVolunteers; }
    public String getImagen() { return imagen; }
    public ApiOrganization getOrganization() { return organization; }
    public java.util.List<ApiVolunteer> getVolunteers() { return volunteers; }
    public java.util.List<ApiOds> getOds() { return ods; }
}
