public class AuthService {

    private User defaultUser = new User("admin", "123");

    public boolean login(String username, String password) {
        return username.equals(defaultUser.getUsername()) &&
               password.equals(defaultUser.getPassword());
    }
}
