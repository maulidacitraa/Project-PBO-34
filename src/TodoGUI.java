import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TodoGUI extends JFrame {

    private final JPanel taskListPanel;
    private final java.util.List<ToDo> todos = new ArrayList<>();
    private final String currentUsername;

    private String activeFilter = "All";
    private String priorityFilter = "AllPriority";

    // Warna dari LoginGUI
    private final Color PRIMARY = new Color(0x4682A9);     // Top bar
    private final Color SECONDARY = new Color(0xC4E1E6);   // Main background
    private final Color CARD = new Color(0x3B6E8A);        // Cards + Sidebar
    private final Color CREAM = new Color(0xF1F0E8);       // Optional softer bg

    public TodoGUI(String username) {
        this.currentUsername = username;

        setTitle("Let's Do!!");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // BASE FRAME — Absolute Layout
        JPanel base = new JPanel(null);
        base.setBackground(SECONDARY);  // Sama dengan Login
        setContentPane(base);

        // ========================
        // TOP BAR — PRIMARY COLOR
        // ========================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY);
        topBar.setBounds(0, 0, 1200, 60);

        JLabel title = new JLabel("Let's Do!!", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        topBar.add(title, BorderLayout.CENTER);

        base.add(topBar);

        // ========================
        // SIDEBAR — CARD COLOR
        // ========================
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

        base.add(sidebar);

        // ========================
        // MAIN PANEL — LIGHT BG
        // ========================
        JPanel mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);
        mainPanel.setBounds(190, 70, 980, 670);
        base.add(mainPanel);

        // ========================
        // PRIORITY FILTER BAR
        // ========================
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
        mainPanel.add(priorityBar);

        // ========================
        // TASK LIST AREA
        // ========================
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(taskListPanel);
        scroll.setBounds(0, 50, 980, 620);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        mainPanel.add(scroll);

        // ========================
        // FLOATING ADD BUTTON
        // ========================
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

        // SAMPLE DATA
        todos.add(new ActivityToDo("Meditate", "Mindfulness", LocalDate.now(), Priority.LOW, "Home"));
        todos.add(new EventToDo("Meeting", "Team standup", LocalDate.now(), Priority.HIGH, 1));
        todos.add(new TaskToDo("Homework", "Java OOP", LocalDate.now().plusDays(1), Priority.MEDIUM));

        refreshList();
    }

    // ===============================
    // SIDEBAR BUTTONS — WHITE TEXT
    // ===============================
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

    // ADD TASK DIALOG (TIDAK DIUBAH)
    private void showAddTaskDialog() {
        JTextField titleField = new JTextField();

        String[] types = {"ActivityTodo", "EventTodo", "TaskTodo"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);

        Object[] form = {
                "Task name:", titleField,
                "Type:", typeBox,
                "Priority:", priorityBox
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add Task", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && !titleField.getText().trim().isEmpty()) {

            ToDo newTodo;
            Priority prio = Priority.valueOf(priorityBox.getSelectedItem().toString().toUpperCase());

            switch (typeBox.getSelectedItem().toString()) {
                case "ActivityTodo":
                    newTodo = new ActivityToDo(titleField.getText(), "Description", LocalDate.now(), prio, "Home");
                    break;
                case "EventTodo":
                    newTodo = new EventToDo(titleField.getText(), "Description", LocalDate.now(), prio, 1);
                    break;
                default:
                    newTodo = new TaskToDo(titleField.getText(), "Description", LocalDate.now(), prio);
            }

            todos.add(newTodo);
            refreshList();
        }
    }

    // EDIT DIALOG
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
            todo.setTitle(titleField.getText().trim());
            todo.setPriority(prioBox.getSelectedItem().toString());
            refreshList();
        }
    }

    // FILTER LOGIC
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

    // ===============================
    // TASK CARD — MENGGUNAKAN CARD COLOR
    // ===============================
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

    // LIST REFRESH
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
