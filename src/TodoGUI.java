import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TodoGUI extends JFrame {

    private final JPanel taskListPanel;
    private final java.util.List<ToDo> todos = new ArrayList<>();
    
    private final User loggedInUser; 
    private final String currentUsername;

    private String activeFilter = "All";
    private String priorityFilter = "AllPriority";

    private final Color PRIMARY = new Color(0x4682A9);
    private final Color SECONDARY = new Color(0xC4E1E6);
    private final Color CARD = new Color(0x3B6E8A);

    public TodoGUI(User user) {
        this.loggedInUser = user;
        this.currentUsername = user.getUsername();

        setTitle("Let's Do!!");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel base = new JPanel(null);
        base.setBackground(SECONDARY);
        setContentPane(base);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBounds(0, 0, 1200, 60);

        JLabel title = new JLabel("Let's Do!!", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        topBar.add(title, BorderLayout.CENTER);
        
        // Statistics button
        JButton btnStats = new JButton("📊 Stats");
        btnStats.setBackground(PRIMARY.darker());
        btnStats.setForeground(Color.WHITE);
        btnStats.setFocusPainted(false);
        btnStats.addActionListener(e -> showStatistics());
        topBar.add(btnStats, BorderLayout.EAST);

        base.add(topBar);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CARD);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBounds(0, 60, 180, 740);

        JLabel userLabel = new JLabel("User: " + currentUsername);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
        sidebar.add(userLabel);

        JLabel filterLabel = new JLabel("FILTER TYPE");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
        sidebar.add(filterLabel);

        addFilterButton(sidebar, "All");
        addFilterButton(sidebar, "ActivityTodo");
        addFilterButton(sidebar, "EventTodo");
        addFilterButton(sidebar, "TaskTodo");
        addFilterButton(sidebar, "Completed");
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Export buttons
        JButton btnExportCSV = new JButton("📤 Export CSV");
        btnExportCSV.setBackground(CARD.darker());
        btnExportCSV.setForeground(Color.WHITE);
        btnExportCSV.setFocusPainted(false);
        btnExportCSV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnExportCSV.addActionListener(e -> exportTodos("CSV"));
        sidebar.add(btnExportCSV);
        
        JButton btnExportTXT = new JButton("📝 Export TXT");
        btnExportTXT.setBackground(CARD.darker());
        btnExportTXT.setForeground(Color.WHITE);
        btnExportTXT.setFocusPainted(false);
        btnExportTXT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnExportTXT.addActionListener(e -> exportTodos("TXT"));
        sidebar.add(btnExportTXT);

        base.add(sidebar);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);
        mainPanel.setBounds(190, 70, 980, 670);
        base.add(mainPanel);

        // PRIORITY FILTER BAR
        JPanel priorityBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        priorityBar.setBackground(PRIMARY);
        priorityBar.setBounds(0, 0, 980, 45);

        JLabel prioLabel = new JLabel("Priority:");
        prioLabel.setForeground(Color.WHITE);

        String[] prOptions = {"AllPriority", "Low", "Medium", "High"};
        JComboBox<String> prioFilter = new JComboBox<>(prOptions);
        prioFilter.addActionListener(e -> {
            priorityFilter = prioFilter.getSelectedItem().toString();
            refreshList();
        });

        priorityBar.add(prioLabel);
        priorityBar.add(prioFilter);
        
        // Search field
        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton("🔍 Search");
        btnSearch.setBackground(PRIMARY.brighter());
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> performSearch(searchField.getText()));
        
        priorityBar.add(Box.createRigidArea(new Dimension(20, 0)));
        priorityBar.add(new JLabel("Search:"));
        priorityBar.add(searchField);
        priorityBar.add(btnSearch);
        
        // Refresh button
        JButton btnRefresh = new JButton("🔄");
        btnRefresh.setBackground(PRIMARY.brighter());
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setToolTipText("Refresh from database");
        btnRefresh.addActionListener(e -> loadTodosFromDatabase());
        priorityBar.add(btnRefresh);
        
        mainPanel.add(priorityBar);

        // TASK LIST AREA
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(taskListPanel);
        scroll.setBounds(0, 50, 980, 620);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        mainPanel.add(scroll);

        // FLOATING ADD BUTTON
        JButton btnAdd = new JButton("+");
        btnAdd.setBounds(1110, 610, 60, 60);
        btnAdd.setBackground(PRIMARY.brighter());
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 30));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(null);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showAddTaskDialog());
        base.add(btnAdd);
        base.setComponentZOrder(btnAdd, 0);

        // Load data dari database menggunakan thread
        loadTodosFromDatabase();
    }

    // Load todos menggunakan TodoLoaderThread
    private void loadTodosFromDatabase() {
        taskListPanel.removeAll();
        JLabel loading = new JLabel("⏳ Loading todos...", SwingConstants.CENTER);
        loading.setForeground(Color.DARK_GRAY);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taskListPanel.add(loading);
        taskListPanel.revalidate();
        taskListPanel.repaint();
        
        TodoLoaderThread loader = new TodoLoaderThread(loggedInUser.getId());
        loader.start();
        
        new Thread(() -> {
            try {
                loader.join();
                
                SwingUtilities.invokeLater(() -> {
                    if (loader.getErrorMessage() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Error loading todos: " + loader.getErrorMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        todos.clear();
                    } else {
                        todos.clear();
                        todos.addAll(loader.getLoadedTodos());
                    }
                    refreshList();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Search menggunakan SearchThread
    private void performSearch(String keyword) {
        if (keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan keyword pencarian!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        taskListPanel.removeAll();
        JLabel searching = new JLabel("🔍 Searching for '" + keyword + "'...", SwingConstants.CENTER);
        searching.setForeground(Color.DARK_GRAY);
        searching.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taskListPanel.add(searching);
        taskListPanel.revalidate();
        taskListPanel.repaint();
        
        SearchThread searcher = new SearchThread(loggedInUser.getId(), keyword);
        searcher.start();
        
        new Thread(() -> {
            try {
                searcher.join();
                
                SwingUtilities.invokeLater(() -> {
                    if (searcher.getErrorMessage() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Search error: " + searcher.getErrorMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        todos.clear();
                        todos.addAll(searcher.getSearchResults());
                        refreshList();
                        
                        if (todos.isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Tidak ditemukan hasil untuk: " + keyword,
                                    "Search Result",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Statistics menggunakan StatisticsCalculatorThread
    private void showStatistics() {
        StatisticsCalculatorThread stats = new StatisticsCalculatorThread(loggedInUser.getId());
        stats.start();
        
        JDialog loadingDialog = new JDialog(this, "Loading Statistics", true);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        
        JLabel loadingLabel = new JLabel("📊 Calculating statistics...", SwingConstants.CENTER);
        loadingDialog.add(loadingLabel);
        
        new Thread(() -> {
            try {
                stats.join();
                
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    
                    if (stats.getErrorMessage() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Error: " + stats.getErrorMessage(),
                                "Statistics Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                stats.getSummary(),
                                "Todo Statistics",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        new Thread(() -> {
            try {
                Thread.sleep(500);
                if (stats.isFinished()) {
                    SwingUtilities.invokeLater(() -> loadingDialog.dispose());
                } else {
                    SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Export menggunakan ExportThread
    private void exportTodos(String format) {
        ExportThread exporter = new ExportThread(loggedInUser.getId(), format);
        exporter.start();
        
        JDialog exportDialog = new JDialog(this, "Exporting", true);
        exportDialog.setSize(350, 100);
        exportDialog.setLocationRelativeTo(this);
        
        JLabel exportLabel = new JLabel("📤 Exporting to " + format + "...", SwingConstants.CENTER);
        exportDialog.add(exportLabel);
        
        new Thread(() -> {
            try {
                exporter.join();
                
                SwingUtilities.invokeLater(() -> {
                    exportDialog.dispose();
                    
                    if (exporter.getErrorMessage() != null) {
                        JOptionPane.showMessageDialog(this,
                                "Export error: " + exporter.getErrorMessage(),
                                "Export Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Export berhasil!\nFile: " + exporter.getOutputFilePath(),
                                "Export Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        new Thread(() -> {
            try {
                Thread.sleep(300);
                if (exporter.isFinished()) {
                    SwingUtilities.invokeLater(() -> exportDialog.dispose());
                } else {
                    SwingUtilities.invokeLater(() -> exportDialog.setVisible(true));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addFilterButton(JPanel sidebar, String name) {
        JButton btn = new JButton(name);
        btn.setBackground(CARD.darker());
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        btn.addActionListener(e -> {
            activeFilter = name;
            refreshList();
        });

        sidebar.add(btn);
    }

    private void showAddTaskDialog() {
        JTextField titleField = new JTextField();

        String[] types = {"ActivityTodo", "EventTodo", "TaskTodo"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        
        JTextField detailField = new JTextField();
        JLabel detailLabel = new JLabel("Location/Duration:");
        detailField.setVisible(false);
        detailLabel.setVisible(false);

        typeBox.addActionListener(e -> {
            String selected = (String) typeBox.getSelectedItem();
            if ("ActivityTodo".equals(selected)) {
                detailLabel.setText("Location:");
                detailField.setVisible(true);
                detailLabel.setVisible(true);
            } else if ("EventTodo".equals(selected)) {
                detailLabel.setText("Duration (Hours):");
                detailField.setVisible(true);
                detailLabel.setVisible(true);
            } else {
                detailField.setVisible(false);
                detailLabel.setVisible(false);
            }
        });

        Object[] form = {
                "Task name:", titleField,
                "Type:", typeBox,
                "Priority:", priorityBox,
                detailLabel, detailField
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add Task", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && !titleField.getText().trim().isEmpty()) {

            ToDo newTodo;
            Priority prio = Priority.valueOf(priorityBox.getSelectedItem().toString().toUpperCase());
            String detail = detailField.getText().trim();

            switch (typeBox.getSelectedItem().toString()) {
                case "ActivityTodo":
                    String location = detail.isEmpty() ? "Unknown" : detail;
                    newTodo = new ActivityToDo(titleField.getText(), "Description", LocalDate.now(), prio, location);
                    break;
                case "EventTodo":
                    int duration = 1;
                    try {
                        duration = Integer.parseInt(detail);
                    } catch (NumberFormatException ignored) {}
                    newTodo = new EventToDo(titleField.getText(), "Description", LocalDate.now(), prio, duration);
                    break;
                default:
                    newTodo = new TaskToDo(titleField.getText(), "Description", LocalDate.now(), prio);
            }

            ToDoManager manager = new ToDoManager(); 
            int userId = loggedInUser.getId(); 

            if (manager.saveToDo(newTodo, userId)) { 
                JOptionPane.showMessageDialog(this, "To-Do berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTodosFromDatabase(); // Reload dari database
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan To-Do.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog(ToDo todo) {
        JTextField titleField = new JTextField(todo.getTitle());

        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> prioBox = new JComboBox<>(priorities);
        prioBox.setSelectedItem(todo.getPriority());

        Object[] form = {
                "Task name:", titleField,
                "Priority:", prioBox
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Task", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            String newPriority = prioBox.getSelectedItem().toString();
            
            // Update menggunakan TodoUpdateThread (simulasi, perlu ID dari database)
            todo.setTitle(newTitle);
            todo.setPriority(newPriority);
            refreshList();
        }
    }

    private boolean shouldShow(ToDo t) {
        boolean typeMatch =
                activeFilter.equals("All") ||
                        (activeFilter.equals("Completed") && t.isCompleted()) ||
                        t.getType().equals(activeFilter);

        boolean prioMatch =
                priorityFilter.equals("AllPriority") ||
                        t.getPriority().equals(priorityFilter);

        return typeMatch && prioMatch;
    }

    private JPanel createTaskCard(ToDo todo) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel(todo.getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel small = new JLabel(todo.getType() + " • Priority: " + todo.getPriority());
        small.setForeground(new Color(220, 220, 220));

        JLabel desc = new JLabel(todo.getDescription());
        desc.setForeground(new Color(210, 210, 210));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(small);
        left.add(desc);

        JPanel right = new JPanel();
        right.setOpaque(false);

        JLabel badge = new JLabel(todo.getPriority());
        badge.setOpaque(true);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        switch (todo.getPriority()) {
            case "Low": badge.setBackground(new Color(85,214,120)); break;
            case "Medium": badge.setBackground(new Color(255,208,70)); break;
            case "High": badge.setBackground(new Color(255,92,92)); break;
        }

        JButton edit = new JButton("edit");
        JButton done = new JButton("✓");

        edit.addActionListener(e -> showEditDialog(todo));
        done.addActionListener(e -> {
            todo.setCompleted(true);
            refreshList();
        });

        right.add(badge);
        right.add(edit);
        right.add(done);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private void refreshList() {
        taskListPanel.removeAll();

        for (ToDo t : todos) {
            if (shouldShow(t)) {
                taskListPanel.add(createTaskCard(t));
                taskListPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }
}