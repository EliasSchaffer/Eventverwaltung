import data.LoginInfo;

import java.util.Scanner;

public class Settings {
    private static Scanner scanner = new Scanner(System.in);

    // Fragt die Benutzereinstellungen ab und gibt ein data.LoginInfo-Objekt zurück
    public static LoginInfo setupLoginInfo() {
        System.out.println("===== ERSTEINRICHTUNG =====");
        System.out.print("Möchten Sie einen externen Datenbankserver verwenden? (j/n): ");
        String choice = scanner.nextLine().toLowerCase();

        String dbLink;
        String username;
        String password;

        if (choice.equals("j") || choice.equals("ja")) {
            System.out.print("Geben Sie die Datenbankverbindungs-URL ein: ");
            dbLink = scanner.nextLine();

            System.out.print("Benutzername: ");
            username = scanner.nextLine();

            System.out.print("Passwort: ");
            password = scanner.nextLine();
        } else {
            // Standard-Einstellungen für lokalen Server
            dbLink = "jdbc:mysql://localhost:3306/eventverwaltung";

            System.out.print("Benutzername für lokalen MySQL-Server: ");
            username = scanner.nextLine();

            System.out.print("Passwort für lokalen MySQL-Server: ");
            password = scanner.nextLine();
        }

        return new LoginInfo(dbLink, username, password);
    }

    // Ermöglicht das Ändern der Datenbankverbindungseinstellungen
    public static LoginInfo changeLoginSettings(LoginInfo currentLoginInfo) {
        System.out.println("===== DATENBANKEINSTELLUNGEN ÄNDERN =====");
        System.out.println("Aktuelle Verbindung: " + currentLoginInfo.getDbLink());
        System.out.println("Aktueller Benutzer: " + currentLoginInfo.getUsername());

        System.out.print("Möchten Sie die Datenbankverbindung ändern? (j/n): ");
        String choice = scanner.nextLine().toLowerCase();

        if (choice.equals("j") || choice.equals("ja")) {
            System.out.print("Neue Datenbankverbindungs-URL (oder freilasse für aktuelle): ");
            String dbLink = scanner.nextLine();
            if (!dbLink.isEmpty()) {
                currentLoginInfo.setDbLink(dbLink);
            }

            System.out.print("Neuer Benutzername (oder freilasse für aktuellen): ");
            String username = scanner.nextLine();
            if (!username.isEmpty()) {
                currentLoginInfo.setUsername(username);
            }

            System.out.print("Neues Passwort (oder freilasse für aktuelles): ");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                currentLoginInfo.setPassword(password);
            }

            System.out.println("Datenbankeinstellungen wurden aktualisiert.");
        }

        return currentLoginInfo;
    }
}