package Client;

public class Authentication {
    private String username;
    private String password;
    private boolean authenticated;
    private boolean admin;

    public Authentication(){
        this.username = null;
        this.password = null;
        this.authenticated = false;
        this.admin = false;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }
}

