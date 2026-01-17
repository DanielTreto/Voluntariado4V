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

    public Organization(String id, String name, String email, String description, String type, String phone, String sector, String scope, String contactPerson, String date, String volunteersCount, String status, String avatarUrl) {
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
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    // Getters for new fields
    public String getType() { return type; }
    public String getPhone() { return phone; }
    public String getSector() { return sector; }
    public String getScope() { return scope; }
    public String getContactPerson() { return contactPerson; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDate() { return date; }
    public String getVolunteersCount() { return volunteersCount; }
    public String getStatus() { return status; }
    public String getAvatarUrl() { return avatarUrl; }
}