import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginGUI extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginGUI() {

        setTitle("Let's Do!! - Login");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        Color PRIMARY = new Color(0x4682A9);    
        Color SECONDARY = new Color(0xC4E1E6);  
        Color CARD = new Color(0x3B6E8A);       

        // BASE
        JPanel base = new JPanel(new BorderLayout());
        base.setBackground(PRIMARY);
        setContentPane(base);

        // TITLE
        JLabel appTitle = new JLabel("Let's Do!!", SwingConstants.CENTER);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 40));
        appTitle.setBorder(new EmptyBorder(40, 0, 20, 0));
        base.add(appTitle, BorderLayout.NORTH);

        // WRAPPER (center vertically)
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        base.add(wrapper, BorderLayout.CENTER);

        // CARD
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 450));
        card.setLayout(null);
        wrapper.add(card);

        // LOGIN LABEL
        JLabel loginLabel = new JLabel("Login", SwingConstants.CENTER);
        loginLabel.setForeground(SECONDARY);
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        loginLabel.setBounds(0, 25, 380, 40);
        card.add(loginLabel);

        // USERNAME
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setBounds(60, 90, 260, 20);
        card.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(60, 115, 260, 40);
        usernameField.setBackground(SECONDARY);
        usernameField.setForeground(Color.BLACK);
        usernameField.setCaretColor(Color.BLACK);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(new EmptyBorder(5,10,5,10));
        card.add(usernameField);

        // PASSWORD
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setBounds(60, 175, 260, 20);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(60, 200, 260, 40);
        passwordField.setBackground(SECONDARY);
        passwordField.setForeground(Color.BLACK);
        passwordField.setCaretColor(Color.BLACK);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(5,10,5,10));
        card.add(passwordField);

        // LOGIN BUTTON
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(60, 260, 260, 45);
        btnLogin.setBackground(SECONDARY);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(btnLogin);

        btnLogin.addActionListener(e -> handleLogin());

        // REGISTER BUTTON
        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(60, 320, 260, 40);
        btnRegister.setBackground(SECONDARY);
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(btnRegister);

        btnRegister.addActionListener(e -> handleRegister());
    }

    // ==========================
    // LOGIN PROCESS
    // ==========================
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());

        if (user.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Isi username dan password dulu!",
                    "Gagal Login",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService auth = new AuthService();

        User loggedInUser = auth.login(user, pass); 

        if (loggedInUser != null) {
            TodoGUI todoGUI = new TodoGUI(loggedInUser);
            todoGUI.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah! (Cek koneksi DB & data user)",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // REGISTER PROCESS
    // ==========================
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());

        if (user.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Isi username dan password dulu!",
                    "Gagal Registrasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService auth = new AuthService();
        boolean ok = auth.register(user, pass);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Registrasi berhasil! Silakan login.",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username sudah digunakan atau data tidak valid.",
                    "Gagal Registrasi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}