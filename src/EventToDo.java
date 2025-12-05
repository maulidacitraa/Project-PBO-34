import java.time.LocalDate;

public class EventToDo extends ToDo {
    private int durationHours;

    public EventToDo(int id, String title, String description, LocalDate date, 
                    Priority priority, boolean completed, int durationHours) {
        super(id, title, description, date, priority, completed);
        this.durationHours = durationHours;
    }

    public EventToDo(String title, String description, LocalDate date, 
                    Priority priority, int durationHours) {
        super(title, description, date, priority);
        this.durationHours = durationHours;
    }

    @Override
    public String getType() {
        return "EVENT";
    }

    @Override
    public String getTypeEmoji() {
        return "📅";
    }

    @Override
    public String getExtraInfo() {
        return "⏱️ " + durationHours + " hours";
    }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }
}