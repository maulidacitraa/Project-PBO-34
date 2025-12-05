import java.sql.*;

public class StatisticsCalculatorThread extends Thread {
    
    private final int userId;
    private int totalTodos = 0;
    private int totalActivity = 0;
    private int totalEvent = 0;
    private int totalTask = 0;
    private int highPriority = 0;
    private int mediumPriority = 0;
    private int lowPriority = 0;
    private boolean finished = false;
    private String errorMessage = null;
    
    public StatisticsCalculatorThread(int userId) {
        this.userId = userId;
    }
    
    @Override
    public void run() {
        System.out.println("📊 Calculating statistics for user ID: " + userId);
        
        String SQL = "SELECT type, priority, COUNT(*) as count FROM todo WHERE user_id = ? GROUP BY type, priority";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    String priority = rs.getString("priority");
                    int count = rs.getInt("count");
                    
                    totalTodos += count;
                    
                    // Count by type
                    switch (type) {
                        case "ACTIVITY":
                            totalActivity += count;
                            break;
                        case "EVENT":
                            totalEvent += count;
                            break;
                        case "TASK":
                            totalTask += count;
                            break;
                    }
                    
                    // Count by priority
                    switch (priority) {
                        case "HIGH":
                            highPriority += count;
                            break;
                        case "MEDIUM":
                            mediumPriority += count;
                            break;
                        case "LOW":
                            lowPriority += count;
                            break;
                    }
                }
            }
            
            System.out.println("✅ Statistics calculated:");
            System.out.println("   Total: " + totalTodos);
            System.out.println("   Activity: " + totalActivity + " | Event: " + totalEvent + " | Task: " + totalTask);
            System.out.println("   High: " + highPriority + " | Medium: " + mediumPriority + " | Low: " + lowPriority);
            
        } catch (SQLException e) {
            errorMessage = "Error calculating statistics: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            finished = true;
        }
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public int getTotalTodos() {
        return totalTodos;
    }
    
    public int getTotalActivity() {
        return totalActivity;
    }
    
    public int getTotalEvent() {
        return totalEvent;
    }
    
    public int getTotalTask() {
        return totalTask;
    }
    
    public int getHighPriority() {
        return highPriority;
    }
    
    public int getMediumPriority() {
        return mediumPriority;
    }
    
    public int getLowPriority() {
        return lowPriority;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getSummary() {
        return String.format("Total: %d | Activity: %d | Event: %d | Task: %d\nHigh: %d | Medium: %d | Low: %d",
                totalTodos, totalActivity, totalEvent, totalTask, highPriority, mediumPriority, lowPriority);
    }
}