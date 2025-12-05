import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginGUI extends JFrame {
    // Pink Color Palette 
    private final Color PINK_PRIMARY = new Color(0xFFB3D9);    // Soft pink
    private final Color PINK_SECONDARY = new Color(0xFFE5F0);  // Very light pink
    private final Color PINK_ACCENT = new Color(0xFF85B3);     // Darker pink
    private final Color PINK_DARK = new Color(0xE875A0);       // Strong pink
    private final Color WHITE = Color.WHITE;

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginGUI() {
        setTitle("✨ Cute Todo List - Login 💖");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Base Panel
        JPanel base = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PINK_SECONDARY,
                    0, getHeight(), PINK_PRIMARY
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Cute decorative circles
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);
                g2d.fillOval(getWidth() / 2 - 100, -80, 200, 200);
            }
        };
        base.setOpaque(true);
        setContentPane(base);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(40, 0, 20, 0));
        
        JLabel appTitle = new JLabel("✨ Cute Todo List 💖", SwingConstants.CENTER);
        appTitle.setForeground(PINK_DARK);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        
        JLabel subtitle = new JLabel("🌸 Organize your day with cuteness! 🌸", SwingConstants.CENTER);
        subtitle.setForeground(PINK_ACCENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(appTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitle);
        
        base.add(titlePanel, BorderLayout.NORTH);

        // Center Wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        base.add(wrapper, BorderLayout.CENTER);

        // Card Panel
        JPanel card = new RoundedPanel(30);
        card.setBackground(WHITE);
        card.setPreferredSize(new Dimension(420, 480));
        card.setLayout(null);
        wrapper.add(card);

        // Login Label with emoji
        JLabel loginLabel = new JLabel("🔐 Login", SwingConstants.CENTER);
        loginLabel.setForeground(PINK_DARK);
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginLabel.setBounds(0, 30, 420, 40);
        card.add(loginLabel);

        JLabel welcomeLabel = new JLabel("Welcome back! 💕", SwingConstants.CENTER);
        welcomeLabel.setForeground(PINK_ACCENT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setBounds(0, 75, 420, 25);
        card.add(welcomeLabel);

        // Username Section
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        userIcon.setBounds(60, 125, 30, 30);
        card.add(userIcon);

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(PINK_DARK);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setBounds(100, 120, 260, 20);
        card.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(60, 145, 300, 45);
        usernameField.setBackground(PINK_SECONDARY);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(5, 15, 5, 15)
        ));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(usernameField);

        // Password Section
        JLabel passIcon = new JLabel("🔑");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        passIcon.setBounds(60, 210, 30, 30);
        card.add(passIcon);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(PINK_DARK);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setBounds(100, 205, 260, 20);
        card.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(60, 230, 300, 45);
        passwordField.setBackground(PINK_SECONDARY);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(5, 15, 5, 15)
        ));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        card.add(passwordField);

        // Login Button
        JButton btnLogin = createCuteButton("💖 Login", PINK_DARK);
        btnLogin.setBounds(60, 300, 300, 50);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.addActionListener(e -> handleLogin());
        card.add(btnLogin);

        // Register Button
        JButton btnRegister = createCuteButton("✨ Create Account", PINK_ACCENT);
        btnRegister.setBounds(60, 365, 300, 45);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegister.addActionListener(e -> handleRegister());
        card.add(btnRegister);

        // Footer
        JLabel footerLabel = new JLabel("Made with 💖 by Cute Team", SwingConstants.CENTER);
        footerLabel.setForeground(PINK_ACCENT);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setBounds(0, 430, 420, 30);
        card.add(footerLabel);

        // Enter key listeners
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleLogin());
    }

    private JButton createCuteButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                super.paintComponent(g);
            }
        };
        
        button.setForeground(WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showCuteMessage("⚠️ Oops!", "Please fill in both username and password! 🌸", 
                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService auth = new AuthService();
        User loggedInUser = auth.login(user, pass);

        if (loggedInUser != null) {
            showCuteMessage("🎉 Welcome!", "Login successful! Let's be productive! 💖", 
                          JOptionPane.INFORMATION_MESSAGE);
            
            SwingUtilities.invokeLater(() -> {
                TodoGUI todoGUI = new TodoGUI(loggedInUser);
                todoGUI.setVisible(true);
                this.dispose();
            });
        } else {
            showCuteMessage("😢 Login Failed", 
                          "Invalid username or password. Please try again! 🌸", 
                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showCuteMessage("⚠️ Oops!", "Please fill in both username and password! 🌸", 
                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService auth = new AuthService();
        boolean success = auth.register(user, pass);

        if (success) {
            showCuteMessage("🎉 Success!", 
                          "Account created successfully! You can now login! 💖", 
                          JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
        } else {
            showCuteMessage("😢 Registration Failed", 
                          "Username already exists or invalid data! 🌸", 
                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCuteMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // Rounded Panel Class
    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Subtle shadow effect
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }
}