//Claude

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static DatabaseConnection db;
    private static Scanner scanner = new Scanner(System.in);
    private static ReaderWriter readerWriter = new ReaderWriter();
    private static UI ui = new UI();

    public static void main(String[] args) {
        LoginInfo login = readerWriter.loadLoginInfo();
        if (login.getDbLink().equals("none")) {
            login.setDbLink("jdbc:mysql://localhost:3306/login");
        }
        db = new DatabaseConnection(login);
        try {
            Connection connection = db.loadDatabase();


            boolean running = true;
            while (running) {
                ui.displayMenu();
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

    // 1. Alle Einträge der Tabelle 'category' auflisten

}