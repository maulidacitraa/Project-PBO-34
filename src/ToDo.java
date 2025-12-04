import java.time.LocalDate;

public abstract class ToDo {

    protected String title;
    protected String description;
    protected LocalDate date;
    protected Priority priority;
    public boolean completed = false;

    public ToDo(String title, String description, LocalDate date, Priority priority) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
    }

    public abstract void showDetails();
    
    public abstract String getType();
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getPriority() {
        return priority.toString().charAt(0) + priority.toString().substring(1).toLowerCase();
    }
    
    public void setPriority(String priorityStr) {
        this.priority = Priority.valueOf(priorityStr.toUpperCase());
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

