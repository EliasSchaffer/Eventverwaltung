
import data.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthenticationSystem {
    private Connection connection;
    private User currentUser;
    private Scanner scanner;

    public AuthenticationSystem(Connection connection) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
    }

    public User login() {
        System.out.println("\n===== LOGIN =====");

        String email = "";
        boolean validEmail = false;

        // Email validation loop for login
        while (!validEmail) {
            System.out.print("Email: ");
            email = scanner.nextLine();

            if (isValidEmail(email)) {
                validEmail = true;
            } else {
                System.out.println("Ungültige E-Mail-Adresse! Bitte versuchen Sie es erneut.");
            }
        }

        System.out.print("Passwort: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);

        try {
            String query = "SELECT userID, firstName, lastName, email, role FROM User WHERE email = ? AND hashPassword = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, hashedPassword);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("userID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String role = resultSet.getString("role");

                currentUser = new User(userId, firstName, lastName, email, role);
                System.out.println("Login erfolgreich! Willkommen, " + firstName + " " + lastName + " (" + role + ")");
                return currentUser;
            } else {
                System.out.println("Login fehlgeschlagen. Ungültige Email oder Passwort.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Login:");
            e.printStackTrace();
            return null;
        }
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Auf Wiedersehen, " + currentUser.getFirstName() + "!");
            currentUser = null;
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(String requiredRole) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().equalsIgnoreCase(requiredRole);
    }

    public boolean canAccessFeature(String feature) {
        if (currentUser == null) {
            return false;
        }

        String role = currentUser.getRole().toLowerCase();

        switch (feature.toLowerCase()) {
            case "create_event":
            case "edit_event":
            case "delete_event":
                return role.equals("admin") || role.equals("organizer");

            case "view_events":
            case "book_ticket":
                return true; // All users can view events and book tickets

            case "manage_users":
            case "export_data":
                return role.equals("admin");

            case "view_participants":
                return role.equals("admin") || role.equals("organizer");

            default:
                return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fehler beim Hashen des Passworts", e);
        }
    }
}