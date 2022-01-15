package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;


public class test {

    public static void main(String[] args) throws IOException {
        Company c = new Company();
        c.addNewRecurrentFlight("0001","Porto", "Lisboa", 245);
        c.addNewRecurrentFlight("0002","Porto", "Faro", 290);
        c.addNewRecurrentFlight("0003","Lisboa", "Faro", 130);
        c.addNewRecurrentFlight("0004", "Faro","Lisboa",120);
        LocalDate date = LocalDate.of(2022,01,03);
        //Pair<Pair<String,String>,LocalDate> flight = new Pair<>(new Pair<>("Porto","Lisboa"),date);
        //c.makeReservation()

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
