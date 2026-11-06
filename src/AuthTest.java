import java.sql.SQLException;
import java.util.Scanner;

import model.User;
import service.AuthService;

public class AuthTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AuthService auth = new AuthService();

        try {

            System.out.println("==============================");
            System.out.println("       TODO LIST APP");
            System.out.println("==============================");

            // Registration
            System.out.println("\n--- USER REGISTRATION ---");

            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter email: ");
            String email = sc.nextLine();

            System.out.print("Enter password: ");
            String password = sc.nextLine();

            boolean registered = auth.register(
                username, email, password
            );

            if (registered) {
                System.out.println("\nRegistration successful!");
            }

            // Verify password is hashed in database
            User user = auth.login(email, password);

            if (user != null) {
                System.out.println("Password verification successful!");
                System.out.println("User ID: " + user.getUserId());
            }

            // Login with incorrect password
            System.out.println("\n--- INVALID LOGIN TEST ---");

            User invalidUser = auth.login(email, "WrongPassword123");

            if (invalidUser == null) {
                System.out.println("Incorrect password rejected!");
            }

            // Normal login
            System.out.println("\n--- LOGIN TEST ---");

            System.out.print("Enter email: ");
            String loginEmail = sc.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = sc.nextLine();

            User loggedInUser = auth.login(loginEmail, loginPassword);

            if (loggedInUser != null) {
                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + loggedInUser.getUsername());
            } else {
                System.out.println("\nInvalid email or password!");
            }

        } catch (IllegalArgumentException e) {

            System.out.println("\nRegistration failed: " + e.getMessage());

        } catch (SQLException e) {

            System.out.println("\nDatabase error!");
            e.printStackTrace();

        } finally {
            sc.close();
        }
    }
}