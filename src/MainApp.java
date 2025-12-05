import java.awt.*;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║               ✨ TODO LIST APPLICATION 💖                ║");
        System.out.println("║              Made with CIWI-CIWI Cuteness!                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show Splash Screen
        SwingUtilities.invokeLater(() -> {
            showSplashScreen();
            
            // Launch Login GUI after splash
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    SwingUtilities.invokeLater(() -> {
                        LoginGUI loginGUI = new LoginGUI();
                        loginGUI.setVisible(true);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(600, 400);
        splash.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0xFFE5F0),
                    0, getHeight(), new Color(0xFFB3D9)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.fillOval(-50, -50, 250, 250);
                g2d.fillOval(getWidth() - 200, getHeight() - 200, 250, 250);
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE875A0), 4, true));

        JLabel title = new JLabel("✨ Todo List 💖");
        title.setFont(new Font("Segoe UI", Font.BOLD, 64));
        title.setForeground(new Color(0xE875A0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("🌸 Organize Your Day With Plan! 🌸");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        subtitle.setForeground(new Color(0xFF85B3));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loading = new JLabel("", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loading.setForeground(new Color(0xE875A0));
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(loading);
        panel.add(Box.createVerticalGlue());

        splash.setContentPane(panel);
        splash.setVisible(true);

        // Loading Animation
        new Thread(() -> {
            String[] frames = {"💖", "✨", "🌸", "💕", "⭐", "🌺", "💝", "🎀"};
            for (int i = 0; i < 30; i++) {
                final String frame = frames[i % frames.length];
                SwingUtilities.invokeLater(() -> 
                    loading.setText("Loading " + frame)
                );
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            splash.dispose();
        }).start();
    }
}