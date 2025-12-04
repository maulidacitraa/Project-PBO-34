import java.util.ArrayList;
import java.util.List;

public class AuthService {

    // shared user storage so registrations persist across AuthService instances
    private static final List<User> users = new ArrayList<>();

    static {
        users.add(new User("admin", "123"));
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) return false;
        for (User u : users) {
            if (username.equals(u.getUsername()) && password.equals(u.getPassword())) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return false;
        // check if username already exists
        for (User u : users) {
            if (u.getUsername().equals(username)) return false;
        }
        users.add(new User(username, password));
        return true;
    }
}
