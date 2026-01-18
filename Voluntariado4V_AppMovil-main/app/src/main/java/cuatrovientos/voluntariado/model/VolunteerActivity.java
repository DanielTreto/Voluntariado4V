package cuatrovientos.voluntariado.model;

public class VolunteerActivity {
    private String title;
    private String description;
    private String location;
    private String date;
    private String duration;
    private String endDate;
    private int maxVolunteers;
    private String category; // "Medio Ambiente", "Tecnológico", "Social", "Educativo"
    private String status;   // "Active" (Pestaña 1) o "Pending" (Pestaña 2)
    private String organizationName;
    private String organizationAvatar;

    private int id; // Added ID field
    private int imageColor; // Background color for category chip
    private String imageUrl; // Header image

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private java.util.List<Volunteer> participants;

    public VolunteerActivity(String title, String description, String location, String date, String duration, String endDate, int maxVolunteers, String category, String status, String organizationName, String organizationAvatar, int imageColor, String imageUrl) {
        this(title, description, location, date, duration, endDate, maxVolunteers, category, status, organizationName, organizationAvatar, imageColor, imageUrl, new java.util.ArrayList<>());
    }

    public VolunteerActivity(String title, String description, String location, String date, String duration, String endDate, int maxVolunteers, String category, String status, String organizationName, String organizationAvatar, int imageColor, String imageUrl, java.util.List<Volunteer> participants) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.duration = duration;
        this.endDate = endDate;
        this.maxVolunteers = maxVolunteers;
        this.category = category;
        this.status = status;
        this.organizationName = organizationName;
        this.organizationAvatar = organizationAvatar;
        this.imageColor = imageColor;
        this.imageUrl = imageUrl;
        this.participants = participants;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getDuration() { return duration; }
    public String getEndDate() { return endDate; }
    public int getMaxVolunteers() { return maxVolunteers; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getOrganizationName() { return organizationName; }
    public String getOrganizationAvatar() { return organizationAvatar; }
    public int getImageColor() { return imageColor; }
    public String getImageUrl() { return imageUrl; }
    public java.util.List<Volunteer> getParticipants() { return participants; }
    
    // Helper to get just avatars for the card view
    public java.util.List<String> getParticipantAvatars() {
        java.util.List<String> avatars = new java.util.ArrayList<>();
        if (participants != null) {
            for (Volunteer v : participants) {
                avatars.add(v.getAvatarUrl());
            }
        }
        return avatars;
    }
}