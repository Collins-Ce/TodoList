package ui;

import model.User;
import model.Task;
import service.TaskService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Dashboard extends JFrame {

    private User user;
    private TaskService taskService;
    private JTable taskTable;
    private DefaultTableModel tableModel;

    private List<Task> tasks;

    public Dashboard(User user) {

        this.user = user;
        this.taskService = new TaskService();

        setTitle("To-Do List Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        loadTasks();
    }

    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + user.getUsername() + "!"
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> logout());

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // TASK TABLE
        String[] columns = {
            "Task ID",
            "Title",
            "Description",
            "Priority",
            "Due Date",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(28);
        taskTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(taskTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );

        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 15)
        );

        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Toggle Status");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> toggleStatus());
        refreshButton.addActionListener(e -> loadTasks());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // LOAD TASKS FROM DATABASE
    private void loadTasks() {

        try {

            tasks = taskService.getTasks(user.getUserId());

            tableModel.setRowCount(0);

            for (Task task : tasks) {

                tableModel.addRow(new Object[] {
                    task.getTaskId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getDueDate() == null
                        ? "Not set"
                        : task.getDueDate(),
                    task.getStatus()
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to load tasks!\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ADD TASK
    private void addTask() {

        Task task = showTaskDialog(null);

        if (task == null) {
            return;
        }

        try {

            if (taskService.addTask(task)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Task added successfully!"
                );

                loadTasks();
            }

        } catch (SQLException | IllegalArgumentException e) {

            showError(e.getMessage());
        }
    }

    // EDIT TASK
    private void editTask() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a task to edit.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(row, 0);

        Task selectedTask = findTask(taskId);

        if (selectedTask == null) {
            showError("Task not found.");
            return;
        }

        Task updatedTask = showTaskDialog(selectedTask);

        if (updatedTask == null) {
            return;
        }

        try {

            if (taskService.updateTask(updatedTask)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Task updated successfully!"
                );

                loadTasks();
            }

        } catch (SQLException | IllegalArgumentException e) {

            showError(e.getMessage());
        }
    }

    // DELETE TASK
    private void deleteTask() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a task to delete.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this task?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            if (taskService.deleteTask(
                    taskId, user.getUserId())) {

                JOptionPane.showMessageDialog(
                    this,
                    "Task deleted successfully!"
                );

                loadTasks();
            }

        } catch (SQLException e) {

            showError(e.getMessage());
        }
    }

    // MARK TASK COMPLETED OR PENDING
    private void toggleStatus() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a task.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(row, 0);

        Task task = findTask(taskId);

        if (task == null) {
            showError("Task not found.");
            return;
        }

        try {

            boolean success;

            if (task.getStatus().equals("PENDING")) {

                success = taskService.markCompleted(
                    taskId, user.getUserId()
                );

            } else {

                success = taskService.markPending(
                    taskId, user.getUserId()
                );
            }

            if (success) {

                JOptionPane.showMessageDialog(
                    this,
                    "Task status updated!"
                );

                loadTasks();
            }

        } catch (SQLException e) {

            showError(e.getMessage());
        }
    }

    // TASK ADD / EDIT DIALOG
    private Task showTaskDialog(Task existingTask) {

        boolean editing = existingTask != null;

        JTextField titleField = new JTextField(
            editing ? existingTask.getTitle() : ""
        );

        JTextArea descriptionArea = new JTextArea(
            editing ? existingTask.getDescription() : "",
            4,
            20
        );

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JComboBox<String> priorityBox = new JComboBox<>(
            new String[] {"LOW", "MEDIUM", "HIGH"}
        );

        if (editing) {
            priorityBox.setSelectedItem(
                existingTask.getPriority()
            );
        } else {
            priorityBox.setSelectedItem("MEDIUM");
        }

        JTextField dueDateField = new JTextField(
            editing && existingTask.getDueDate() != null
                ? existingTask.getDueDate().toString()
                : ""
        );

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        panel.add(new JLabel("Task Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);

        panel.add(new JLabel("Due Date (YYYY-MM-DD, optional):"));
        panel.add(dueDateField);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            editing ? "Edit Task" : "Add Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {

            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();

            String dateText = dueDateField.getText().trim();

            LocalDate dueDate = dateText.isEmpty()
                ? null
                : LocalDate.parse(dateText);

            if (editing) {

                existingTask.setTitle(title);
                existingTask.setDescription(description);
                existingTask.setPriority(priority);
                existingTask.setDueDate(dueDate);

                return existingTask;
            }

            return new Task(
                user.getUserId(),
                title,
                description,
                priority,
                dueDate
            );

        } catch (Exception e) {

            showError(
                "Invalid input. Please check the due date format.\n" +
                "Use YYYY-MM-DD, for example 2026-11-10."
            );

            return null;
        }
    }

    // FIND TASK BY ID
    private Task findTask(int taskId) {

        if (tasks == null) {
            return null;
        }

        for (Task task : tasks) {

            if (task.getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }

    // LOGOUT
    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // ERROR MESSAGE
    private void showError(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}