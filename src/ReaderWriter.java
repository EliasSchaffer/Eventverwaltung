import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

public class ReaderWriter {

    private static final String MASTER_KEY = "bEurG5yB3BgPk0vbMJgs0KW0TbxxjO9OdV9bbiFzre7fM4eya0";
    private static final String FILE_NAME = "logininfo.dat";

    private static SecretKey getAESKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // 128-bit AES
        return new SecretKeySpec(key, "AES");
    }

    public static void saveLoginInfo(String dbLink, String username, String password) throws Exception {
        try {
            SecretKey key = getAESKey(MASTER_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(FILE_NAME), cipher);
                 ObjectOutputStream oos = new ObjectOutputStream(cos)) {
                oos.writeObject(new LoginInfo(dbLink, username, password));
                System.out.println("LoginInfo erfolgreich verschlüsselt gespeichert.");
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern der LoginInfo:");
            e.printStackTrace();
        }
    }

    public static LoginInfo loadLoginInfo() {
        try {
            SecretKey key = getAESKey(MASTER_KEY);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            try (CipherInputStream cis = new CipherInputStream(new FileInputStream(FILE_NAME), cipher);
                 ObjectInputStream ois = new ObjectInputStream(cis)) {
                return (LoginInfo) ois.readObject();
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der LoginInfo:");
            e.printStackTrace();
            return null;
        }
    }
}
