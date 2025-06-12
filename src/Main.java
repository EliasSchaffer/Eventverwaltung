import data.LoginInfo;
import data.User;

import java.sql.*;

public class Main {

    private static DatabaseConnection db;
    private static Connection connection;
    private static ReaderWriter readerWriter = new ReaderWriter();
    private static AuthenticationSystem authSystem;
    private static UI ui;

    public static void main(String[] args) {
        // Check if LoginInfo already exists or needs to be created
        LoginInfo login = readerWriter.loadLoginInfo();

        if (login == null) {
            // Ask for login information on first start
            login = Settings.setupLoginInfo();

            // Save login information
            try {
                readerWriter.saveLoginInfo(login.getDbLink(), login.getUsername(), login.getPassword());
            } catch (Exception e) {
                System.err.println("Error saving login information:");
                e.printStackTrace();
            }
        }

        db = new DatabaseConnection(login);
        try {
            connection = db.loadDatabase();
            authSystem = new AuthenticationSystem(connection);
            ui = new UI(authSystem, db);

            boolean running = true;
            while (running) {
                ui.displayMainMenu();
                int choice = ui.getUserChoice();

                if (!authSystem.isLoggedIn()) {
                    // Guest menu options
                    running = handleGuestMenu(choice);
                } else {
                    // User is logged in, check role
                    String role = authSystem.getCurrentUser().getRole().toLowerCase();
                    switch (role) {
                        case "admin":
                            running = handleAdminMenu(choice);
                            break;
                        case "organizer":
                            running = handleOrganizerMenu(choice);
                            break;
                        default: // normal user
                            running = handleUserMenu(choice);
                            break;
                    }
                }

                System.out.println(); // Line break for better readability
            }

            // Close connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error connecting to database!");
            e.printStackTrace();
        }
    }

    private static boolean handleGuestMenu(int choice) {
        switch (choice) {
            case 1: // Login
                User user = authSystem.login();
                if (user != null) {
                    System.out.println("Erfolgreich eingeloggt als: " + user.getFullName() + " (" + user.getRole() + ")");
                }
                return true;
            case 2: // Register
                db.createUser(false);
                return true;
            case 3: // View events
                db.listAllEvents();
                return true;
            case 0: // Exit
                System.out.println("Auf Wiedersehen!");
                return false;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
                return true;
        }
    }

    private static boolean handleUserMenu(int choice) {
        switch (choice) {
            case 1: // View events
                db.listAllEvents();
                return true;
            case 2: // Book ticket
                ui.displayTicketBookingForm();
                return true;
            case 3: // View my tickets
                db.displayMyTickets(authSystem.getCurrentUser().getUserId());
                return true;
            case 4: // Edit profile
                ui.displayProfileEditForm();
                return true;
            case 5: // Logout
                authSystem.logout();
                System.out.println("Erfolgreich ausgeloggt.");
                return true;
            case 0: // Exit
                System.out.println("Auf Wiedersehen!");
                return false;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
                return true;
        }
    }

    private static boolean handleOrganizerMenu(int choice) {
        switch (choice) {
            case 1: // View events
                db.listAllEvents();
                return true;
            case 2: // Book ticket
                ui.displayTicketBookingForm();
                return true;
            case 3: // View my tickets
                db.displayMyTickets(authSystem.getCurrentUser().getUserId());
                return true;
            case 4: // Edit profile
                ui.displayProfileEditForm();
                return true;
            case 5: // Manage my events
                db.displayOrganizerEvents(authSystem.getCurrentUser().getUserId());
                return true;
            case 6: // Create new event
                ui.displayEventCreationForm();
                return true;
            case 7: // Export participant lists
                System.out.print("Event-ID für Teilnehmerliste: ");
                int eventId = ui.getUserChoice();
                db.displayParticipantList(eventId, authSystem.getCurrentUser().getUserId());
                return true;
            case 8: // Logout
                authSystem.logout();
                System.out.println("Erfolgreich ausgeloggt.");
                return true;
            case 0: // Exit
                System.out.println("Auf Wiedersehen!");
                return false;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
                return true;
        }
    }

    private static boolean handleAdminMenu(int choice) {
        switch (choice) {
            case 1: // View events
                db.listAllEvents();
                return true;
            case 2: // Book ticket
                ui.displayTicketBookingForm();
                return true;
            case 3: // View my tickets
                db.displayMyTickets(authSystem.getCurrentUser().getUserId());
                return true;
            case 4: // Edit profile
                ui.displayProfileEditForm();
                return true;
            case 5: // User management
                handleUserManagement();
                return true;
            case 6: // Category management
                handleCategoryManagement();
                return true;
            case 7: // Location management
                handleLocationManagement();
                return true;
            case 8: // Event management
                handleEventManagement();
                return true;
            case 9: // View statistics
                handleStatistics();
                return true;
            case 10: // Export data
                System.out.println("Exportiere Datenbank als CSV-Dateien...");
                boolean success = readerWriter.exportDatabaseToCSV(connection);
                if (success) {
                    System.out.println("CSV-Export erfolgreich abgeschlossen.");
                } else {
                    System.err.println("CSV-Export konnte nicht abgeschlossen werden.");
                }
                return true;
            case 11: // Logout
                authSystem.logout();
                System.out.println("Erfolgreich ausgeloggt.");
                return true;
            case 0: // Exit
                System.out.println("Auf Wiedersehen!");
                return false;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
                return true;
        }
    }

    private static void handleUserManagement() {
        ui.displayUserManagementMenu();
        int choice = ui.getUserChoice();

        switch (choice) {
            case 1: // Show all users
                db.listAllUsers();
                break;
            case 2: // Create new user
                db.createUser(true);
                break;
            case 3: // Edit user
                ui.displayUserEditForm();
                break;
            case 4: // Delete user
                ui.displayUserDeleteForm();
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
        }
    }

    private static void handleCategoryManagement() {
        ui.displayCategoryManagementMenu();
        int choice = ui.getUserChoice();

        switch (choice) {
            case 1: // Show all categories
                db.listAllCategories();
                break;
            case 2: // Create new category
                db.createCategory();
                break;
            case 3: // Edit category
                ui.displayCategoryEditForm();
                break;
            case 4: // Delete category
                ui.displayCategoryDeleteForm();
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
        }
    }

    private static void handleLocationManagement() {
        ui.displayLocationManagementMenu();
        int choice = ui.getUserChoice();

        switch (choice) {
            case 1: // Show all locations
                db.listAllLocations();
                break;
            case 2: // Create new location
                ui.displayLocationManagementMenu(); // This needs a create location form in UI
                break;
            case 3: // Edit location
                ui.displayLocationManagementMenu(); // This needs an edit location form in UI
                break;
            case 4: // Delete location
                ui.displayLocationManagementMenu(); // This needs a delete location form in UI
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
        }
    }

    private static void handleEventManagement() {
        ui.displayEventDeleteForm(); // Reusing the delete form for event management
    }

    private static void handleStatistics() {
        ui.displayStatisticsMenu();
        int choice = ui.getUserChoice();

        switch (choice) {
            case 1: // Events per user
                db.displayEventsPerUser();
                break;
            case 2: // Events with most bookings
                db.displayMostBookedEvents();
                break;
            case 3: // Most popular categories
                db.displayPopularCategories();
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Ungültige Eingabe. Bitte versuchen Sie es erneut.");
        }
    }
}