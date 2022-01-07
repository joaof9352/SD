package Client;

import java.io.DataInputStream;
import java.sql.SQLOutput;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadClientOutput implements Runnable{
    private DataInputStream readSocket;
    private ReentrantLock lock;
    private Condition cond;

    public ThreadClientOutput(DataInputStream rs, ReentrantLock l, Condition c){
        this.readSocket = rs;
        this.lock=l;
        this.cond=c;
    }

    public void run() {
        try{
            String line;

            while((line = readSocket.readUTF()) != null) {
                if(line.equals("Sessão iniciada como Admin!")){
                    System.out.println("Sessão iniciada!");
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                    AuthenticationSingleton.getInstance().setAuthenticated(true);
                    AuthenticationSingleton.getInstance().setAdmin(true);
                } else if(line.equals("Sessão iniciada como Client")){
                    System.out.println("Sessão iniciada!");
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                    AuthenticationSingleton.getInstance().setAuthenticated(true);

                } else if(line.equals("ERRO: Username não existe") || line.equals("ERRO: Password incorreta")
                          || line.startsWith("Compra efetuada com código de reserva")
                          || line.equals("Reserva cancelada")){ //Erro ao iniciar sessão
                    System.out.println(line);
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                } else if(line.equals("Todos os voos até 2 escalas: ") || line.equals("Todos os voos disponíveis: ")) {
                    this.lock.lock();
                    System.out.println(line);
                    while (!(line = readSocket.readUTF()).equals("FIM")) {
                        System.out.println(line);
                    }
                    cond.signal();
                    this.lock.unlock();
                }
            }
        } catch (Exception e){
        }
    }
}