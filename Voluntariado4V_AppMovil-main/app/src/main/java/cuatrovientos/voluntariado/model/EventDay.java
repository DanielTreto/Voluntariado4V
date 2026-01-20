package cuatrovientos.voluntariado.model;

public class EventDay {
    private String dayNumber;   // "1", "2", etc.
    private String eventTitle;  // "Limpieza de playa", o null si no hay evento
    private String colorHex;    // "#1B5E20" (Verde), etc.

    public EventDay(String dayNumber, String eventTitle, String colorHex) {
        this.dayNumber = dayNumber;
        this.eventTitle = eventTitle;
        this.colorHex = colorHex;
    }

    public String getDayNumber() { return dayNumber; }
    public String getEventTitle() { return eventTitle; }
    public String getColorHex() { return colorHex; }
}