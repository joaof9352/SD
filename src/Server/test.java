package Server;

import Server.Exceptions.UserAlreadyExistsException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;


public class test {

    public static void main(String[] args) throws IOException, UserAlreadyExistsException {
        Company c = new Company();
        c.addNewRecurrentFlight("0001","Porto", "Lisboa", 1);
        c.addNewRecurrentFlight("0002","Porto", "Faro", 1);
        c.addNewRecurrentFlight("0003","Lisboa", "Faro", 1);
        c.addNewRecurrentFlight("0004", "Faro","Lisboa",1);
        LocalDate date = LocalDate.of(2022,01,03);
        UserListSingleton.getInstance().signUp("admin","admin",true);
        UserListSingleton.getInstance().signUp("user","user",false);
        UserListSingleton.getInstance().signUp("user2","user2",false);

        Socket s = null;

        ServerSocket ss = new ServerSocket(12345);

        while (true) {

            s = ss.accept();
            System.out.println("Nova ligação!");
            Thread client = new Thread(new ClientHandler(s,c));
            client.start();
        }

    }
}
