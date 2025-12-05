import java.time.LocalDate;

public class ActivityToDo extends ToDo {
    private String location;

    public ActivityToDo(int id, String title, String description, LocalDate date, 
                       Priority priority, boolean completed, String location) {
        super(id, title, description, date, priority, completed);
        this.location = location;
    }

    public ActivityToDo(String title, String description, LocalDate date, 
                       Priority priority, String location) {
        super(title, description, date, priority);
        this.location = location;
    }

    @Override
    public String getType() {
        return "ACTIVITY";
    }

    @Override
    public String getTypeEmoji() {
        return "🎯";
    }

    @Override
    public String getExtraInfo() {
        return "📍 " + location;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}