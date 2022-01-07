import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Socket s = ss.accept();

        DataInputStream readSocket = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        DataOutputStream writeSocket = new DataOutputStream(s.getOutputStream());

        while(!s.isClosed()){
            String line = readSocket.readUTF();
            switch (line){
                case "TodosVoosEscalas":
                    System.out.println(line);
                    writeSocket.writeUTF("Todos os voos disponíveis: ");
                    writeSocket.writeUTF("FIM"); writeSocket.flush();
                    break;
                case "AUTENTICAÇÃO":
                    System.out.println(line);
                    writeSocket.writeUTF("Sessão iniciada como Admin!"); writeSocket.flush();
                    break;
            }
        }
    }
}
