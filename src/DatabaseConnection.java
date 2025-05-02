import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;


public class DatabaseConnection {

    private static  String URL = "jdbc:mysql://localhost:3306/eventverwaltung";
    private static  String USERNAME = "root";
    private static  String PASSWORD = "MYSQLPW1310&";

    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);

    public DatabaseConnection(LoginInfo loginInfo) {
        URL = loginInfo.getDbLink();
        USERNAME = loginInfo.getUsername();
        PASSWORD = loginInfo.getPassword();
    }

    public Connection loadDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Verbindung zur Datenbank herstellen
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("Verbindung zur Datenbank hergestellt!");
        return connection;
    }

    public void listAllCategories() {
        try {
            String sql = "SELECT name FROM category";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n===== ALLE KATEGORIEN =====");
            System.out.printf("%-30s\n", "Name");
            System.out.println("------------------------------");

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                System.out.printf("%-30s\n", name);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Kategorien!");
            e.printStackTrace();
        }
    }

    public void createCategory() {
        try {
            scanner.nextLine(); // Zeilenumbruch konsumieren

            System.out.print("Geben Sie den Namen der neuen Kategorie ein: ");
            String categoryName = scanner.nextLine();

            String sql = "INSERT INTO category (name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, categoryName);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Kategorie '" + categoryName + "' erfolgreich erstellt!");
            } else {
                System.out.println("Kategorie konnte nicht erstellt werden.");
            }

            preparedStatement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Kategorie!");
            e.printStackTrace();
        }
    }

    public void listAllUsers() {
        try {
            String sql = "SELECT userID, firstName, lastName, email, role FROM User";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n===== ALLE BENUTZER =====");
            System.out.printf("%-5s | %-15s | %-15s | %-30s | %-10s\n",
                    "ID", "Vorname", "Nachname", "Email", "Rolle");
            System.out.println("---------------------------------------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("userID");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");

                System.out.printf("%-5d | %-15s | %-15s | %-30s | %-10s\n",
                        id, firstName, lastName, email, role);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Benutzer!");
            e.printStackTrace();
        }
    }

    public void createUser() {
        try {
            scanner.nextLine(); // Zeilenumbruch konsumieren

            System.out.print("Vorname: ");
            String firstName = scanner.nextLine();

            System.out.print("Nachname: ");
            String lastName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Rolle (admin, organizer, user): ");
            String role = scanner.nextLine();

            System.out.print("Passwort: ");
            String password = scanner.nextLine();
            // Hier solltest du das Passwort eigentlich hashen:
            String hashedPassword = hashPassword(password); // -> Dummy-Funktion oder echte Hash-Methode

            String sql = "INSERT INTO User (firstName, lastName, email, role, hashPassword) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, role);
            preparedStatement.setString(5, hashedPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Benutzer '" + firstName + " " + lastName + "' erfolgreich erstellt!");
            } else {
                System.out.println("Benutzer konnte nicht erstellt werden.");
            }

            preparedStatement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen des Benutzers!");
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
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
