package ui;

import javax.swing.*;
import java.awt.*;

import model.User;

public class Dashboard extends JFrame {

    private User user;

    public Dashboard(User user) {

        this.user = user;

        setTitle("Todo List - Dashboard");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + user.getUsername() + "!"
        );

        welcomeLabel.setFont(
            new Font("Arial", Font.BOLD, 24)
        );

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {

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
        });

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center content
        JPanel contentPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(
            "MY TASKS",
            SwingConstants.CENTER
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Empty task table for now
        String[] columns = {
            "Task ID",
            "Task Title",
            "Priority",
            "Due Date",
            "Status"
        };

        Object[][] data = {};

        JTable taskTable = new JTable(data, columns);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}