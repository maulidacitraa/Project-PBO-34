import java.time.LocalDateTime;
import java.util.List;

public class ReminderThread extends Thread {

    private boolean running = true;
    private final List<Task> tasks;
    private final int checkIntervalMs;

    public ReminderThread(List<Task> tasks, int checkIntervalMs) {
        this.tasks = tasks;
        this.checkIntervalMs = checkIntervalMs;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(checkIntervalMs);

                LocalDateTime now = LocalDateTime.now();

                for (Task task : tasks) {
                    if (!task.isDone() && task.getDeadline() != null) {
                        if (task.getDeadline().isBefore(now.plusMinutes(15))) {  
                            System.out.println("⚠️ Reminder: Task '" + task.getName() + "' akan segera jatuh tempo!");
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
