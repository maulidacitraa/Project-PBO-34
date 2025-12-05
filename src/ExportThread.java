import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportThread extends Thread {
    
    private final int userId;
    private final String format; // "CSV" or "TXT"
    private String outputFilePath = null;
    private boolean finished = false;
    private String errorMessage = null;
    
    public ExportThread(int userId, String format) {
        this.userId = userId;
        this.format = format;
    }
    
    @Override
    public void run() {
        System.out.println("📤 Exporting todos to " + format + " format...");
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        outputFilePath = "todos_export_" + timestamp + "." + format.toLowerCase();
        
        String SQL = "SELECT t.title, t.description, t.date, t.priority, t.type, " +
                     "ta.location, te.duration_hours " +
                     "FROM todo t " +
                     "LEFT JOIN todo_activity ta ON t.id = ta.id " +
                     "LEFT JOIN todo_event te ON t.id = te.id " +
                     "WHERE t.user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             FileWriter writer = new FileWriter(outputFilePath)) {
            
            if (conn == null) {
                errorMessage = "Koneksi database gagal";
                finished = true;
                return;
            }
            
            stmt.setInt(1, userId);
            
            // Write header
            if ("CSV".equals(format)) {
                writer.write("Title,Description,Date,Priority,Type,Location,Duration\n");
            } else {
                writer.write("========== TODO LIST EXPORT ==========\n\n");
            }
            
            int count = 0;
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String date = rs.getString("date");
                    String priority = rs.getString("priority");
                    String type = rs.getString("type");
                    String location = rs.getString("location");
                    String duration = rs.getString("duration_hours");
                    
                    if ("CSV".equals(format)) {
                        writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                                escapeCSV(title), escapeCSV(description), date, priority, type,
                                location != null ? location : "", duration != null ? duration : ""));
                    } else {
                        writer.write(String.format("[%s] %s\n", type, title));
                        writer.write(String.format("  Description: %s\n", description));
                        writer.write(String.format("  Date: %s | Priority: %s\n", date, priority));
                        if (location != null) {
                            writer.write(String.format("  Location: %s\n", location));
                        }
                        if (duration != null) {
                            writer.write(String.format("  Duration: %s hours\n", duration));
                        }
                        writer.write("\n");
                    }
                    
                    count++;
                    
                    // Simulasi processing
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            if ("TXT".equals(format)) {
                writer.write(String.format("\nTotal: %d todos exported\n", count));
            }
            
            System.out.println("✅ Exported " + count + " todos to: " + outputFilePath);
            
        } catch (SQLException | IOException e) {
            errorMessage = "Export error: " + e.getMessage();
            System.err.println(errorMessage);
        } finally {
            finished = true;
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public String getOutputFilePath() {
        return outputFilePath;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}