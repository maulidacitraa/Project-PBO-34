import java.time.LocalDate;

public class EventToDo extends ToDo {

    private int durationHours;

    public EventToDo(String title, String description, LocalDate date, Priority priority, int durationHours) {
        super(title, description, date, priority);
        this.durationHours = durationHours;
    }

    @Override
    public void showDetails() {
        System.out.println("[EVENT] " + title + " | " + date + " | " + priority);
        System.out.println("  Duration: " + durationHours + " hours");
    }
    
    @Override
    public String getType() {
        return "EventTodo";
    }
    
    public int getDurationHours() {
        return durationHours;
    }
    
    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }
}
