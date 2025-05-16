import data.LoginInfo;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static DatabaseConnection db;
    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);
    private static ReaderWriter readerWriter = new ReaderWriter();
    private static UI ui = new UI(new AuthenticationSystem(connection));

    public static void main(String[] args) {
        // Überprüfen, ob data.LoginInfo bereits existiert oder erstellt werden muss
        LoginInfo login = readerWriter.loadLoginInfo();

        if (login == null) {
            // Beim ersten Start die Login-Informationen abfragen
            login = Settings.setupLoginInfo();

            // Login-Informationen speichern
            try {
                readerWriter.saveLoginInfo(login.getDbLink(), login.getUsername(), login.getPassword());
            } catch (Exception e) {
                System.err.println("Fehler beim Speichern der Login-Informationen:");
                e.printStackTrace();
            }
        }

        db = new DatabaseConnection(login);
        try {
            connection = db.loadDatabase();

            boolean running = true;
            while (running) {
                ui.displayMainMenu();
                int choice = getUserChoice();

                switch (choice) {
                    case 1:
                        db.listAllCategories();
                        break;
                    case 2:
                        db.createCategory();
                        break;
                    case 3:
                        db.listAllUsers();
                        break;
                    case 4:
                        db.createUser();
                        break;
                    case 5:
                        // Einstellungen ändern
                        login = Settings.changeLoginSettings(login);
                        try {
                            readerWriter.saveLoginInfo(login.getDbLink(), login.getUsername(), login.getPassword());
                            System.out.println("Die neuen Einstellungen wurden gespeichert.");
                            System.out.println("Bitte starten Sie das Programm neu, um die Änderungen zu übernehmen.");
                        } catch (Exception e) {
                            System.err.println("Fehler beim Speichern der Login-Informationen:");
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        // Datenbank als CSV exportieren
                        System.out.println("Exportiere Datenbank als CSV-Dateien...");
                        boolean success = readerWriter.exportDatabaseToCSV(connection);
                        if (success) {
                            System.out.println("CSV-Export erfolgreich abgeschlossen.");
                        } else {
                            System.err.println("CSV-Export konnte nicht vollständig durchgeführt werden.");
                        }
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Ungültige Auswahl. Bitte erneut versuchen.");
                }

                System.out.println(); // Leerzeile für bessere Lesbarkeit
            }

            // Verbindung schließen
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Datenbankverbindung geschlossen.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Treiber nicht gefunden!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankverbindung!");
            e.printStackTrace();
        }
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Bitte eine Zahl eingeben.");
            scanner.next();
        }
        return scanner.nextInt();
    }
}