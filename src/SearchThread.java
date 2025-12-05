import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchThread extends Thread {
    
    private final int userId;
    private final String keyword;
    private List<ToDo> searchResults;
    private boolean finished = false;
    private String errorMessage = null;
    
    public SearchThread(int userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
        this.searchResults = new ArrayList<>();
    }
    
    @Override
    public void run() {
        System.out.println("🔍 Searching todos with keyword: '" + keyword + "'");
        
        String SQL = "SELECT t.id, t.title, t.description, t.date, t.priority, t.type, " +
                     "ta.location, te.duration_hours " +
                     "FROM todo t " +
                     "LEFT JOIN todo_activity ta ON t.id = ta.id " +
                     "LEFT JOIN todo_event te ON t.id = te.id " +
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
            
            // Simulasi delay pencarian
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
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
                        searchResults.add(todo);
                    }
                }
            }
            
            System.out.println("✅ Found " + searchResults.size() + " matching todos");
            
        } catch (SQLException e) {
            errorMessage = "Search error: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            finished = true;
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public List<ToDo> getSearchResults() {
        return searchResults;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}