import java.time.LocalDate;

public abstract class ToDo {

    protected String title;
    protected String description;
    protected LocalDate date;
    protected Priority priority;

    public ToDo(String title, String description, LocalDate date, Priority priority) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
    }

    public abstract void showDetails();
}

