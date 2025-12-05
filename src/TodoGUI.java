import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import com.toedter.calendar.JDateChooser;

public class TodoGUI extends JFrame {
    // Pink Color Palette 
    private final Color PINK_PRIMARY = new Color(0xFFB3D9);
    private final Color PINK_SECONDARY = new Color(0xFFE5F0);
    private final Color PINK_ACCENT = new Color(0xFF85B3);
    private final Color PINK_DARK = new Color(0xE875A0);
    private final Color PINK_LIGHT = new Color(0xFFF0F7);
    private final Color WHITE = Color.WHITE;

    private JPanel todoListPanel;  
    private final List<ToDo> todos;
    private final User loggedInUser;
    private final ToDoManager manager;

    private String activeFilter = "ALL";
    private Priority priorityFilter = null;

    private JLabel statsLabel;

    public TodoGUI(User user) {
        this.loggedInUser = user;
        this.todos = new ArrayList<>();
        this.manager = new ToDoManager();

        setTitle("✨ Todo List 💖");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main Panel with gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, PINK_LIGHT,
                    0, getHeight(), PINK_SECONDARY
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        // Top Bar
        mainPanel.add(createTopBar(), BorderLayout.NORTH);

        // Sidebar
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        // Center Content
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);

        // Floating Add Button
        JButton fabButton = createFAB();
        mainPanel.add(fabButton, BorderLayout.SOUTH);

        // Load todos
        loadTodos();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PINK_PRIMARY);
        topBar.setPreferredSize(new Dimension(1400, 80));
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Left: Title
        JLabel titleLabel = new JLabel("✨ My Todo List 💖");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PINK_DARK);
        topBar.add(titleLabel, BorderLayout.WEST);

        // Right: User info & buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        statsLabel = new JLabel("📊 Loading...");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(WHITE);
        rightPanel.add(statsLabel);

        JButton statsButton = createTopButton("📊 Stats", PINK_ACCENT);
        statsButton.addActionListener(e -> showStatistics());
        rightPanel.add(statsButton);

        JButton refreshButton = createTopButton("🔄 Refresh", PINK_ACCENT);
        refreshButton.addActionListener(e -> loadTodos());
        rightPanel.add(refreshButton);

        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JButton createTopButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        
        return btn;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(250, 820));
        sidebar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 3, PINK_PRIMARY),
            new EmptyBorder(20, 15, 20, 15)
        ));

        // Filter Section
        addSidebarTitle(sidebar, "🔍 FILTERS");
        
        addFilterButton(sidebar, "ALL", "🌟 All Todos", null);
        addFilterButton(sidebar, "ACTIVITY", "🎯 Activities", null);
        addFilterButton(sidebar, "EVENT", "📅 Events", null);
        addFilterButton(sidebar, "TASK", "✏️ Tasks", null);
        addFilterButton(sidebar, "COMPLETED", "✅ Completed", null);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Priority Section
        addSidebarTitle(sidebar, "🎨 PRIORITY");
        
        addFilterButton(sidebar, "PRIORITY_HIGH", "🔴 High", Priority.HIGH);
        addFilterButton(sidebar, "PRIORITY_MEDIUM", "🟡 Medium", Priority.MEDIUM);
        addFilterButton(sidebar, "PRIORITY_LOW", "🟢 Low", Priority.LOW);

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void addSidebarTitle(JPanel sidebar, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(PINK_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(label);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addFilterButton(JPanel sidebar, String filter, String text, Priority priority) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean isActive = filter.startsWith("PRIORITY_") 
                    ? (filter.equals("PRIORITY_ALL") && priorityFilter == null) || 
                      (priority != null && priority == priorityFilter)
                    : activeFilter.equals(filter);
                
                if (isActive) {
                    g2d.setColor(PINK_PRIMARY);
                } else if (getModel().isRollover()) {
                    g2d.setColor(PINK_SECONDARY);
                } else {
                    g2d.setColor(PINK_LIGHT);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(PINK_DARK);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btn.addActionListener(e -> {
            if (filter.startsWith("PRIORITY_")) {
                priorityFilter = priority;
            } else {
                activeFilter = filter;
            }
            refreshList();
        });
        
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search Bar
        centerPanel.add(createSearchBar(), BorderLayout.NORTH);

        // Todo List
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createSearchBar() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(8, 15, 8, 15)
        ));
        searchField.setBackground(WHITE);

        JButton searchBtn = createCuteButton("🔍 Search", PINK_ACCENT);
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                performSearch(keyword);
            } else {
                loadTodos();
            }
        });

        JButton clearBtn = createCuteButton("✨ Clear", PINK_PRIMARY);
        clearBtn.setPreferredSize(new Dimension(100, 40));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadTodos();
        });

        searchPanel.add(new JLabel("🔎"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        return searchPanel;
    }

    private JButton createFAB() {
        JButton fab = new JButton("➕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(4, 4, getWidth() - 4, getHeight() - 4);
                
                // Button
                if (getModel().isPressed()) {
                    g2d.setColor(PINK_DARK.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(PINK_DARK.brighter());
                } else {
                    g2d.setColor(PINK_DARK);
                }
                
                g2d.fillOval(0, 0, getWidth() - 4, getHeight() - 4);
                
                super.paintComponent(g);
            }
        };
        
        fab.setFont(new Font("Segoe UI", Font.BOLD, 36));
        fab.setForeground(WHITE);
        fab.setBounds(1280, 780, 80, 80);
        fab.setContentAreaFilled(false);
        fab.setBorderPainted(false);
        fab.setFocusPainted(false);
        fab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fab.addActionListener(e -> showAddDialog());
        
        return fab;
    }

    private JButton createCuteButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private void loadTodos() {
        todoListPanel.removeAll();
        
        JPanel loadingPanel = new JPanel();
        loadingPanel.setOpaque(false);
        loadingPanel.add(new JLabel("⏳ Loading your todos..."));
        todoListPanel.add(loadingPanel);
        todoListPanel.revalidate();
        todoListPanel.repaint();
        
        TodoLoaderThread loader = new TodoLoaderThread(loggedInUser.getId());
        loader.start();
        
        new Thread(() -> {
            try {
                loader.join();
                
                SwingUtilities.invokeLater(() -> {
                    if (loader.getErrorMessage() != null) {
                        showMessage("❌ Error", "Failed to load todos: " + loader.getErrorMessage(), 
                                  JOptionPane.ERROR_MESSAGE);
                        todos.clear();
                    } else {
                        todos.clear();
                        todos.addAll(loader.getLoadedTodos());
                        updateStats();
                    }
                    refreshList();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void performSearch(String keyword) {
        todoListPanel.removeAll();
        
        JPanel searchingPanel = new JPanel();
        searchingPanel.setOpaque(false);
        searchingPanel.add(new JLabel("🔍 Searching for '" + keyword + "'..."));
        todoListPanel.add(searchingPanel);
        todoListPanel.revalidate();
        todoListPanel.repaint();
        
        SearchThread searcher = new SearchThread(loggedInUser.getId(), keyword);
        searcher.start();
        
        new Thread(() -> {
            try {
                searcher.join();
                
                SwingUtilities.invokeLater(() -> {
                    if (searcher.getErrorMessage() != null) {
                        showMessage("❌ Error", "Search failed: " + searcher.getErrorMessage(), 
                                  JOptionPane.ERROR_MESSAGE);
                    } else {
                        todos.clear();
                        todos.addAll(searcher.getSearchResults());
                        
                        if (todos.isEmpty()) {
                            showMessage("😢 Not Found", 
                                      "No todos found with keyword: " + keyword, 
                                      JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    refreshList();
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showStatistics() {
        StatisticsThread stats = new StatisticsThread(loggedInUser.getId());
        stats.start();
        
        JDialog loadingDialog = new JDialog(this, "📊 Statistics", true);
        loadingDialog.setSize(400, 150);
        loadingDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PINK_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel loadingLabel = new JLabel("📊 Calculating statistics... 💖", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(loadingLabel);
        
        loadingDialog.setContentPane(panel);
        
        new Thread(() -> {
            try {
                stats.join();
                
                SwingUtilities.invokeLater(() -> {
                    loadingDialog.dispose();
                    
                    if (stats.getErrorMessage() != null) {
                        showMessage("❌ Error", stats.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
                    } else {
                        showMessage("📊 Todo Statistics", stats.getSummary(), 
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

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "✨ Add New Todo 💖", true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PINK_LIGHT);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Title Field
        mainPanel.add(createLabel("✏️ Title:"));
        JTextField titleField = createTextField();
        mainPanel.add(titleField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description Field
        mainPanel.add(createLabel("📝 Description:"));
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 80));
        mainPanel.add(descScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Date Picker
        mainPanel.add(createLabel("📅 Due Date:"));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        dateChooser.setPreferredSize(new Dimension(400, 40));
        mainPanel.add(dateChooser);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Priority ComboBox
        mainPanel.add(createLabel("🎨 Priority:"));
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priorityBox.setPreferredSize(new Dimension(400, 40));
        mainPanel.add(priorityBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Type ComboBox
        mainPanel.add(createLabel("🌟 Type:"));
        String[] types = {"🎯 Activity", "📅 Event", "✏️ Task"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeBox.setPreferredSize(new Dimension(400, 40));
        mainPanel.add(typeBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Extra Fields Panel
        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new BoxLayout(extraPanel, BoxLayout.Y_AXIS));
        extraPanel.setOpaque(false);
        
        JLabel extraLabel = createLabel("");
        JTextField extraField = createTextField();
        JCheckBox importantCheck = new JCheckBox("⭐ Mark as Important");
        importantCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        importantCheck.setOpaque(false);
        
        extraPanel.add(extraLabel);
        extraPanel.add(extraField);
        extraPanel.add(importantCheck);
        
        extraLabel.setVisible(false);
        extraField.setVisible(false);
        importantCheck.setVisible(false);

        typeBox.addActionListener(e -> {
            int idx = typeBox.getSelectedIndex();
            if (idx == 0) { // Activity
                extraLabel.setText("📍 Location:");
                extraLabel.setVisible(true);
                extraField.setVisible(true);
                importantCheck.setVisible(false);
            } else if (idx == 1) { // Event
                extraLabel.setText("⏱️ Duration (hours):");
                extraLabel.setVisible(true);
                extraField.setVisible(true);
                importantCheck.setVisible(false);
            } else { // Task
                extraLabel.setVisible(false);
                extraField.setVisible(false);
                importantCheck.setVisible(true);
            }
            dialog.revalidate();
        });

        mainPanel.add(extraPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton saveBtn = createCuteButton("💾 Save", PINK_DARK);
        saveBtn.setPreferredSize(new Dimension(150, 45));
        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                showMessage("⚠️ Oops!", "Please enter a title! 🌸", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate date = dateChooser.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                Priority priority = (Priority) priorityBox.getSelectedItem();
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                
                ToDo newTodo = null;
                int typeIdx = typeBox.getSelectedIndex();
                
                // Activity
                if (typeIdx == 0) { 
                    String location = extraField.getText().trim();
                    if (location.isEmpty()) location = "Unknown";
                    newTodo = new ActivityToDo(title, desc, date, priority, location);
                // Event
                } else if (typeIdx == 1) { 
                    int duration = 1;
                    try {
                        duration = Integer.parseInt(extraField.getText().trim());
                    } catch (NumberFormatException ex) {
                        duration = 1;
                    }
                    newTodo = new EventToDo(title, desc, date, priority, duration);
                // Task
                } else { 
                    newTodo = new TaskToDo(title, desc, date, priority);
                    ((TaskToDo) newTodo).setImportant(importantCheck.isSelected());
                }
                
                if (manager.saveToDo(newTodo, loggedInUser.getId())) {
                    showMessage("🎉 Success!", "Todo added successfully! 💖", 
                              JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTodos();
                } else {
                    showMessage("❌ Error", "Failed to save todo! 😢", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                showMessage("❌ Error", "Invalid data: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = createCuteButton("❌ Cancel", PINK_ACCENT);
        cancelBtn.setPreferredSize(new Dimension(150, 45));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(PINK_DARK);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(400, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void refreshList() {
        todoListPanel.removeAll();

        List<ToDo> filteredTodos = todos.stream()
            .filter(this::matchesFilter)
            .toList();

        if (filteredTodos.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("🌸 No todos here! Add some tasks! 💖");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            emptyLabel.setForeground(PINK_ACCENT);
            emptyPanel.add(emptyLabel);
            todoListPanel.add(emptyPanel);
        } else {
            for (ToDo todo : filteredTodos) {
                todoListPanel.add(createTodoCard(todo));
                todoListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    private boolean matchesFilter(ToDo todo) {
        boolean typeMatch = switch (activeFilter) {
            case "ALL" -> true;
            case "COMPLETED" -> todo.isCompleted();
            case "ACTIVE" -> !todo.isCompleted();
            default -> todo.getType().equals(activeFilter);
        };

        boolean priorityMatch = priorityFilter == null || todo.getPriority() == priorityFilter;

        return typeMatch && priorityMatch;
    }

    private JPanel createTodoCard(ToDo todo) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Card background
                if (todo.isCompleted()) {
                    g2d.setColor(new Color(240, 240, 240));
                } else if (todo.isOverdue()) {
                    g2d.setColor(new Color(255, 230, 230));
                } else if (todo.isDueToday()) {
                    g2d.setColor(new Color(255, 250, 220));
                } else {
                    g2d.setColor(WHITE);
                }
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Border
                g2d.setColor(PINK_ACCENT);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
            }
        };

        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setPreferredSize(new Dimension(1000, 140));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Left: Type Icon
        JLabel iconLabel = new JLabel(todo.getTypeEmoji());
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        card.add(iconLabel, BorderLayout.WEST);

        // Center: Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(todo.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(todo.isCompleted() ? Color.GRAY : PINK_DARK);
        if (todo.isCompleted()) {
            titleLabel.setText("<html><strike>" + todo.getTitle() + "</strike></html>");
        }
        contentPanel.add(titleLabel);

        // Description
        JLabel descLabel = new JLabel(todo.getDescription());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        contentPanel.add(descLabel);

        // Extra Info
        JLabel extraLabel = new JLabel(todo.getExtraInfo());
        extraLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        extraLabel.setForeground(PINK_ACCENT);
        contentPanel.add(extraLabel);

        // Date & Status
        String dateText = "📅 " + todo.getFormattedDate();
        if (todo.isOverdue()) {
            dateText += " ⚠️ OVERDUE";
        } else if (todo.isDueToday()) {
            dateText += " 🔔 TODAY";
        }
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(todo.isOverdue() ? Color.RED : Color.DARK_GRAY);
        contentPanel.add(dateLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Right: Priority Badge & Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        // Priority Badge
        JLabel priorityBadge = new JLabel(todo.getPriority().toString());
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priorityBadge.setForeground(WHITE);
        priorityBadge.setOpaque(true);
        priorityBadge.setBackground(Color.decode(todo.getPriority().getColor()));
        priorityBadge.setBorder(new EmptyBorder(5, 12, 5, 12));
        priorityBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(priorityBadge);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setMaximumSize(new Dimension(120, 60));

        JButton editBtn = createSmallButton("✏️");
        editBtn.setToolTipText("Edit");
        editBtn.addActionListener(e -> showEditDialog(todo));

        JButton deleteBtn = createSmallButton("🗑️");
        deleteBtn.setToolTipText("Delete");
        deleteBtn.addActionListener(e -> deleteTodo(todo));

        JButton completeBtn = createSmallButton(todo.isCompleted() ? "↩️" : "✅");
        completeBtn.setToolTipText(todo.isCompleted() ? "Mark Incomplete" : "Mark Complete");
        completeBtn.addActionListener(e -> toggleComplete(todo));

        JButton infoBtn = createSmallButton("ℹ️");
        infoBtn.setToolTipText("Details");
        infoBtn.addActionListener(e -> showDetails(todo));

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(completeBtn);
        buttonsPanel.add(infoBtn);

        rightPanel.add(buttonsPanel);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btn.setBackground(PINK_SECONDARY);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PINK_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PINK_SECONDARY);
            }
        });
        
        return btn;
    }

    private void showEditDialog(ToDo todo) {
        // showAddDialog pre-filled values
        JDialog dialog = new JDialog(this, "✏️ Edit Todo", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PINK_LIGHT);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        mainPanel.add(createLabel("✏️ Title:"));
        JTextField titleField = createTextField();
        titleField.setText(todo.getTitle());
        mainPanel.add(titleField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createLabel("📝 Description:"));
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setText(todo.getDescription());
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_ACCENT, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 80));
        mainPanel.add(descScroll);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createLabel("📅 Due Date:"));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(todo.getDate()));
        dateChooser.setPreferredSize(new Dimension(400, 40));
        mainPanel.add(dateChooser);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createLabel("🎨 Priority:"));
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(todo.getPriority());
        priorityBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priorityBox.setPreferredSize(new Dimension(400, 40));
        mainPanel.add(priorityBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton saveBtn = createCuteButton("💾 Save", PINK_DARK);
        saveBtn.setPreferredSize(new Dimension(150, 45));
        saveBtn.addActionListener(e -> {
            todo.setTitle(titleField.getText().trim());
            todo.setDescription(descArea.getText().trim());
            todo.setDate(dateChooser.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            todo.setPriority((Priority) priorityBox.getSelectedItem());
            
            if (manager.updateToDo(todo)) {
                showMessage("🎉 Success!", "Todo updated! 💖", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshList();
            } else {
                showMessage("❌ Error", "Failed to update! 😢", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = createCuteButton("❌ Cancel", PINK_ACCENT);
        cancelBtn.setPreferredSize(new Dimension(150, 45));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private void deleteTodo(ToDo todo) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this todo? 🗑️\n" + todo.getTitle(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.deleteToDo(todo.getId())) {
                showMessage("✅ Deleted", "Todo deleted successfully! 💔", 
                          JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
            } else {
                showMessage("❌ Error", "Failed to delete! 😢", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleComplete(ToDo todo) {
        todo.setCompleted(!todo.isCompleted());
        if (manager.updateToDo(todo)) {
            refreshList();
            updateStats();
        } else {
            showMessage("❌ Error", "Failed to update! 😢", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetails(ToDo todo) {
        String details = String.format(
            "%s %s\n\n" +
            "Title: %s\n" +
            "Description: %s\n" +
            "Date: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n" +
            "%s",
            todo.getTypeEmoji(), todo.getType(),
            todo.getTitle(),
            todo.getDescription(),
            todo.getFormattedDate(),
            todo.getPriority(),
            todo.isCompleted() ? "✅ Completed" : "⏳ Active",
            todo.getExtraInfo()
        );
        
        showMessage("ℹ️ Todo Details", details, JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStats() {
        long active = todos.stream().filter(t -> !t.isCompleted()).count();
        long completed = todos.stream().filter(ToDo::isCompleted).count();
        long overdue = todos.stream().filter(ToDo::isOverdue).count();
        
        statsLabel.setText(String.format("📊 %d Active | ✅ %d Completed | ⚠️ %d Overdue", 
                                        active, completed, overdue));
    }

    private void showMessage(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }
}
