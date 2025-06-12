import data.LoginInfo;
import data.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {

    private String URL;
    private String USERNAME;
    private String PASSWORD;

    private Connection connection;
    private static Scanner scanner = new Scanner(System.in);

    public DatabaseConnection(LoginInfo loginInfo) {
        if (loginInfo != null) {
            this.URL = loginInfo.getDbLink();
            this.USERNAME = loginInfo.getUsername();
            this.PASSWORD = loginInfo.getPassword();
        } else {
            this.URL = "jdbc:mysql://localhost:3306/eventverwaltung";
            this.USERNAME = "root";
            this.PASSWORD = "";
        }
    }

    public Connection loadDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("Verbindung zur Datenbank hergestellt!");
        return connection;
    }

    // ===== CATEGORY MANAGEMENT =====

    public void listAllCategories() {
        try {
            String sql = "SELECT  name FROM category";
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

    public boolean deleteCategory(int categoryId) {
        try {
            String sql = "DELETE FROM category WHERE categoryID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, categoryId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(int categoryId, String newName) {
        try {
            String sql = "UPDATE category SET name = ? WHERE categoryID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setInt(2, categoryId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== USER MANAGEMENT =====

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

    public void createUser(boolean isAdmin) {
        try {
            System.out.print("Vorname: ");
            String firstName = scanner.nextLine();

            System.out.print("Nachname: ");
            String lastName = scanner.nextLine();

            String email = "";
            boolean validEmail = false;

            // Email validation loop
            while (!validEmail) {
                System.out.print("Email: ");
                email = scanner.nextLine();

                if (isValidEmail(email)) {
                    validEmail = true;
                } else {
                    System.out.println("Ungültige E-Mail-Adresse! Bitte versuchen Sie es erneut.");
                }
            }

            String role;
            if (isAdmin) {
                System.out.print("Rolle (admin, organizer, user): ");
                role = scanner.nextLine();
            }else  role = "user";

            System.out.print("Passwort: ");
            String password = scanner.nextLine();
            String hashedPassword = hashPassword(password);

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

    // Email validation method
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public boolean updateUserProfile(int userId, String firstName, String lastName, String email, String password) {
        try {
            String sql = "UPDATE User SET firstName = ?, lastName = ?, email = ?, hashPassword = ? WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, hashPassword(password));
            stmt.setInt(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            connection.setAutoCommit(false); // Transaction starten

            // 1. Alle Tickets für Events löschen, die der User organisiert hat
            String deleteAllTicketsForUserEventsSQL =
                    "DELETE FROM Ticket WHERE eventID IN " +
                            "(SELECT eventID FROM Event WHERE organizerID = ?)";
            PreparedStatement stmt1 = connection.prepareStatement(deleteAllTicketsForUserEventsSQL);
            stmt1.setInt(1, userId);
            stmt1.executeUpdate();

            // 2. Alle Tickets löschen, die der User selbst gekauft hat (für andere Events)
            String deleteUserTicketsSQL = "DELETE FROM Ticket WHERE userID = ?";
            PreparedStatement stmt2 = connection.prepareStatement(deleteUserTicketsSQL);
            stmt2.setInt(1, userId);
            stmt2.executeUpdate();

            // 3. Alle EventTicketCategorie Einträge für Events löschen, wo der User Organizer ist
            String deleteEventTicketCategoriesSQL =
                    "DELETE FROM EventTicketCategorie WHERE eventID IN " +
                            "(SELECT eventID FROM Event WHERE organizerID = ?)";
            PreparedStatement stmt3 = connection.prepareStatement(deleteEventTicketCategoriesSQL);
            stmt3.setInt(1, userId);
            stmt3.executeUpdate();

            // 4. Alle Events löschen, wo der User Organizer ist
            String deleteEventsSQL = "DELETE FROM Event WHERE organizerID = ?";
            PreparedStatement stmt4 = connection.prepareStatement(deleteEventsSQL);
            stmt4.setInt(1, userId);
            stmt4.executeUpdate();

            // 5. Schließlich den User löschen
            String deleteUserSQL = "DELETE FROM User WHERE userID = ?";
            PreparedStatement stmt5 = connection.prepareStatement(deleteUserSQL);
            stmt5.setInt(1, userId);
            int result = stmt5.executeUpdate();

            connection.commit(); // Transaction bestätigen
            return result > 0;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Bei Fehler alles rückgängig machen
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // AutoCommit wieder aktivieren
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateUser(int userId, String firstName, String lastName, String email, String role) {
        try {
            String sql = "UPDATE User SET firstName = ?, lastName = ?, email = ?, role = ? WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setInt(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== AUTHENTICATION =====

    public User login() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Passwort: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);

        User currentUser;

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

    // ===== LOCATION MANAGEMENT =====

    public void listAllLocations() {
        try {
            String sql = "SELECT locationID, location, city, street, number FROM Location";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n===== ALLE LOCATIONS =====");
            System.out.printf("%-5s | %-15s | %-15s | %-15s | %-10s\n",
                    "ID", "Name", "Stadt", "Straße", "Hausnummer");
            System.out.println("---------------------------------------------------------------------");

            while (resultSet.next()) {
                int locationID = resultSet.getInt("locationID");
                String land = resultSet.getString("location");
                String stadt = resultSet.getString("city");
                String strasse = resultSet.getString("street");
                int nummer = resultSet.getInt("number");
                System.out.printf("%-5d | %-15s | %-15s | %-15s | %-10d\n",
                        locationID, land, stadt, strasse, nummer);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Locations!");
            e.printStackTrace();
        }
    }

    public void listAllTicketTypes() {
        try {
            String sql = "SELECT typeID, type FROM tickettype";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n===== TICKET-TYPEN =====");
            System.out.printf("%-5s | %-15s\n",
                    "ID", "Type");
            System.out.println("---------------------------------------------------------------------");

            while (resultSet.next()) {
                int typeID = resultSet.getInt("typeID");
                String type = resultSet.getString("type");
                System.out.printf("%-5d | %-15s \n",
                        typeID, type);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Locations!");
            e.printStackTrace();
        }
    }

    public boolean createLocation(String location, String city, String street, int number) {
        try {
            String sql = "INSERT INTO Location (location, city, street, street_number) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, location);
            stmt.setString(2, city);
            stmt.setString(3, street);
            stmt.setInt(4, number);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ort '" + location + "' wurde erfolgreich erstellt!");
                return true;
            } else {
                System.out.println("Fehler beim Erstellen des Ortes.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen des Ortes!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLocation(int locationId, String location, String city, String street, int number) {
        try {
            String sql = "UPDATE Location SET location = ?, city = ?, street = ?, street_number = ? WHERE locationID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, location);
            stmt.setString(2, city);
            stmt.setString(3, street);
            stmt.setInt(4, number);
            stmt.setInt(5, locationId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ort wurde erfolgreich aktualisiert!");
                return true;
            } else {
                System.out.println("Fehler beim Aktualisieren des Ortes.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Bearbeiten des Ortes!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLocation(int locationId) {
        try {
            String sql = "DELETE FROM Location WHERE locationID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, locationId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ort wurde erfolgreich gelöscht!");
                return true;
            } else {
                System.out.println("Fehler beim Löschen des Ortes.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen des Ortes!");
            e.printStackTrace();
            return false;
        }
    }

    // ===== EVENT MANAGEMENT =====

    public void listAllEvents() {
        try {
            String sql = "SELECT e.eventID, e.title, e.description, e.tickets, " +
                    "COUNT(t.ticketID) AS soldTickets " +
                    "FROM Event e " +
                    "LEFT JOIN Ticket t ON e.eventID = t.eventID " +
                    "GROUP BY e.eventID, e.title, e.description, e.tickets";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n===== ALLE EVENTS =====");
            System.out.printf("%-5s | %-30s | %-20s | %-10s | %-10s\n",
                    "ID", "Title", "Beschreibung", "Total", "Verfügbar");
            System.out.println("-------------------------------------------------------------------------");

            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                int totalTickets = resultSet.getInt("tickets");
                int soldTickets = resultSet.getInt("soldTickets");
                int availableTickets = totalTickets - soldTickets;

                System.out.printf("%-5d | %-30s | %-20s | %-10d | %-10d\n",
                        eventID, title, description, totalTickets, availableTickets);
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Events!");
            e.printStackTrace();
        }
    }

    public boolean createEvent(String title, String description, String fromDate, String toDate,
                               int organizerId, int categoryId, int locationId, int tickets) {
        try {
            String sql = "INSERT INTO Event (title, description, fromDate, toDate, organizerID, categorie, locationID, tickets) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, fromDate);
            stmt.setString(4, toDate);
            stmt.setInt(5, organizerId);
            stmt.setInt(6, categoryId);
            stmt.setInt(7, locationId);
            stmt.setInt(8, tickets);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(int eventId) {
        try {
            // First delete all tickets for this event
            String deleteTickets = "DELETE FROM Ticket WHERE eventID = ?";
            PreparedStatement ticketStmt = connection.prepareStatement(deleteTickets);
            ticketStmt.setInt(1, eventId);
            ticketStmt.executeUpdate();

            // Then delete the event
            String sql = "DELETE FROM Event WHERE eventID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, eventId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void displayOrganizerEvents(int organizerId) {
        try {
            String sql = "SELECT eventID, title, description, fromDate, toDate, tickets FROM Event WHERE organizerID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== MEINE EVENTS =====");
            System.out.printf("%-5s | %-20s | %-15s | %-12s | %-12s | %-8s\n",
                    "ID", "Titel", "Beschreibung", "Von", "Bis", "Tickets");
            System.out.println("-------------------------------------------------------------------------");

            while (rs.next()) {
                int eventId = rs.getInt("eventID");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String fromDate = rs.getString("fromDate");
                String toDate = rs.getString("toDate");
                int tickets = rs.getInt("tickets");

                System.out.printf("%-5d | %-20s | %-15s | %-12s | %-12s | %-8d\n",
                        eventId, title, description, fromDate, toDate, tickets);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Events!");
            e.printStackTrace();
        }
    }

    // ===== TICKET MANAGEMENT =====

    public boolean bookTicket(int eventId, int userId, int typeId) {
        try {
            // Check if tickets available
            String checkSql = "SELECT tickets FROM Event WHERE eventID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, eventId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int available = rs.getInt("tickets");
                if (available <= 0) {
                    System.out.println("Keine Tickets mehr verfügbar!");
                    return false;
                }
            }

            // Book ticket - korrigiert nach UML Schema
            String sql = "INSERT INTO Ticket (userID, eventID, bookingDate, redeemed, typeID, vorName, nachName) " +
                    "VALUES (?, ?, CURDATE(), FALSE, ?, '', '')";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, typeId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Decrease available tickets
                String updateSql = "UPDATE Event SET tickets = tickets - 1 WHERE eventID = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, eventId);
                updateStmt.executeUpdate();

                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void displayMyTickets(int userId) {
        try {
            String sql = "SELECT t.ticketID, e.title, t.bookingDate, t.redeemed, tt.type " +
                    "FROM Ticket t " +
                    "JOIN Event e ON t.eventID = e.eventID " +
                    "JOIN TicketType tt ON t.typeID = tt.typeID " +
                    "WHERE t.userID = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== MEINE TICKETS =====");
            System.out.printf("%-10s | %-30s | %-12s | %-10s | %-15s\n",
                    "Ticket-ID", "Event", "Buchungsdatum", "Eingelöst", "Typ");
            System.out.println("-------------------------------------------------------------------------");

            while (rs.next()) {
                int ticketId = rs.getInt("ticketID");
                String eventTitle = rs.getString("title");
                String bookingDate = rs.getString("bookingDate");
                boolean redeemed = rs.getBoolean("redeemed");
                String ticketType = rs.getString("type");

                System.out.printf("%-10d | %-30s | %-12s | %-10s | %-15s\n",
                        ticketId, eventTitle, bookingDate,
                        redeemed ? "Ja" : "Nein", ticketType);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Tickets!");
            e.printStackTrace();
        }
    }

    // ===== STATISTICS =====

    public void displayEventsPerUser() {
        try {
            String sql = "SELECT u.firstName, u.lastName, COUNT(t.ticketID) as ticketCount " +
                    "FROM User u " +
                    "LEFT JOIN Ticket t ON u.userID = t.userID " +
                    "GROUP BY u.userID, u.firstName, u.lastName " +
                    "ORDER BY ticketCount DESC";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n===== EVENTS PRO BENUTZER =====");
            System.out.printf("%-20s | %-20s | %-10s\n", "Vorname", "Nachname", "Tickets");
            System.out.println("-----------------------------------------------------");

            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                int ticketCount = rs.getInt("ticketCount");

                System.out.printf("%-20s | %-20s | %-10d\n", firstName, lastName, ticketCount);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Statistik!");
            e.printStackTrace();
        }
    }

    public void displayMostBookedEvents() {
        try {
            String sql = "SELECT e.title, COUNT(t.ticketID) as bookingCount " +
                    "FROM Event e " +
                    "LEFT JOIN Ticket t ON e.eventID = t.eventID " +
                    "GROUP BY e.eventID, e.title " +
                    "ORDER BY bookingCount DESC " +
                    "LIMIT 10";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n===== EVENTS MIT DEN MEISTEN BUCHUNGEN =====");
            System.out.printf("%-30s | %-10s\n", "Event-Titel", "Buchungen");
            System.out.println("---------------------------------------------");

            while (rs.next()) {
                String title = rs.getString("title");
                int bookingCount = rs.getInt("bookingCount");

                System.out.printf("%-30s | %-10d\n", title, bookingCount);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Statistik!");
            e.printStackTrace();
        }
    }

    public void displayPopularCategories() {
        try {
            String sql = "SELECT c.name, COUNT(t.ticketID) as ticketCount " +
                    "FROM category c " +
                    "LEFT JOIN Event e ON c.categorie = e.categoryID " +
                    "LEFT JOIN Ticket t ON e.eventID = t.eventID " +
                    "GROUP BY c.categoryID, c.name " +
                    "ORDER BY ticketCount DESC";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n===== BELIEBTESTE KATEGORIEN =====");
            System.out.printf("%-20s | %-10s\n", "Kategorie", "Tickets");
            System.out.println("--------------------------------");

            while (rs.next()) {
                String categoryName = rs.getString("name");
                int ticketCount = rs.getInt("ticketCount");

                System.out.printf("%-20s | %-10d\n", categoryName, ticketCount);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Anzeigen der Statistik!");
            e.printStackTrace();
        }
    }

    // ===== PARTICIPANT LIST EXPORT =====

    public void displayParticipantList(int eventId, int organizerId) {
        try {
            String sql = "SELECT u.firstName, u.lastName, u.email, t.bookingDate " +
                    "FROM Ticket t " +
                    "JOIN User u ON t.userID = u.userID " +
                    "JOIN Event e ON t.eventID = e.eventID " +
                    "WHERE t.eventID = ? AND e.organizerID = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setInt(2, organizerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== TEILNEHMERLISTE =====");
            System.out.printf("%-15s | %-15s | %-25s | %-12s\n",
                    "Vorname", "Nachname", "Email", "Buchungsdatum");
            System.out.println("-----------------------------------------------------------------------");

            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String bookingDate = rs.getString("bookingDate");

                System.out.printf("%-15s | %-15s | %-25s | %-12s\n",
                        firstName, lastName, email, bookingDate);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Exportieren der Teilnehmerliste!");
            e.printStackTrace();
        }
    }

    // ===== UTILITY METHODS =====

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

    // Legacy method - sollte nicht mehr verwendet werden
    @Deprecated
    public String getUser(String username) {
        try {
            String sql = "SELECT hashPassword FROM User Where username = ?";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                return resultSet.getString("hashPassword");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Fehler beim Auflisten der Benutzer!");
            e.printStackTrace();
        }
        return "";
    }
}