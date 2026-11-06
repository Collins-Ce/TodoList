package ui;

import model.User;
import model.Task;
import service.TaskService;

import ui.components.TaskTablePanel;
import ui.components.TaskStatsPanel;
import ui.dialog.TaskDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Dashboard extends JFrame {

    private User user;
    private TaskService taskService;

    private TaskTablePanel taskTablePanel;
    private TaskStatsPanel taskStatsPanel;

    private List<Task> tasks;

    public Dashboard(User user) {

        this.user = user;
        this.taskService = new TaskService();

        setTitle("To-Do List Dashboard");
        setSize(1050, 700);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        loadTasks();
    }

    // CREATE DASHBOARD
    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());

        header.setBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + user.getUsername() + "!"
        );

        welcomeLabel.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> logout());

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // STATISTICS
        taskStatsPanel = new TaskStatsPanel();

        // TASK TABLE, SEARCH AND FILTERS
        taskTablePanel = new TaskTablePanel();

        JPanel centerPanel = new JPanel(
            new BorderLayout(10, 10)
        );

        centerPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );

        centerPanel.add(
            taskStatsPanel,
            BorderLayout.NORTH
        );

        centerPanel.add(
            taskTablePanel,
            BorderLayout.CENTER
        );

        add(centerPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 12, 15)
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

    // LOAD TASKS
    private void loadTasks() {

        try {

            tasks = taskService.getTasks(user.getUserId());

            taskStatsPanel.updateStatistics(tasks);
            taskTablePanel.setTasks(tasks);

        } catch (SQLException e) {

            showError(
                "Unable to load tasks!\n" + e.getMessage()
            );
        }
    }

    // ADD TASK
    private void addTask() {

        Task task = TaskDialog.showDialog(
            this,
            null,
            user.getUserId()
        );

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

        int taskId = taskTablePanel.getSelectedTaskId();

        if (taskId == -1) {

            showError("Please select a task to edit.");
            return;
        }

        Task selectedTask = findTask(taskId);

        if (selectedTask == null) {

            showError("Task not found.");
            return;
        }

        // Work on a copy until the user saves successfully
        Task editableTask = copyTask(selectedTask);

        Task updatedTask = TaskDialog.showDialog(
            this,
            editableTask,
            user.getUserId()
        );

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

        int taskId = taskTablePanel.getSelectedTaskId();

        if (taskId == -1) {

            showError("Please select a task to delete.");
            return;
        }

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
                    taskId,
                    user.getUserId())) {

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

    // TOGGLE TASK STATUS
    private void toggleStatus() {

        int taskId = taskTablePanel.getSelectedTaskId();

        if (taskId == -1) {

            showError("Please select a task.");
            return;
        }

        Task task = findTask(taskId);

        if (task == null) {

            showError("Task not found.");
            return;
        }

        try {

            boolean success;

            if ("PENDING".equals(task.getStatus())) {

                success = taskService.markCompleted(
                    taskId,
                    user.getUserId()
                );

            } else {

                success = taskService.markPending(
                    taskId,
                    user.getUserId()
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

    // FIND TASK
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

    // COPY TASK
    private Task copyTask(Task task) {

        return new Task(
            task.getTaskId(),
            task.getUserId(),
            task.getTitle(),
            task.getDescription(),
            task.getPriority(),
            task.getDueDate(),
            task.getStatus()
        );
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