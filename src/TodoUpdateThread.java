import java.sql.*;

public class TodoUpdateThread extends Thread {
    
    private final int todoId;
    private final String newTitle;
    private final String newPriority;
    private boolean success = false;
    private boolean finished = false;
    private String errorMessage = null;
    
    public TodoUpdateThread(int todoId, String newTitle, String newPriority) {
        this.todoId = todoId;
        this.newTitle = newTitle;
        this.newPriority = newPriority;
    }
    
    @Override
    public void run() {
        System.out.println("✏️ Updating todo ID: " + todoId);
        
        String SQL = "UPDATE todo SET title = ?, priority = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }
            
            stmt.setString(1, newTitle);
            stmt.setString(2, newPriority.toUpperCase());
            stmt.setInt(3, todoId);
            
            int rowsAffected = stmt.executeUpdate();
            success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Todo updated successfully");
            } else {
                System.out.println("⚠️ Todo not found");
            }
            
        } catch (SQLException e) {
            errorMessage = "Error updating todo: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            finished = true;
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}