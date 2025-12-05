import java.sql.*;

public class StatisticsThread extends Thread {
    private final int userId;
    private int totalTodos;
    private int completedTodos;
    private int activeTodos;
    private int totalActivity;
    private int totalEvent;
    private int totalTask;
    private int highPriority;
    private int mediumPriority;
    private int lowPriority;
    private int overdueTodos;
    private boolean finished;
    private String errorMessage;

    public StatisticsThread(int userId) {
        this.userId = userId;
        this.finished = false;
        this.errorMessage = null;
    }

    @Override
    public void run() {
        String SQL = "SELECT type, priority, completed, " +
                     "CASE WHEN date < CURDATE() AND completed = 0 THEN 1 ELSE 0 END as is_overdue, " +
                     "COUNT(*) as count " +
                     "FROM todo WHERE user_id = ? " +
                     "GROUP BY type, priority, completed, is_overdue";

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
                    boolean completed = rs.getBoolean("completed");
                    boolean isOverdue = rs.getBoolean("is_overdue");
                    int count = rs.getInt("count");

                    totalTodos += count;

                    if (completed) {
                        completedTodos += count;
                    } else {
                        activeTodos += count;
                    }

                    if (isOverdue) {
                        overdueTodos += count;
                    }

                    switch (type) {
                        case "ACTIVITY": totalActivity += count; break;
                        case "EVENT": totalEvent += count; break;
                        case "TASK": totalTask += count; break;
                    }

                    switch (priority) {
                        case "HIGH": highPriority += count; break;
                        case "MEDIUM": mediumPriority += count; break;
                        case "LOW": lowPriority += count; break;
                    }
                }
            }

        } catch (SQLException e) {
            errorMessage = "Error calculating statistics: " + e.getMessage();
            System.err.println("❌ " + errorMessage);
        } finally {
            finished = true;
        }
    }

    public boolean isFinished() { return finished; }
    public String getErrorMessage() { return errorMessage; }
    
    public String getSummary() {
        return String.format(
            "📊 STATISTICS SUMMARY\n\n" +
            "Total Todos: %d\n" +
            "✅ Completed: %d\n" +
            "⏳ Active: %d\n" +
            "⚠️ Overdue: %d\n\n" +
            "BY TYPE:\n" +
            "🎯 Activities: %d\n" +
            "📅 Events: %d\n" +
            "✏️ Tasks: %d\n\n" +
            "BY PRIORITY:\n" +
            "🔴 High: %d\n" +
            "🟡 Medium: %d\n" +
            "🟢 Low: %d",
            totalTodos, completedTodos, activeTodos, overdueTodos,
            totalActivity, totalEvent, totalTask,
            highPriority, mediumPriority, lowPriority
        );
    }

    public int getTotalTodos() { return totalTodos; }
    public int getCompletedTodos() { return completedTodos; }
    public int getActiveTodos() { return activeTodos; }
    public int getOverdueTodos() { return overdueTodos; }
}