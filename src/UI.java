import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.sql.*;

public class UI {
    private AuthenticationSystem auth;
    private Scanner scanner;
    private DatabaseConnection db;

    public UI(AuthenticationSystem auth, DatabaseConnection db) {
        this.auth = auth;
        this.db = db;
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        System.out.println("\n===== EVENT PLATFORM =====");

        if (!auth.isLoggedIn()) {
            displayGuestMenu();
        } else {
            String role = auth.getCurrentUser().getRole().toLowerCase();
            System.out.println("Eingeloggt als: " + auth.getCurrentUser().getFullName() + " (" + role + ")");

            switch (role) {
                case "admin":
                    displayAdminMenu();
                    break;
                case "organizer":
                    displayOrganizerMenu();
                    break;
                default:
                    displayUserMenu();
                    break;
            }
        }
    }

    public void displayGuestMenu() {
        System.out.println("1. Einloggen");
        System.out.println("2. Registrieren");
        System.out.println("3. Events ansehen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    public void displayUserMenu() {
        System.out.println("1. Events ansehen");
        System.out.println("2. Ticket buchen");
        System.out.println("3. Meine Tickets anzeigen");
        System.out.println("4. Profil bearbeiten");
        System.out.println("5. Ausloggen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    public void displayOrganizerMenu() {
        System.out.println("1. Events ansehen");
        System.out.println("2. Ticket buchen");
        System.out.println("3. Meine Tickets anzeigen");
        System.out.println("4. Profil bearbeiten");
        System.out.println("5. Meine Events verwalten");
        System.out.println("6. Neues Event erstellen");
        System.out.println("7. Teilnehmerlisten exportieren");
        System.out.println("8. Ausloggen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    public void displayAdminMenu() {
        System.out.println("1. Events ansehen");
        System.out.println("2. Ticket buchen");
        System.out.println("3. Meine Tickets anzeigen");
        System.out.println("4. Profil bearbeiten");
        System.out.println("5. Benutzerverwaltung");
        System.out.println("6. Kategorieverwaltung");
        System.out.println("7. Orte verwalten");
        System.out.println("8. Events verwalten");
        System.out.println("9. Statistiken anzeigen");
        System.out.println("10. Daten exportieren");
        System.out.println("11. Ausloggen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    public void displayEventCreationForm() {
        System.out.println("\n===== NEUES EVENT ERSTELLEN =====");

        try {
            scanner.nextLine(); // Clear buffer

            System.out.print("Event-Titel: ");
            String title = scanner.nextLine();

            System.out.print("Beschreibung: ");
            String description = scanner.nextLine();


            System.out.print("Start-Datum (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine();

            System.out.print("Start-Uhrzeit (HH:MM): ");
            String startTimeStr = scanner.nextLine();

            System.out.print("End-Datum (YYYY-MM-DD): ");
            String endDateStr = scanner.nextLine();

            System.out.print("End-Uhrzeit (HH:MM): ");
            String endTimeStr = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDate = LocalDateTime.parse(startDateStr + " " + startTimeStr, formatter);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + " " + endTimeStr, formatter);

            System.out.print("Anzahl Tickets: ");
            int tickets = scanner.nextInt();

            System.out.println("\nVerfügbare Kategorien:");
            db.listAllCategories();
            System.out.print("Kategorie: ");
            int categoryId = scanner.nextInt();

            System.out.println("\nVerfügbare Orte:");
            db.listAllLocations();
            System.out.print("Ort-ID: ");
            int locationId = scanner.nextInt();


            boolean success = db.createEvent(title, description, startDate.toString(), endDate.toString(),auth.getCurrentUser().getUserId() ,categoryId, locationId, tickets);

            if (success) {
                System.out.println("Event '" + title + "' wurde erfolgreich erstellt!");
            } else {
                System.out.println("Fehler beim Erstellen des Events.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen des Events: " + e.getMessage());
        }
    }

    public void displayTicketBookingForm() {
        System.out.println("\n===== TICKET BUCHEN =====");

        try {
            // Show available events
            db.listAllEvents();

            System.out.print("Event-ID eingeben: ");
            int eventId = scanner.nextInt();

            System.out.print("Ticket-Typ-ID (1 für Standard): ");
            int ticketTypeId = scanner.nextInt();

            int userId = auth.getCurrentUser().getUserId();

            boolean success = db.bookTicket(eventId, userId, ticketTypeId);

            if (success) {
                System.out.println("Ticket wurde erfolgreich gebucht!");
            } else {
                System.out.println("Fehler beim Buchen des Tickets.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Buchen: " + e.getMessage());
        }
    }

    public void displayUserManagementMenu() {
        System.out.println("\n===== BENUTZERVERWALTUNG =====");
        System.out.println("1. Alle Benutzer anzeigen");
        System.out.println("2. Neuen Benutzer anlegen");
        System.out.println("3. Benutzer bearbeiten");
        System.out.println("4. Benutzer löschen");
        System.out.println("0. Zurück zum Hauptmenü");
        System.out.print("Ihre Wahl: ");
    }

    public void displayCategoryManagementMenu() {
        System.out.println("\n===== KATEGORIEVERWALTUNG =====");
        System.out.println("1. Alle Kategorien anzeigen");
        System.out.println("2. Neue Kategorie anlegen");
        System.out.println("3. Kategorie bearbeiten");
        System.out.println("4. Kategorie löschen");
        System.out.println("0. Zurück zum Hauptmenü");
        System.out.print("Ihre Wahl: ");
    }

    public void displayLocationManagementMenu() {
        System.out.println("\n===== ORTSVERWALTUNG =====");
        System.out.println("1. Alle Orte anzeigen");
        System.out.println("2. Neuen Ort anlegen");
        System.out.println("3. Ort bearbeiten");
        System.out.println("4. Ort löschen");
        System.out.println("0. Zurück zum Hauptmenü");
        System.out.print("Ihre Wahl: ");
    }

    public void displayStatisticsMenu() {
        System.out.println("\n===== STATISTIKEN =====");
        System.out.println("1. Events pro Benutzer");
        System.out.println("2. Events mit den meisten Buchungen");
        System.out.println("3. Beliebteste Kategorien");
        System.out.println("0. Zurück zum Hauptmenü");
        System.out.print("Ihre Wahl: ");
    }

    public void displayProfileEditForm() {
        System.out.println("\n===== PROFIL BEARBEITEN =====");

        try {
            scanner.nextLine(); // Clear buffer

            System.out.print("Neuer Vorname: ");
            String firstName = scanner.nextLine();

            System.out.print("Neuer Nachname: ");
            String lastName = scanner.nextLine();

            System.out.print("Neue Email: ");
            String email = scanner.nextLine();

            System.out.print("Neues Passwort: ");
            String password = scanner.nextLine();

            int userId = auth.getCurrentUser().getUserId();

            boolean success = db.updateUserProfile(userId, firstName, lastName, email, password);

            if (success) {
                System.out.println("Profil wurde erfolgreich aktualisiert!");
                // Update current user object
                auth.getCurrentUser().setFirstName(firstName);
                auth.getCurrentUser().setLastName(lastName);
                auth.getCurrentUser().setEmail(email);
            } else {
                System.out.println("Fehler beim Aktualisieren des Profils.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Bearbeiten des Profils: " + e.getMessage());
        }
    }

    public void displayUserEditForm() {
        System.out.println("\n===== BENUTZER BEARBEITEN =====");

        try {
            db.listAllUsers();

            System.out.print("Benutzer-ID eingeben: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            System.out.print("Neuer Vorname: ");
            String firstName = scanner.nextLine();

            System.out.print("Neuer Nachname: ");
            String lastName = scanner.nextLine();

            System.out.print("Neue Email: ");
            String email = scanner.nextLine();

            System.out.print("Neue Rolle (admin, organizer, user): ");
            String role = scanner.nextLine();

            boolean success = db.updateUser(userId, firstName, lastName, email, role);

            if (success) {
                System.out.println("Benutzer wurde erfolgreich aktualisiert!");
            } else {
                System.out.println("Fehler beim Aktualisieren des Benutzers.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Bearbeiten des Benutzers: " + e.getMessage());
        }
    }

    public void displayUserDeleteForm() {
        System.out.println("\n===== BENUTZER LÖSCHEN =====");

        try {
            db.listAllUsers();

            System.out.print("Benutzer-ID zum Löschen eingeben: ");
            int userId = scanner.nextInt();

            System.out.print("Sind Sie sicher? (j/n): ");
            String confirm = scanner.next();

            if (confirm.equalsIgnoreCase("j") || confirm.equalsIgnoreCase("ja")) {
                boolean success = db.deleteUser(userId);

                if (success) {
                    System.out.println("Benutzer wurde erfolgreich gelöscht!");
                } else {
                    System.out.println("Fehler beim Löschen des Benutzers.");
                }
            } else {
                System.out.println("Löschvorgang abgebrochen.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Löschen des Benutzers: " + e.getMessage());
        }
    }

    public void displayCategoryEditForm() {
        System.out.println("\n===== KATEGORIE BEARBEITEN =====");

        try {
            db.listAllCategories();

            System.out.print("Kategorie-ID eingeben: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            System.out.print("Neuer Name: ");
            String newName = scanner.nextLine();

            boolean success = db.updateCategory(categoryId, newName);

            if (success) {
                System.out.println("Kategorie wurde erfolgreich aktualisiert!");
            } else {
                System.out.println("Fehler beim Aktualisieren der Kategorie.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Bearbeiten der Kategorie: " + e.getMessage());
        }
    }

    public void displayCategoryDeleteForm() {
        System.out.println("\n===== KATEGORIE LÖSCHEN =====");

        try {
            db.listAllCategories();

            System.out.print("Kategorie-ID zum Löschen eingeben: ");
            int categoryId = scanner.nextInt();

            System.out.print("Sind Sie sicher? (j/n): ");
            String confirm = scanner.next();

            if (confirm.equalsIgnoreCase("j") || confirm.equalsIgnoreCase("ja")) {
                boolean success = db.deleteCategory(categoryId);

                if (success) {
                    System.out.println("Kategorie wurde erfolgreich gelöscht!");
                } else {
                    System.out.println("Fehler beim Löschen der Kategorie.");
                }
            } else {
                System.out.println("Löschvorgang abgebrochen.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Löschen der Kategorie: " + e.getMessage());
        }
    }

    public void displayEventDeleteForm() {
        System.out.println("\n===== EVENT LÖSCHEN =====");

        try {
            db.listAllEvents();

            System.out.print("Event-ID zum Löschen eingeben: ");
            int eventId = scanner.nextInt();

            System.out.print("Sind Sie sicher? Alle Tickets für dieses Event werden ebenfalls gelöscht! (j/n): ");
            String confirm = scanner.next();

            if (confirm.equalsIgnoreCase("j") || confirm.equalsIgnoreCase("ja")) {
                boolean success = db.deleteEvent(eventId);

                if (success) {
                    System.out.println("Event wurde erfolgreich gelöscht!");
                } else {
                    System.out.println("Fehler beim Löschen des Events.");
                }
            } else {
                System.out.println("Löschvorgang abgebrochen.");
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Löschen des Events: " + e.getMessage());
        }
    }

    public void displayMyTickets() {
        System.out.println("\n===== MEINE TICKETS =====");

        try {
            int userId = auth.getCurrentUser().getUserId();

            String sql = "SELECT t.ticketID, e.title, e.date, e.time, l.location, l.city " +
                    "FROM Ticket t " +
                    "JOIN Event e ON t.eventID = e.eventID " +
                    "JOIN location l ON e.locationID = l.locationID " +
                    "WHERE t.userID = ?";

            // This would need to be implemented in DatabaseConnection
            System.out.println("Ticket-Anzeige-Funktion muss noch in DatabaseConnection implementiert werden.");

        } catch (Exception e) {
            System.err.println("Fehler beim Anzeigen der Tickets: " + e.getMessage());
        }
    }

    public int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Bitte geben Sie eine gültige Zahl ein.");
            scanner.next();
        }
        return scanner.nextInt();
    }
}