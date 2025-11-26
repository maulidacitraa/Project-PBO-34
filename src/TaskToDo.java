import java.time.LocalDate;

public class TaskToDo extends ToDo {

    public TaskToDo(String title, String description, LocalDate date, Priority priority) {
        super(title, description, date, priority);
    }

    @Override
    public void showDetails() {
        System.out.println("[TASK] " + title + " | " + date + " | " + priority);
        System.out.println("  > " + description);
    }
    
    @Override
    public String getType() {
        return "TaskTodo";
    }
}
