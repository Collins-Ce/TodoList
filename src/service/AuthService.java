package service;

import dao.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    // Register a new user
    public boolean register(String username, String email, String password)
            throws SQLException {

        username = username.trim();
        email = email.trim().toLowerCase();

        // Validate empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }

        // Validate email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        // Validate password length
        if (password.length() < 8) {
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters."
            );
        }

        // Check duplicate username
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        // Check duplicate email
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // Hash password using BCrypt
        String hashedPassword = BCrypt.hashpw(
            password,
            BCrypt.gensalt(12)
        );

        User user = new User(username, email, hashedPassword);

        return userDAO.registerUser(user);
    }

    // Login an existing user
    public User login(String email, String password) throws SQLException {

        if (email == null || password == null ||
            email.trim().isEmpty() || password.isEmpty()) {
            return null;
        }

        email = email.trim().toLowerCase();

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            return null;
        }

        // Compare entered password with stored BCrypt hash
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }

        return null;
    }
}