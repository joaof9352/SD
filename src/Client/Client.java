package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class implements the executable main method for each Client
 */

public class Client {
    public static void main (String[] args){

        // Initializing necessary variables
        Socket socket;
        ReentrantLock lock = new ReentrantLock();
        Condition cond = lock.newCondition();

        try{
            socket = new Socket("localhost",12345);

            DataInputStream ler_socket = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            Thread tci = new Thread (new ThreadClientInput(socket, lock, cond));
            Thread tco = new Thread (new ThreadClientOutput(ler_socket,lock, cond));

            tci.start();
            tco.start();

            tci.join();
            tco.join();

            ler_socket.close();

            System.out.println("Obrigado por escolher a TAP. A chegar atrasados juntos desde 1945\n");
            socket.close();

        } catch (IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}
