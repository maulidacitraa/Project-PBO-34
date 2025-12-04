import java.time.LocalDateTime;
import java.util.List;

public class ReminderThread extends Thread {

    private boolean running = true;
    private final List<ToDo> todos;
    private final int checkIntervalMs;

    public ReminderThread(List<ToDo> todos, int checkIntervalMs) {
        this.todos = todos;
        this.checkIntervalMs = checkIntervalMs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(checkIntervalMs);

                LocalDateTime now = LocalDateTime.now();

                for (ToDo todo : todos) {
                    if (!todo.isCompleted() && todo.getDate() != null) {
                        if (todo.getDate().atStartOfDay().isBefore(now.plusMinutes(15))) {  
                            System.out.println("⚠️ Reminder: Todo '" + todo.getTitle() + "' akan segera jatuh tempo!");
                        }
                    }
                }

            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopReminder() {
        running = false;
        this.interrupt();
    }
}
