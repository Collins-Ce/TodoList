package ui.dialog;

import model.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskDialog {

    public static Task showDialog(
            Component parent,
            Task existingTask,
            int userId) {

        boolean editing = existingTask != null;

        JTextField titleField = new JTextField(
            editing ? existingTask.getTitle() : ""
        );

        JTextArea descriptionArea = new JTextArea(
            editing && existingTask.getDescription() != null
                ? existingTask.getDescription()
                : "",
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

        JPanel panel = new JPanel(
            new GridLayout(0, 1, 5, 5)
        );

        panel.add(new JLabel("Task Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));

        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);

        panel.add(
            new JLabel("Due Date (YYYY-MM-DD, optional):")
        );

        panel.add(dueDateField);

        int result = JOptionPane.showConfirmDialog(
            parent,
            panel,
            editing ? "Edit Task" : "Add Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String title = titleField.getText().trim();

        if (title.isEmpty()) {

            JOptionPane.showMessageDialog(
                parent,
                "Task title cannot be empty.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );

            return null;
        }

        String description =
            descriptionArea.getText().trim();

        String priority =
            (String) priorityBox.getSelectedItem();

        String dateText =
            dueDateField.getText().trim();

        LocalDate dueDate;

        try {

            dueDate = dateText.isEmpty()
                ? null
                : LocalDate.parse(dateText);

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                parent,
                "Invalid date. Use YYYY-MM-DD.\n" +
                "Example: 2026-11-10",
                "Invalid Date",
                JOptionPane.ERROR_MESSAGE
            );

            return null;
        }

        if (editing) {

            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setPriority(priority);
            existingTask.setDueDate(dueDate);

            return existingTask;
        }

        return new Task(
            userId,
            title,
            description,
            priority,
            dueDate
        );
    }
}