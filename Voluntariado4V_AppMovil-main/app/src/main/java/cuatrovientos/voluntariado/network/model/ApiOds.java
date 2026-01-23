package cuatrovientos.voluntariado.network.model;

import com.google.gson.annotations.SerializedName;

public class ApiOds {
    @SerializedName(value = "id", alternate = {"numods", "NUMODS", "numOds"})
    private int id;
    private String description;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
