import java.sql.*;

public class AuthService {
    
    public synchronized boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        
        if (getUserByUsername(username) != null) {
            return false;
        }

        String SQL = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            if (conn == null) return false;
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Gagal registrasi: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String SQL = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) return null;
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), 
                                  rs.getString("username"), 
                                  rs.getString("password"));
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Gagal login: " + e.getMessage());
            return null;
        }
    }

    private User getUserByUsername(String username) {
        String SQL = "SELECT id, username, password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            
            if (conn == null) return null;
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), 
                                  rs.getString("username"), 
                                  rs.getString("password"));
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            return null;
        }
    }
}