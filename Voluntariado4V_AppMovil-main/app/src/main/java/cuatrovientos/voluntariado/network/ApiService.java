package cuatrovientos.voluntariado.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @retrofit2.http.GET("volunteers")
    Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiVolunteer>> getVolunteers();

    @retrofit2.http.GET("volunteers/{id}")
    Call<cuatrovientos.voluntariado.network.model.ApiVolunteer> getVolunteer(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("organizations")
    Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiOrganization>> getOrganizations();

    @retrofit2.http.GET("organizations/{id}")
    Call<cuatrovientos.voluntariado.model.Organization> getOrganization(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("activities")
    Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> getActivities();

    @retrofit2.http.GET("activities")
    Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> getOrganizationActivities(@retrofit2.http.Query("organizationId") String organizationId);

    @retrofit2.http.GET("volunteers/{id}/activities")
    Call<java.util.List<cuatrovientos.voluntariado.network.model.ApiActivity>> getVolunteerActivities(@retrofit2.http.Path("id") String id);
    @retrofit2.http.GET("admin/dashboard/stats")
    Call<cuatrovientos.voluntariado.network.model.AdminStats> getAdminStats(@retrofit2.http.Query("year") int year);
}
