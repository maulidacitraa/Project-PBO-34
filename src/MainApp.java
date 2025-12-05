import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                 TODO LIST APPLICATION                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Demo 1: TodoLoaderThread
        System.out.println("📋 DEMO 1: Todo Loader");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoLoaderThread();
        
        // Demo 2: TodoDeleteThread
        System.out.println("\n🗑️  DEMO 2: Todo Delete");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoDeleteThread();
        
        // Demo 3: TodoUpdateThread
        System.out.println("\n✏️  DEMO 3: Todo Update");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoUpdateThread();
        
        // Demo 4: BatchOperationThread
        System.out.println("\n🔄 DEMO 4: Batch Operation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoBatchOperationThread();
        
        // Demo 5: StatisticsCalculatorThread
        System.out.println("\n📊 DEMO 5: Statistics Calculator");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoStatisticsThread();
        
        // Demo 6: SearchThread
        System.out.println("\n🔍 DEMO 6: Search");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoSearchThread();
        
        // Demo 7: ExportThread
        System.out.println("\n📤 DEMO 7: Export");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        demoExportThread();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║            STARTING GUI APPLICATION...                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        // Jalankan GUI Login sebagai interface utama
        SwingUtilities.invokeLater(() -> {
            // Tampilkan splash screen dengan loading animation
            showSplashScreen();
            
            // Delay 3 detik lalu buka login
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
    
    // Demo 1: TodoLoaderThread
    private static void demoLoaderThread() {
        System.out.println("Loading todos untuk user ID 1...");
        TodoLoaderThread loader = new TodoLoaderThread(1);
        loader.start();
        
        while (loader.isLoading()) {
            System.out.print(".");
            sleep(500);
        }
        System.out.println();
        
        if (loader.getErrorMessage() != null) {
            System.out.println("❌ Error: " + loader.getErrorMessage());
        } else {
            System.out.println("✅ Berhasil load " + loader.getLoadedTodos().size() + " todos");
            loader.getLoadedTodos().forEach(todo -> 
                System.out.println("   • " + todo.getTitle() + " (" + todo.getType() + ")")
            );
        }
    }
    
    // Demo 2: TodoDeleteThread
    private static void demoDeleteThread() {
        System.out.println("Mencoba delete todo dengan ID 999...");
        TodoDeleteThread deleter = new TodoDeleteThread(999);
        deleter.start();
        
        while (!deleter.isFinished()) {
            System.out.print(".");
            sleep(300);
        }
        System.out.println();
        
        if (deleter.getErrorMessage() != null) {
            System.out.println("❌ Error: " + deleter.getErrorMessage());
        } else if (deleter.isSuccess()) {
            System.out.println("✅ Todo berhasil dihapus");
        } else {
            System.out.println("⚠️  Todo tidak ditemukan (normal jika ID 999 tidak ada)");
        }
    }
    
    // Demo 3: TodoUpdateThread
    private static void demoUpdateThread() {
        System.out.println("Mencoba update todo dengan ID 1...");
        TodoUpdateThread updater = new TodoUpdateThread(1, "Updated Task", "HIGH");
        updater.start();
        
        while (!updater.isFinished()) {
            System.out.print(".");
            sleep(300);
        }
        System.out.println();
        
        if (updater.getErrorMessage() != null) {
            System.out.println("❌ Error: " + updater.getErrorMessage());
        } else if (updater.isSuccess()) {
            System.out.println("✅ Todo berhasil diupdate");
        } else {
            System.out.println("⚠️  Todo tidak ditemukan");
        }
    }
    
    // Demo 4: BatchOperationThread
    private static void demoBatchOperationThread() {
        System.out.println("Batch delete untuk IDs: 991, 992, 993...");
        BatchOperationThread batchOp = new BatchOperationThread(
            Arrays.asList(991, 992, 993), 
            "DELETE"
        );
        batchOp.start();
        
        while (!batchOp.isFinished()) {
            System.out.print(".");
            sleep(400);
        }
        System.out.println();
        
        if (batchOp.getErrorMessage() != null) {
            System.out.println("❌ Error: " + batchOp.getErrorMessage());
        } else {
            System.out.println("✅ Batch operation selesai: " + batchOp.getSuccessCount() + " berhasil");
        }
    }
    
    // Demo 5: StatisticsCalculatorThread
    private static void demoStatisticsThread() {
        System.out.println("Menghitung statistik untuk user ID 1...");
        StatisticsCalculatorThread stats = new StatisticsCalculatorThread(1);
        stats.start();
        
        while (!stats.isFinished()) {
            System.out.print(".");
            sleep(400);
        }
        System.out.println();
        
        if (stats.getErrorMessage() != null) {
            System.out.println("❌ Error: " + stats.getErrorMessage());
        } else {
            System.out.println("✅ Statistik berhasil dihitung:");
            System.out.println(stats.getSummary());
        }
    }
    
    // Demo 6: SearchThread
    private static void demoSearchThread() {
        System.out.println("Searching todos dengan keyword 'task'...");
        SearchThread searcher = new SearchThread(1, "task");
        searcher.start();
        
        while (!searcher.isFinished()) {
            System.out.print(".");
            sleep(400);
        }
        System.out.println();
        
        if (searcher.getErrorMessage() != null) {
            System.out.println("❌ Error: " + searcher.getErrorMessage());
        } else {
            System.out.println("✅ Ditemukan " + searcher.getSearchResults().size() + " hasil");
            searcher.getSearchResults().forEach(todo ->
                System.out.println("   • " + todo.getTitle())
            );
        }
    }
    
    // Demo 7: ExportThread
    private static void demoExportThread() {
        System.out.println("Export todos ke format CSV...");
        ExportThread exporter = new ExportThread(1, "CSV");
        exporter.start();
        
        while (!exporter.isFinished()) {
            System.out.print(".");
            sleep(400);
        }
        System.out.println();
        
        if (exporter.getErrorMessage() != null) {
            System.out.println("❌ Error: " + exporter.getErrorMessage());
        } else {
            System.out.println("✅ Export berhasil ke file: " + exporter.getOutputFilePath());
        }
    }
    
    // Helper method untuk sleep
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Splash screen dengan animasi
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(500, 350);
        splash.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x4682A9));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x3B6E8A), 3));
        
        JLabel title = new JLabel("Let's Do!!", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 56));
        
        JLabel subtitle = new JLabel("Todo List Application", SwingConstants.CENTER);
        subtitle.setForeground(new Color(0xC4E1E6));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        JLabel loading = new JLabel("", SwingConstants.CENTER);
        loading.setForeground(Color.WHITE);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(loading);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        splash.setContentPane(panel);
        splash.setVisible(true);
        
        // Animasi loading dengan thread
        new Thread(() -> {
            String[] frames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
            for (int i = 0; i < 30; i++) {
                int index = i % frames.length;
                SwingUtilities.invokeLater(() -> 
                    loading.setText("Loading " + frames[index])
                );
                sleep(100);
            }
            splash.dispose();
        }).start();
    }
}