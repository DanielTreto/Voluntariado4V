package cuatrovientos.voluntariado.model;

public class EventDay {
    private String dayNumber;   
    private String eventTitle;  
    private String colorHex;    
    private VolunteerActivity activity; 

    public EventDay(String dayNumber, String eventTitle, String colorHex, VolunteerActivity activity) {
        this.dayNumber = dayNumber;
        this.eventTitle = eventTitle;
        this.colorHex = colorHex;
        this.activity = activity;
    }

    public String getDayNumber() { return dayNumber; }
    public String getEventTitle() { return eventTitle; }
    public String getColorHex() { return colorHex; }
    public VolunteerActivity getActivity() { return activity; }
}