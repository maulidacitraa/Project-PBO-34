import java.sql.*;
import java.util.List;

public class BatchOperationThread extends Thread {
    
    private final List<Integer> todoIds;
    private final String operation; // "DELETE" or "COMPLETE"
    private int successCount = 0;
    private boolean finished = false;
    private String errorMessage = null;
    
    public BatchOperationThread(List<Integer> todoIds, String operation) {
        this.todoIds = todoIds;
        this.operation = operation;
    }
    
    @Override
    public void run() {
        System.out.println("🔄 Batch operation: " + operation + " on " + todoIds.size() + " todos");
        
        String SQL;
        if ("DELETE".equals(operation)) {
            SQL = "DELETE FROM todo WHERE id = ?";
        } else {
            SQL = "UPDATE todo SET title = CONCAT(title, '') WHERE id = ?"; // placeholder untuk completed
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }
            
            conn.setAutoCommit(false); // Start transaction
            
            for (Integer todoId : todoIds) {
                stmt.setInt(1, todoId);
                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    successCount++;
                }
                
                // Simulasi processing time
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("✅ Batch operation completed: " + successCount + "/" + todoIds.size() + " successful");
            
        } catch (SQLException e) {
            errorMessage = "Batch operation error: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            finished = true;
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}