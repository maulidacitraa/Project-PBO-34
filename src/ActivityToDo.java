import java.time.LocalDate;

public class ActivityToDo extends ToDo {

    private String location;

    public ActivityToDo(String title, String description, LocalDate date, Priority priority, String location) {
        super(title, description, date, priority);
        this.location = location;
    }

    @Override
    public void showDetails() {
        System.out.println("[ACTIVITY] " + title + " | " + date + " | " + priority);
        System.out.println("  Location: " + location);
    }
    
    @Override
    public String getType() {
        return "ActivityTodo";
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}
