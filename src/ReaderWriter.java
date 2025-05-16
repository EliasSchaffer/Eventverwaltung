import data.LoginInfo;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import data.LoginInfo;

public class ReaderWriter {

    private static final String MASTER_KEY = "bEurG5yB3BgPk0vbMJgs0KW0TbxxjO9OdV9bbiFzre7fM4eya0";
    private static final String FILE_NAME = "logininfo.dat";
    private static final String EXPORT_DIRECTORY = "dbexport";

    private static SecretKey getAESKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // 128-bit AES
        return new SecretKeySpec(key, "AES");
    }

    public void saveLoginInfo(String dbLink, String username, String password) throws Exception {
        try {
            SecretKey key = getAESKey(MASTER_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(FILE_NAME), cipher);
                 ObjectOutputStream oos = new ObjectOutputStream(cos)) {
                oos.writeObject(new LoginInfo(dbLink, username, password));
                System.out.println("data.LoginInfo erfolgreich verschlüsselt gespeichert.");
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern der data.LoginInfo:");
            e.printStackTrace();
            throw e; // Weitergabe der Exception für weitere Behandlung
        }
    }

    public LoginInfo loadLoginInfo() {
        File file = new File(FILE_NAME);

        // Überprüfen, ob die Datei existiert
        if (!file.exists()) {
            System.out.println("Keine gespeicherten Login-Informationen gefunden.");
            return null;
        }

        try {
            SecretKey key = getAESKey(MASTER_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            try (CipherInputStream cis = new CipherInputStream(new FileInputStream(FILE_NAME), cipher);
                 ObjectInputStream ois = new ObjectInputStream(cis)) {
                LoginInfo info = (LoginInfo) ois.readObject();
                System.out.println("Login-Informationen erfolgreich geladen.");
                return info;
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der data.LoginInfo:");
            e.printStackTrace();
            return null;
        }
    }


    public boolean exportDatabaseToCSV(Connection connection) {
        // Tabellen, die exportiert werden sollen
        String[] tables = {"User", "data.Event", "data.Ticket", "TicketType", "EventTicketCategorie", "Location", "category"};

        // Verzeichnis für den Export erstellen, falls es nicht existiert
        File directory = new File(EXPORT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        boolean success = true;

        try {
            for (String tableName : tables) {
                if (!exportTableToCSV(connection, tableName)) {
                    success = false;
                }
            }

            if (success) {
                System.out.println("Alle Tabellen wurden erfolgreich nach CSV exportiert.");
                System.out.println("Exportverzeichnis: " + new File(EXPORT_DIRECTORY).getAbsolutePath());
            } else {
                System.err.println("Beim Export der Tabellen sind Fehler aufgetreten.");
            }

            return success;
        } catch (Exception e) {
            System.err.println("Fehler beim Export der Datenbank:");
            e.printStackTrace();
            return false;
        }
    }


    private boolean exportTableToCSV(Connection connection, String tableName) {
        String fileName = EXPORT_DIRECTORY + File.separator + tableName + ".csv";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // Spaltenüberschriften ermitteln und schreiben
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder headerLine = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) {
                    headerLine.append(",");
                }
                headerLine.append("\"").append(metaData.getColumnName(i)).append("\"");
            }
            writer.write(headerLine.toString());
            writer.newLine();

            // Datensätze schreiben
            while (rs.next()) {
                StringBuilder dataLine = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        dataLine.append(",");
                    }

                    String value = rs.getString(i);
                    if (value == null) {
                        dataLine.append("NULL");
                    } else {
                        // Sonderzeichen in CSV escapen (doppelte Anführungszeichen und Kommas)
                        value = value.replace("\"", "\"\"");
                        dataLine.append("\"").append(value).append("\"");
                    }
                }
                writer.write(dataLine.toString());
                writer.newLine();
            }

            System.out.println("Tabelle '" + tableName + "' wurde erfolgreich exportiert: " + fileName);
            return true;

        } catch (SQLException | IOException e) {
            System.err.println("Fehler beim Exportieren der Tabelle '" + tableName + "':");
            e.printStackTrace();
            return false;
        }
    }
}