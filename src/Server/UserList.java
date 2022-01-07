package Server;

import Server.Exceptions.IncorrectPasswordException;
import Server.Exceptions.UserAlreadyExistsException;
import Server.Exceptions.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class UserList {

    Map<String, User> userMap = null;

    public UserList(){
        userMap = new HashMap<>();
    }

    /* Authentication */
    public void signUp(String username, String password, boolean admin) throws UserAlreadyExistsException {
        if (userMap.containsKey(username)) {
            throw new UserAlreadyExistsException();
        } else {
            userMap.put(username, new User(username, password, admin));
        }
    }

    public void signIn(String username, String password) throws IncorrectPasswordException, UserNotFoundException {
        if (userMap.containsKey(username)){
            if(!userMap.get(username).checkPassword(password)){
                throw new IncorrectPasswordException();
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public boolean isAdmin(String username){
        return userMap.get(username).isAdmin();
    }
}
