package PlataformaVoos;

public class UserListSingleton {

    private static UserList instance;

    public static UserList getInstance() {

        if(instance == null){
            instance = new UserList();
        }
        return instance;
    }
}
