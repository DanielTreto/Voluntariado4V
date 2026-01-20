package cuatrovientos.voluntariado.network.model;

import com.google.gson.annotations.SerializedName;

public class AdminStats {
    @SerializedName("totalActivities")
    private int totalActivities;

    @SerializedName("totalVolunteers")
    private int totalVolunteers;

    @SerializedName("activitiesInProgress")
    private int activitiesInProgress;

    @SerializedName("totalOrganizations")
    private int totalOrganizations;

    @SerializedName("monthlyActivities")
    private java.util.List<Integer> monthlyActivities;

    @SerializedName("statusDistribution")
    private java.util.Map<String, Integer> statusDistribution;

    public int getTotalActivities() {
        return totalActivities;
    }

    public int getTotalOrganizations() {
        return totalOrganizations;
    }

    public int getTotalVolunteers() {
        return totalVolunteers;
    }

    public int getActivitiesInProgress() {
        return activitiesInProgress;
    }

    public java.util.List<Integer> getMonthlyActivities() {
        return monthlyActivities;
    }

    public java.util.Map<String, Integer> getStatusDistribution() {
        return statusDistribution;
    }
}
