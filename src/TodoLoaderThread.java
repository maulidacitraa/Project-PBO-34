import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoLoaderThread extends Thread {
    private final int userId;
    private final List<ToDo> loadedTodos;
    private boolean isLoading;
    private String errorMessage;

    public TodoLoaderThread(int userId) {
        this.userId = userId;
        this.loadedTodos = new ArrayList<>();
        this.isLoading = true;
        this.errorMessage = null;
    }

    @Override
    public void run() {
        String SQL = "SELECT t.id, t.title, t.description, t.date, t.priority, t.type, t.completed, " +
                     "ta.location, te.duration_hours, tt.important " +
                     "FROM todo t " +
                     "LEFT JOIN todo_activity ta ON t.id = ta.id " +
                     "LEFT JOIN todo_event te ON t.id = te.id " +
                     "LEFT JOIN todo_task tt ON t.id = tt.id " +
                     "WHERE t.user_id = ? ORDER BY t.date ASC, t.completed ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                isLoading = false;
                return;
            }

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    Priority priority = Priority.valueOf(rs.getString("priority"));
                    String type = rs.getString("type");
                    boolean completed = rs.getBoolean("completed");

                    ToDo todo = null;

                    switch (type) {
                        case "ACTIVITY":
                            String location = rs.getString("location");
                            todo = new ActivityToDo(id, title, description, date, priority, completed, location);
                            break;
                        case "EVENT":
                            int duration = rs.getInt("duration_hours");
                            todo = new EventToDo(id, title, description, date, priority, completed, duration);
                            break;
                        case "TASK":
                            boolean important = rs.getBoolean("important");
                            todo = new TaskToDo(id, title, description, date, priority, completed, important);
                            break;
                    }

                    if (todo != null) {
                        loadedTodos.add(todo);
                    }
                }
            }

        } catch (SQLException e) {
            errorMessage = "Error loading todos: " + e.getMessage();
            System.err.println("❌ " + errorMessage);
        } finally {
            isLoading = false;
        }
    }

    public boolean isLoading() { return isLoading; }
    public List<ToDo> getLoadedTodos() { return loadedTodos; }
    public String getErrorMessage() { return errorMessage; }
}