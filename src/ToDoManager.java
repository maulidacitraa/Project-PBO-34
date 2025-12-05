import java.sql.*;
import java.time.format.DateTimeFormatter;

public class ToDoManager {

    public boolean saveToDo(ToDo todo, int userId) {
        String SQL_INSERT_TODO = "INSERT INTO todo (user_id, title, description, date, priority, type, completed) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtTodo = conn.prepareStatement(SQL_INSERT_TODO, Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) return false;

            String dateString = todo.getDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            stmtTodo.setInt(1, userId);
            stmtTodo.setString(2, todo.getTitle());
            stmtTodo.setString(3, todo.getDescription());
            stmtTodo.setString(4, dateString);
            stmtTodo.setString(5, todo.getPriority().name());
            stmtTodo.setString(6, todo.getType());
            stmtTodo.setBoolean(7, todo.isCompleted());

            stmtTodo.executeUpdate();

            int todoId = -1;
            try (ResultSet generatedKeys = stmtTodo.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    todoId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Gagal mendapatkan ID ToDo");
                }
            }

            // Insert ke child table
            return insertChildTable(conn, todo, todoId);

        } catch (SQLException e) {
            System.err.println("❌ Error save ToDo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertChildTable(Connection conn, ToDo todo, int todoId) throws SQLException {
        String SQL_CHILD;
        PreparedStatement stmtChild;

        if (todo instanceof ActivityToDo) {
            SQL_CHILD = "INSERT INTO todo_activity (id, location) VALUES (?, ?)";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setInt(1, todoId);
            stmtChild.setString(2, ((ActivityToDo) todo).getLocation());
        } else if (todo instanceof EventToDo) {
            SQL_CHILD = "INSERT INTO todo_event (id, duration_hours) VALUES (?, ?)";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setInt(1, todoId);
            stmtChild.setInt(2, ((EventToDo) todo).getDurationHours());
        } else {
            SQL_CHILD = "INSERT INTO todo_task (id, important) VALUES (?, ?)";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setInt(1, todoId);
            stmtChild.setBoolean(2, ((TaskToDo) todo).isImportant());
        }

        int rowsAffected = stmtChild.executeUpdate();
        stmtChild.close();
        return rowsAffected > 0;
    }

    public boolean updateToDo(ToDo todo) {
        String SQL_UPDATE = "UPDATE todo SET title = ?, description = ?, date = ?, priority = ?, completed = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            if (conn == null) return false;

            String dateString = todo.getDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setString(3, dateString);
            stmt.setString(4, todo.getPriority().name());
            stmt.setBoolean(5, todo.isCompleted());
            stmt.setInt(6, todo.getId());

            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                updateChildTable(conn, todo);
            }
            
            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error update ToDo: " + e.getMessage());
            return false;
        }
    }

    private void updateChildTable(Connection conn, ToDo todo) throws SQLException {
        String SQL_CHILD;
        PreparedStatement stmtChild;

        if (todo instanceof ActivityToDo) {
            SQL_CHILD = "UPDATE todo_activity SET location = ? WHERE id = ?";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setString(1, ((ActivityToDo) todo).getLocation());
            stmtChild.setInt(2, todo.getId());
        } else if (todo instanceof EventToDo) {
            SQL_CHILD = "UPDATE todo_event SET duration_hours = ? WHERE id = ?";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setInt(1, ((EventToDo) todo).getDurationHours());
            stmtChild.setInt(2, todo.getId());
        } else {
            SQL_CHILD = "UPDATE todo_task SET important = ? WHERE id = ?";
            stmtChild = conn.prepareStatement(SQL_CHILD);
            stmtChild.setBoolean(1, ((TaskToDo) todo).isImportant());
            stmtChild.setInt(2, todo.getId());
        }

        stmtChild.executeUpdate();
        stmtChild.close();
    }

    public boolean deleteToDo(int todoId) {
        String SQL = "DELETE FROM todo WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            if (conn == null) return false;

            stmt.setInt(1, todoId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error delete ToDo: " + e.getMessage());
            return false;
        }
    }
}