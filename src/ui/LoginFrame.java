package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import model.User;
import service.AuthService;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private AuthService authService;

    public LoginFrame() {

        authService = new AuthService();

        setTitle("Todo List - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel(
            "TODO LIST LOGIN",
            SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(heading, gbc);

        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;

        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(18);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;

        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(18);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> login());

        // Register button
        JButton registerButton = new JButton("Create Account");

        gbc.gridy = 4;

        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        add(panel);
    }

    private void login() {

        String email = emailField.getText();

        String password = new String(
            passwordField.getPassword()
        );

        try {

            User user = authService.login(email, password);

            if (user != null) {

                JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user.getUsername() + "!"
                );

                new Dashboard(user).setVisible(true);
                dispose();

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Invalid email or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Database connection error!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}