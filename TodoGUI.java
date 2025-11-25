import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TodoGUI extends JFrame {

    private final JPanel taskListPanel;
    private final java.util.List<ToDo> todos = new ArrayList<>();
    private String activeFilter = "All";

    public TodoGUI() {

        setTitle("Let's Do!!");
        setSize(800, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color BG = new Color(0x1E1E1E);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Let's Do!!");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Poppins", Font.BOLD, 20));

        JButton btnAdd = new JButton("+ Add Task");
        styleButtonPrimary(btnAdd);
        btnAdd.addActionListener(e -> showAddTaskDialog());

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnAdd, BorderLayout.EAST);

        // SIDEBAR FILTER
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0x252525));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, 0));

        JLabel filterLabel = new JLabel("FILTERS");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filterLabel.setFont(new Font("Arial", Font.BOLD, 13));

        sidebar.add(filterLabel);

        addFilterButton(sidebar, "All");
        addFilterButton(sidebar, "ActivityTodo");
        addFilterButton(sidebar, "EventTodo");
        addFilterButton(sidebar, "TaskTodo");
        addFilterButton(sidebar, "Completed");

        sidebar.add(Box.createVerticalGlue());

        // TASK LIST
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(taskListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(sidebar, BorderLayout.WEST);
        root.add(scroll, BorderLayout.CENTER);

        todos.add(new ActivityToDo("Meditate 10 mins", "Mindfulness meditation", LocalDate.now(), Priority.LOW, "Home"));
        todos.add(new EventToDo("Meeting at 3 PM", "Team standup", LocalDate.now(), Priority.HIGH, 1));
        todos.add(new TaskToDo("Finish homework", "Complete Java assignment", LocalDate.now().plusDays(1), Priority.MEDIUM));

        refreshList();
    }

    // SIDEBAR BUTTON
    private void addFilterButton(JPanel sidebar, String name) {
        JButton btn = new JButton(name);
        styleButtonSidebar(btn);
        btn.addActionListener(e -> {
            activeFilter = name;
            refreshList();
        });
        sidebar.add(btn);
    }

    // ADD TASK DIALOG
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

        int result = JOptionPane.showConfirmDialog(this, form, "Add Task",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && !titleField.getText().trim().isEmpty()) {

            String t = titleField.getText().trim();
            String type = typeBox.getSelectedItem().toString();
            String priority = priorityBox.getSelectedItem().toString();

            ToDo newTodo;
            Priority prio = Priority.valueOf(priority.toUpperCase());

            switch (type) {
                case "ActivityTodo":
                    newTodo = new ActivityToDo(t, "Description", LocalDate.now(), prio, "Location");
                    break;
                case "EventTodo":
                    newTodo = new EventToDo(t, "Description", LocalDate.now(), prio, 1);
                    break;
                default:
                    newTodo = new TaskToDo(t, "Description", LocalDate.now(), prio);
            }

            todos.add(newTodo);
            refreshList();
        }
    }

    // EDIT DIALOG
    private void showEditDialog(ToDo todo) {
        JTextField titleField = new JTextField(todo.title);

        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);
        priorityBox.setSelectedItem(todo.getPriority());

        Object[] form = {
                "Task name:", titleField,
                "Priority:", priorityBox
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Task",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            todo.title = titleField.getText().trim();
            todo.setPriority(priorityBox.getSelectedItem().toString());
            refreshList();
        }
    }

    // REFRESH LIST
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

    private boolean shouldShow(ToDo t) {
        if (activeFilter.equals("All")) return true;
        if (activeFilter.equals("Completed")) return t.completed;
        return t.getType().equals(activeFilter) && !t.completed;
    }

    private JPanel createTaskCard(ToDo todo) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(45,45,45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        // LEFT
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(todo.title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel smallInfo = new JLabel(todo.getType() + " • Priority: " + todo.getPriority());
        smallInfo.setForeground(new Color(180,180,180));
        smallInfo.setFont(new Font("Arial", Font.PLAIN, 11));

        infoPanel.add(titleLabel);
        infoPanel.add(smallInfo);

        // PRIORITY BADGE
        JLabel badge = new JLabel(todo.getPriority());
        badge.setOpaque(true);
        badge.setForeground(Color.BLACK);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setFont(new Font("Arial", Font.BOLD, 11));

        switch (todo.getPriority()) {
            case "Low": badge.setBackground(new Color(85, 214, 120)); break;
            case "Medium": badge.setBackground(new Color(255, 208, 70)); break;
            case "High": badge.setBackground(new Color(255, 92, 92)); break;
        }

        // RIGHT BUTTONS
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        JButton btnDone = new JButton("✓");
        styleButtonPrimary(btnDone);
        btnDone.addActionListener(e -> {
            todo.completed = true;
            refreshList();
        });

        JButton btnEdit = new JButton("edit");
        styleButtonPrimary(btnEdit);
        btnEdit.addActionListener(e -> showEditDialog(todo));

        btnPanel.add(badge);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDone);

        card.add(infoPanel, BorderLayout.WEST);
        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    // BUTTON STYLES
    private void styleButtonPrimary(JButton btn) {
        btn.setBackground(new Color(0xFFB6C1));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleButtonSidebar(JButton btn) {
        btn.setBackground(new Color(50,50,50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70,70,70));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50,50,50));
            }
        });
    }
}
