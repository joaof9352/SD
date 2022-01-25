package PlataformaVoos;

public class User {

    String username;
    String password;
    boolean admin;

    public User(String username, String password, boolean admin){
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public boolean isAdmin() {
        return admin;
    }
}
