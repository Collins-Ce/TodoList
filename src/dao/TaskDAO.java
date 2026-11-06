package dao;

import database.DBConnection;
import model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    // Add a new task
    public boolean addTask(Task task) throws SQLException {

        String sql = "INSERT INTO tasks " +
            "(user_id, title, description, priority, due_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, task.getUserId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getPriority());

            if (task.getDueDate() != null) {
                ps.setDate(5, Date.valueOf(task.getDueDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, task.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTaskId(rs.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Retrieve all tasks belonging to a user
    public List<Task> getTasksByUser(int userId) throws SQLException {

        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE user_id = ? " +
                     "ORDER BY due_date IS NULL, due_date ASC, task_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Date sqlDate = rs.getDate("due_date");

                    Task task = new Task(
                        rs.getInt("task_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getString("status")
                    );

                    tasks.add(task);
                }
            }
        }

        return tasks;
    }

    // Update an existing task
    public boolean updateTask(Task task) throws SQLException {

        String sql = "UPDATE tasks SET title = ?, description = ?, " +
                     "priority = ?, due_date = ?, status = ? " +
                     "WHERE task_id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getPriority());

            if (task.getDueDate() != null) {
                ps.setDate(4, Date.valueOf(task.getDueDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getTaskId());
            ps.setInt(7, task.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete a task belonging to a user
    public boolean deleteTask(int taskId, int userId) throws SQLException {

        String sql = "DELETE FROM tasks WHERE task_id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    // Mark a task as completed or pending
    public boolean updateTaskStatus(
            int taskId, int userId, String status) throws SQLException {

        String sql = "UPDATE tasks SET status = ? " +
                     "WHERE task_id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }
}