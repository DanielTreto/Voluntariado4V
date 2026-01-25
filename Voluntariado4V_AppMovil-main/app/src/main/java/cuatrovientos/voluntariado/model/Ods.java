package cuatrovientos.voluntariado.model;

public class Ods {
    private int id;
    private String description;

    public Ods(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
