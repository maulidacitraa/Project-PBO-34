import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchThread extends Thread {
    private final int userId;
    private final String keyword;
    private final List<ToDo> searchResults;
    private boolean finished;
    private String errorMessage;

    public SearchThread(int userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchResults = new ArrayList<>();
        this.finished = false;
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
                     "WHERE t.user_id = ? AND (t.title LIKE ? OR t.description LIKE ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }

            String searchPattern = "%" + keyword + "%";
            stmt.setInt(1, userId);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            Thread.sleep(300); // Simulasi delay

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
                        searchResults.add(todo);
                    }
                }
            }

        } catch (SQLException | InterruptedException e) {
            errorMessage = "Search error: " + e.getMessage();
            System.err.println("❌ " + errorMessage);
        } finally {
            finished = true;
        }
    }

    public boolean isFinished() { return finished; }
    public List<ToDo> getSearchResults() { return searchResults; }
    public String getErrorMessage() { return errorMessage; }
}