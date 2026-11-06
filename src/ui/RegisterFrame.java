package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import service.AuthService;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private AuthService authService;

    public RegisterFrame() {

        authService = new AuthService();

        setTitle("Todo List - Register");
        setSize(450, 430);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading
        JLabel heading = new JLabel(
            "CREATE ACCOUNT",
            SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(heading, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;

        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(18);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Email
        gbc.gridy = 2;
        gbc.gridx = 0;

        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(18);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridy = 3;
        gbc.gridx = 0;

        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(18);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Confirm password
        gbc.gridy = 4;
        gbc.gridx = 0;

        panel.add(new JLabel("Confirm Password:"), gbc);

        confirmPasswordField = new JPasswordField(18);

        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Register button
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> register());

        // Back to login button
        JButton loginButton = new JButton("Back to Login");

        gbc.gridy = 6;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(panel);
    }

    private void register() {

        String username = usernameField.getText();
        String email = emailField.getText();

        String password = new String(
            passwordField.getPassword()
        );

        String confirmPassword = new String(
            confirmPasswordField.getPassword()
        );

        // Check password confirmation
        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match!",
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        try {

            boolean success = authService.register(
                username,
                email,
                password
            );

            if (success) {

                JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully!"
                );

                new LoginFrame().setVisible(true);
                dispose();
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Database error occurred!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new RegisterFrame().setVisible(true);
        });
    }
}