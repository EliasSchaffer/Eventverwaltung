import data.LoginInfo;
import data.User;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static DatabaseConnection db;
    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);
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
            ui = new UI(authSystem);

            boolean running = true;
            while (running) {
                ui.displayMainMenu();
                int choice = getUserChoice();

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
                    System.out.println("Logged in as: " + user.getFullName() + " (" + user.getRole() + ")");
                }
                return true;
            case 2: // Register
                // This would be implemented in a registration method
                db.createUser();
                return true;
            case 3: // View events
                // Event viewing functionality
                db.listAllEvents();
                return true;
            case 0: // Exit
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
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
                System.out.println("Ticket viewing feature not yet implemented.");
                return true;
            case 4: // Edit profile
                System.out.println("Profile editing feature not yet implemented.");
                return true;
            case 5: // Logout
                authSystem.logout();
                return true;
            case 0: // Exit
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
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
                System.out.println("Ticket viewing feature not yet implemented.");
                return true;
            case 4: // Edit profile
                System.out.println("Profile editing feature not yet implemented.");
                return true;
            case 5: // Manage my events
                System.out.println("Event management feature not yet implemented.");
                return true;
            case 6: // Create new event
                ui.displayEventCreationForm();
                return true;
            case 7: // Export participant lists
                System.out.println("Participant list export feature not yet implemented.");
                return true;
            case 8: // Logout
                authSystem.logout();
                return true;
            case 0: // Exit
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
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
                System.out.println("Ticket viewing feature not yet implemented.");
                return true;
            case 4: // Edit profile
                System.out.println("Profile editing feature not yet implemented.");
                return true;
            case 5: // User management
                ui.displayUserManagementMenu();
                int userMgmtChoice = getUserChoice();
                handleUserManagement(userMgmtChoice);
                return true;
            case 6: // Category management
                ui.displayCategoryManagementMenu();
                int categoryMgmtChoice = getUserChoice();
                handleCategoryManagement(categoryMgmtChoice);
                return true;
            case 7: // Location management
                ui.displayLocationManagementMenu();
                int locationMgmtChoice = getUserChoice();
                handleLocationManagement(locationMgmtChoice);
                return true;
            case 8: // Event management
                System.out.println("Event management feature not yet implemented.");
                return true;
            case 9: // View statistics
                ui.displayStatisticsMenu();
                int statChoice = getUserChoice();
                handleStatistics(statChoice);
                return true;
            case 10: // Export data
                System.out.println("Exporting database as CSV files...");
                boolean success = readerWriter.exportDatabaseToCSV(connection);
                if (success) {
                    System.out.println("CSV export completed successfully.");
                } else {
                    System.err.println("CSV export could not be completed.");
                }
                return true;
            case 11: // Logout
                authSystem.logout();
                return true;
            case 0: // Exit
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    private static void handleUserManagement(int choice) {
        switch (choice) {
            case 1: // Show all users
                db.listAllUsers();
                break;
            case 2: // Create new user
                db.createUser();
                break;
            case 3: // Edit user
                System.out.println("User editing feature not yet implemented.");
                break;
            case 4: // Delete user
                System.out.println("User deletion feature not yet implemented.");
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void handleCategoryManagement(int choice) {
        switch (choice) {
            case 1: // Show all categories
                db.listAllCategories();
                break;
            case 2: // Create new category
                db.createCategory();
                break;
            case 3: // Edit category
                System.out.println("Category editing feature not yet implemented.");
                break;
            case 4: // Delete category
                System.out.println("Category deletion feature not yet implemented.");
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void handleLocationManagement(int choice) {
        switch (choice) {
            case 1: // Show all locations
                db.listAllLocations();
                break;
            case 2: // Create new location
                System.out.println("Location creation feature not yet implemented.");
                break;
            case 3: // Edit location
                System.out.println("Location editing feature not yet implemented.");
                break;
            case 4: // Delete location
                System.out.println("Location deletion feature not yet implemented.");
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void handleStatistics(int choice) {
        switch (choice) {
            case 1: // Events per user
                System.out.println("Events per user statistics not yet implemented.");
                break;
            case 2: // Events with most bookings
                System.out.println("Most booked events statistics not yet implemented.");
                break;
            case 3: // Most popular categories
                System.out.println("Most popular categories statistics not yet implemented.");
                break;
            case 0: // Back to main menu
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }
}