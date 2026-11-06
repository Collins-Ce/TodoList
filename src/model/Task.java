package model;

import java.time.LocalDate;

public class Task {

    private int taskId;
    private int userId;
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private String status;

    // Constructor for creating a new task
    public Task(int userId, String title, String description,
                String priority, LocalDate dueDate) {

        this.userId = userId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = "PENDING";
    }

    // Constructor for retrieving an existing task
    public Task(int taskId, int userId, String title,
                String description, String priority,
                LocalDate dueDate, String status) {

        this.taskId = taskId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getters
    public int getTaskId() {
        return taskId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}