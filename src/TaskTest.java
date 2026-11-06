import model.Task;
import service.TaskService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TaskTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        TaskService taskService = new TaskService();

        try {
            System.out.println("==============================");
            System.out.println("       TODO LIST TEST");
            System.out.println("==============================");

            System.out.print("Enter your User ID: ");
            int userId = sc.nextInt();
            sc.nextLine();

            // 1. ADD TASK
            System.out.println("\n--- ADD TASK ---");

            Task task = new Task(
                userId,
                "Complete Java Project",
                "Finish JDBC To-Do List application",
                "HIGH",
                LocalDate.now().plusDays(3)
            );

            if (taskService.addTask(task)) {
                System.out.println("Task added successfully!");
                System.out.println("Task ID: " + task.getTaskId());
            }

            int taskId = task.getTaskId();

            // 2. RETRIEVE TASKS
            System.out.println("\n--- RETRIEVE TASKS ---");

            List<Task> tasks = taskService.getTasks(userId);

            for (Task t : tasks) {
                System.out.println(
                    "ID: " + t.getTaskId() +
                    " | Title: " + t.getTitle() +
                    " | Priority: " + t.getPriority() +
                    " | Status: " + t.getStatus()
                );
            }

            // 3. UPDATE TASK
            System.out.println("\n--- UPDATE TASK ---");

            task.setTitle("Complete JDBC To-Do List Project");
            task.setPriority("MEDIUM");

            if (taskService.updateTask(task)) {
                System.out.println("Task updated successfully!");
            }

            // 4. MARK TASK COMPLETED
            System.out.println("\n--- COMPLETE TASK ---");

            if (taskService.markCompleted(taskId, userId)) {
                System.out.println("Task marked as completed!");
            }

            // Verify status
            tasks = taskService.getTasks(userId);

            for (Task t : tasks) {
                if (t.getTaskId() == taskId) {
                    System.out.println("Current Status: " + t.getStatus());
                }
            }

            // 5. DELETE TASK
            System.out.println("\n--- DELETE TASK ---");

            if (taskService.deleteTask(taskId, userId)) {
                System.out.println("Task deleted successfully!");
            }

            System.out.println("\n==============================");
            System.out.println("    ALL TESTS COMPLETED");
            System.out.println("==============================");

        } catch (SQLException e) {
            System.out.println("Database error!");
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());

        } finally {
            sc.close();
        }
    }
}