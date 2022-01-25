package Client;

public class AuthenticationSingleton {

    private static Authentication instance = null;

    public AuthenticationSingleton(){}

    public static Authentication getInstance(){
        if (instance == null)
            instance = new Authentication();
        return instance;
    }
}
