package cuatrovientos.voluntariado.model;

public class Organization {
    private String id;             
    private String name;
    private String email;
    private String description;
    private String type;           // <--- NEW
    private String phone;          // <--- NEW
    private String sector;         // <--- NEW
    private String scope;          // <--- NEW
    private String contactPerson;  // <--- NEW
    private String date;           
    private String volunteersCount;
    private String status;         
    private String avatarUrl;      
    private java.util.Set<String> activityStatuses; // <--- NEW

    public Organization() {}

    public Organization(String id, String name, String email, String description, String type, String phone, String sector, String scope, String contactPerson, String date, String volunteersCount, String status, String avatarUrl, java.util.Set<String> activityStatuses) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
        this.type = type;
        this.phone = phone;
        this.sector = sector;
        this.scope = scope;
        this.contactPerson = contactPerson;
        this.date = date;
        this.volunteersCount = volunteersCount;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.activityStatuses = activityStatuses;
    }

    public java.util.Set<String> getActivityStatuses() { return activityStatuses; }
    public void setActivityStatuses(java.util.Set<String> activityStatuses) { this.activityStatuses = activityStatuses; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Getters for new fields
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getVolunteersCount() { return volunteersCount; }
    public void setVolunteersCount(String volunteersCount) { this.volunteersCount = volunteersCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}