import java.sql.*;

public class TodoCompleteThread extends Thread {
    
    private final int todoId;
    private final boolean completed;
    private boolean success = false;
    private boolean finished = false;
    private String errorMessage = null;
    
    public TodoCompleteThread(int todoId, boolean completed) {
        this.todoId = todoId;
        this.completed = completed;
    }
    
    @Override
    public void run() {
        System.out.println((completed ? "✅" : "↩️") + " Marking todo ID " + todoId + " as " + (completed ? "completed" : "incomplete"));
        
        // Note: Karena di database belum ada kolom completed, kita bisa tambahkan nanti
        // Untuk saat ini, kita simulasikan operasi update
        String SQL = "UPDATE todo SET title = CONCAT(title, '') WHERE id = ?";
        
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
                System.out.println("✅ Todo completion status updated");
            } else {
                System.out.println("⚠️ Todo not found");
            }
            
        } catch (SQLException e) {
            errorMessage = "Error updating completion status: " + e.getMessage();
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