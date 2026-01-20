package cuatrovientos.voluntariado.network;

public class LoginResponse {
    private boolean success;
    private String role;
    private String id;
    private String name;
    private String email;
    private String firebaseUid;
    private String error; // For error cases

    public boolean isSuccess() {
        return success;
    }

    public String getRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getError() {
        return error;
    }
}
