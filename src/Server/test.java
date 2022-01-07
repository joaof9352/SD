package Server;

import javafx.util.Pair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

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

        ServerSocket ss;
        Socket s = null;

        try{
            ss = new ServerSocket(12345);

            while((s = ss.accept()) != null){
                System.out.println("Nova ligação!");
                Thread client = new Thread(new ClientHandler(s,c));
                client.run();
                client.join();
            }

            ss.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
