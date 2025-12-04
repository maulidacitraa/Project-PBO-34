import java.sql.*;
import java.time.format.DateTimeFormatter; 

public class ToDoManager {

    public boolean saveToDo(ToDo todo, int userId) { 

        String SQL_INSERT_PARENT = "INSERT INTO todo (user_id, title, description, date, priority, type) VALUES (?, ?, ?, ?, ?, ?)";

        String SQL_INSERT_CHILD;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtParent = conn.prepareStatement(SQL_INSERT_PARENT, Statement.RETURN_GENERATED_KEYS)) {

            String dateString = todo.getDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            stmtParent.setInt(1, userId);
            stmtParent.setString(2, todo.getTitle());
            stmtParent.setString(3, todo.getDescription());
            stmtParent.setString(4, dateString);
            stmtParent.setString(5, todo.getPriority().toUpperCase()); 
            stmtParent.setString(6, todo.getType().toUpperCase().replace("TODO", "")); 

            stmtParent.executeUpdate();

            int todoId = -1;
            try (ResultSet generatedKeys = stmtParent.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    todoId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Gagal mendapatkan ID ToDo, INSERT parent gagal.");
                }
            }

            PreparedStatement stmtChild;
            
            if (todo instanceof ActivityToDo activity) {
                SQL_INSERT_CHILD = "INSERT INTO todo_activity (id, location) VALUES (?, ?)";
                stmtChild = conn.prepareStatement(SQL_INSERT_CHILD);
                stmtChild.setInt(1, todoId);
                stmtChild.setString(2, activity.getLocation());
            } else if (todo instanceof EventToDo event) {
                SQL_INSERT_CHILD = "INSERT INTO todo_event (id, durationHours) VALUES (?, ?)"; 
                stmtChild = conn.prepareStatement(SQL_INSERT_CHILD);
                stmtChild.setInt(1, todoId);
                stmtChild.setInt(2, event.getDurationHours());
            } else { // TaskToDo
                SQL_INSERT_CHILD = "INSERT INTO todo_task (id) VALUES (?)";
                stmtChild = conn.prepareStatement(SQL_INSERT_CHILD);
                stmtChild.setInt(1, todoId);
            }

            int rowsAffected = stmtChild.executeUpdate();
            stmtChild.close();
            
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database Error saat menyimpan To-Do: " + e.getMessage());
            e.printStackTrace(); 
            return false;
        }
    }
}