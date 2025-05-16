
public class UI {
    private AuthenticationSystem auth;

    public UI(AuthenticationSystem auth) {
        this.auth = auth;
    }

    public void displayMainMenu() {
        System.out.println("\n===== EVENT PLATFORM =====");

        if (!auth.isLoggedIn()) {
            displayGuestMenu();
        } else {
            String role = auth.getCurrentUser().getRole().toLowerCase();

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

    private void displayGuestMenu() {
        System.out.println("1. Einloggen");
        System.out.println("2. Registrieren");
        System.out.println("3. Events ansehen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    private void displayUserMenu() {
        System.out.println("1. Events ansehen");
        System.out.println("2. Ticket buchen");
        System.out.println("3. Meine Tickets anzeigen");
        System.out.println("4. Profil bearbeiten");
        System.out.println("5. Ausloggen");
        System.out.println("0. Programm beenden");
        System.out.print("Ihre Wahl: ");
    }

    private void displayOrganizerMenu() {
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

    private void displayAdminMenu() {
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
        // TODO implementieren
    }

    public void displayTicketBookingForm() {
        System.out.println("\n===== TICKET BUCHEN =====");
        // TODO implementieren
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
}