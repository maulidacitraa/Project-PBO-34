import java.sql.*;

//public class TodoDeleteThread extends Thread {
    
    private final int todoId;
    private boolean success = false;
    private boolean finished = false;
    private String errorMessage = null;
    
    public TodoDeleteThread(int todoId) {
        this.todoId = todoId;
    }
    
    @Override
    public void run() {
        System.out.println("🗑️ Deleting todo ID: " + todoId);
        
        String SQL = "DELETE FROM todo WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }
            
            stmt.setInt(1, todoId);
            int rowsAffected = stmt.executeUpdate();
            
            success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Todo deleted successfully");
            } else {
                System.out.println("⚠️ Todo not found");
            }
            
        } catch (SQLException e) {
            errorMessage = "Error deleting todo: " + e.getMessage();
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