package cuatrovientos.voluntariado.model;

public class VolunteerActivity {
    private String title;
    private String description;
    private String location;
    private String date;
    private String category; // "Medio Ambiente", "Tecnológico", "Social", "Educativo"
    private String status;   // "Active" (Pestaña 1) o "Pending" (Pestaña 2)

    // En una app real usarías URLs, aquí usaremos colores o recursos para simular la imagen
    private int imageColor;

    public VolunteerActivity(String title, String description, String location, String date, String category, String status, int imageColor) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.category = category;
        this.status = status;
        this.imageColor = imageColor;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public int getImageColor() { return imageColor; }
}