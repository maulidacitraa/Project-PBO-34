import java.time.LocalDate;

public class TaskToDo extends ToDo {
    private boolean important;

    public TaskToDo(int id, String title, String description, LocalDate date, 
                   Priority priority, boolean completed, boolean important) {
        super(id, title, description, date, priority, completed);
        this.important = important;
    }

    public TaskToDo(String title, String description, LocalDate date, Priority priority) {
        super(title, description, date, priority);
        this.important = false;
    }

    @Override
    public String getType() {
        return "TASK";
    }

    @Override
    public String getTypeEmoji() {
        return important ? "⭐" : "✏️";
    }

    @Override
    public String getExtraInfo() {
        return important ? "⭐ Important Task" : "📝 Regular Task";
    }

    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
}