import javax.swing.SwingUtilities;

public class MainApp {

    public static void main(String[] args) {
        // Jalankan GUI Login sebagai interface utama
        SwingUtilities.invokeLater(() -> {
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
        });
    }
}
