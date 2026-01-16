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
    private String imagen; // Matches backend field 'imagen'
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getImagen() { return imagen; }
}
