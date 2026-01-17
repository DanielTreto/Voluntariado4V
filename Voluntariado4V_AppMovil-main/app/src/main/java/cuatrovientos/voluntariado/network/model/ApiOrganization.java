package cuatrovientos.voluntariado.network.model;

public class ApiOrganization {
    private String id;
    private String name;
    private String avatar;

    private String email;
    private String description;
    private String status;
    private String type;
    private String phone;
    private String sector;
    private String scope;
    private String contactPerson;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    
    public String getType() { return type; }
    public String getPhone() { return phone; }
    public String getSector() { return sector; }
    public String getScope() { return scope; }
    public String getContactPerson() { return contactPerson; }
}
