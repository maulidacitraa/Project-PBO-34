import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class ToDo {
    protected int id;
    protected String title;
    protected String description;
    protected LocalDate date;
    protected Priority priority;
    protected boolean completed;

    public ToDo(int id, String title, String description, LocalDate date, Priority priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.completed = completed;
    }

    public ToDo(String title, String description, LocalDate date, Priority priority) {
        this(-1, title, description, date, priority, false);
    }

    public abstract String getType();
    public abstract String getTypeEmoji();
    public abstract String getExtraInfo();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public boolean isOverdue() {
        return !completed && date.isBefore(LocalDate.now());
    }

    public boolean isDueToday() {
        return !completed && date.isEqual(LocalDate.now());
    }
}