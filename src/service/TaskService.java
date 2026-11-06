package service;

import dao.TaskDAO;
import model.Task;

import java.sql.SQLException;
import java.util.List;

public class TaskService {

    private TaskDAO taskDAO = new TaskDAO();

    // Add task
    public boolean addTask(Task task) throws SQLException {

        if (task.getTitle() == null ||
            task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Task title cannot be empty."
            );
        }

        if (task.getTitle().trim().length() > 100) {
            throw new IllegalArgumentException(
                "Task title cannot exceed 100 characters."
            );
        }

        validatePriority(task.getPriority());

        task.setTitle(task.getTitle().trim());

        return taskDAO.addTask(task);
    }

    // Get all tasks for a user
    public List<Task> getTasks(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return taskDAO.getTasksByUser(userId);
    }

    // Update task
    public boolean updateTask(Task task) throws SQLException {

        if (task.getTaskId() <= 0 || task.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid task or user ID.");
        }

        if (task.getTitle() == null ||
            task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Task title cannot be empty."
            );
        }

        validatePriority(task.getPriority());
        validateStatus(task.getStatus());

        task.setTitle(task.getTitle().trim());

        return taskDAO.updateTask(task);
    }

    // Delete task
    public boolean deleteTask(int taskId, int userId)
            throws SQLException {

        validateIds(taskId, userId);

        return taskDAO.deleteTask(taskId, userId);
    }

    // Mark task as completed
    public boolean markCompleted(int taskId, int userId)
            throws SQLException {

        validateIds(taskId, userId);

        return taskDAO.updateTaskStatus(
            taskId, userId, "COMPLETED"
        );
    }

    // Mark task as pending
    public boolean markPending(int taskId, int userId)
            throws SQLException {

        validateIds(taskId, userId);

        return taskDAO.updateTaskStatus(
            taskId, userId, "PENDING"
        );
    }

    // Validate IDs
    private void validateIds(int taskId, int userId) {

        if (taskId <= 0 || userId <= 0) {
            throw new IllegalArgumentException("Invalid ID.");
        }
    }

    // Validate priority
    private void validatePriority(String priority) {

        if (priority == null ||
            !(priority.equals("LOW") ||
              priority.equals("MEDIUM") ||
              priority.equals("HIGH"))) {

            throw new IllegalArgumentException(
                "Priority must be LOW, MEDIUM, or HIGH."
            );
        }
    }

    // Validate status
    private void validateStatus(String status) {

        if (status == null ||
            !(status.equals("PENDING") ||
              status.equals("COMPLETED"))) {

            throw new IllegalArgumentException(
                "Invalid task status."
            );
        }
    }
}