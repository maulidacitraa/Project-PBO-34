import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.SQLException;

public class AuthService {
    
    private User getUserByUsername(String username) {
        String SQL_SELECT = "SELECT id, username, password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            if (conn == null) return null;
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Jika ditemukan, kembalikan objek User dengan ID
                    int userId = rs.getInt("id");
                    return new User(userId, rs.getString("username"), rs.getString("password"));
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Gagal cek username di database: " + e.getMessage());
            return null;
        }
    }
    
    public synchronized boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return false;
        
        if (getUserByUsername(username) != null) {
            System.err.println("Username " + username + " sudah terdaftar.");
            return false;
        }

        String SQL_INSERT = "INSERT INTO users (username, password) VALUES (?, ?)"; 
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            if (conn == null) return false;
            
            stmt.setString(1, username);
            stmt.setString(2, password); 
            
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0; 

        } catch (SQLException e) {
            System.err.println("Gagal registrasi ke database: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String SQL_SELECT = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
            
            if (conn == null) return null; // Koneksi gagal
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Login berhasil, kembalikan objek User (yang sekarang memiliki ID)
                    int userId = rs.getInt("id");
                    return new User(userId, rs.getString("username"), rs.getString("password"));
                }
            }
            return null; // Login gagal (username/password salah)
        } catch (SQLException e) {
            System.err.println("Gagal login database: " + e.getMessage());
            return null;
        }
    }
}