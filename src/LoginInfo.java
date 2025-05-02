public class LoginInfo {
    String dbLink;
    String username;
    String password;

    public LoginInfo(String dbLink, String username, String password) {
        this.dbLink = dbLink;
        this.username = username;
        this.password = password;
    }

    public String getDbLink() {
        return dbLink;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setDbLink(String dbLink) {
        this.dbLink = dbLink;
    }
}
