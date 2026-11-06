package ui.components;

import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TaskStatsPanel extends JPanel {

    private JLabel totalLabel;
    private JLabel pendingLabel;
    private JLabel completedLabel;
    private JLabel progressLabel;

    private JProgressBar progressBar;

    public TaskStatsPanel() {

        setLayout(new GridLayout(1, 4, 12, 0));

        totalLabel = new JLabel("Total Tasks: 0", SwingConstants.CENTER);
        pendingLabel = new JLabel("Pending: 0", SwingConstants.CENTER);
        completedLabel = new JLabel("Completed: 0", SwingConstants.CENTER);

        Font font = new Font("Arial", Font.BOLD, 16);

        totalLabel.setFont(font);
        pendingLabel.setFont(font);
        completedLabel.setFont(font);

        add(createCard(totalLabel));
        add(createCard(pendingLabel));
        add(createCard(completedLabel));

        // PROGRESS PANEL
        JPanel progressPanel = new JPanel(new BorderLayout(5, 8));

        progressLabel = new JLabel(
            "Completion Progress: 0%",
            SwingConstants.CENTER
        );

        progressLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        progressPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
            )
        );

        add(progressPanel);
    }

    private JPanel createCard(JLabel label) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 5, 15, 5)
            )
        );

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    public void updateStatistics(List<Task> tasks) {

        int total = tasks.size();
        int pending = 0;
        int completed = 0;

        for (Task task : tasks) {

            if ("PENDING".equals(task.getStatus())) {
                pending++;
            } else if ("COMPLETED".equals(task.getStatus())) {
                completed++;
            }
        }

        totalLabel.setText("Total Tasks: " + total);
        pendingLabel.setText("Pending: " + pending);
        completedLabel.setText("Completed: " + completed);

        int percentage = total == 0
            ? 0
            : (completed * 100) / total;

        progressBar.setValue(percentage);

        progressLabel.setText(
            "Completion Progress: " + percentage + "%"
        );
    }
}