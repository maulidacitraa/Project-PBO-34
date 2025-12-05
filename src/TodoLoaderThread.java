import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoLoaderThread extends Thread {
    
    private final int userId;
    private List<ToDo> loadedTodos;
    private boolean isLoading = true;
    private String errorMessage = null;
    
    public TodoLoaderThread(int userId) {
        this.userId = userId;
        this.loadedTodos = new ArrayList<>();
    }
    
    @Override
    public void run() {
        System.out.println("🔄 Loading todos for user ID: " + userId);
        
        String SQL = "SELECT t.id, t.title, t.description, t.date, t.priority, t.type, " +
                     "ta.location, te.duration_hours " +
                     "FROM todo t " +
                     "LEFT JOIN todo_activity ta ON t.id = ta.id " +
                     "LEFT JOIN todo_event te ON t.id = te.id " +
                     "WHERE t.user_id = ?";
        
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
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    Priority priority = Priority.valueOf(rs.getString("priority"));
                    String type = rs.getString("type");
                    
                    ToDo todo = null;
                    
                    switch (type) {
                        case "ACTIVITY":
                            String location = rs.getString("location");
                            todo = new ActivityToDo(title, description, date, priority, location);
                            break;
                        case "EVENT":
                            int duration = rs.getInt("duration_hours");
                            todo = new EventToDo(title, description, date, priority, duration);
                            break;
                        case "TASK":
                            todo = new TaskToDo(title, description, date, priority);
                            break;
                    }
                    
                    if (todo != null) {
                        loadedTodos.add(todo);
                    }
                }
            }
            
            System.out.println("✅ Loaded " + loadedTodos.size() + " todos");
            
        } catch (SQLException e) {
            errorMessage = "Error loading todos: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            isLoading = false;
        }
    }
    
    public boolean isLoading() {
        return isLoading;
    }
    
    public List<ToDo> getLoadedTodos() {
        return loadedTodos;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}