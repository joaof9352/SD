package Client;

import java.io.DataInputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadClientOutput implements Runnable{
    private DataInputStream readSocket;
    private ReentrantLock lock;
    private Condition cond;

    public ThreadClientOutput(DataInputStream rs, ReentrantLock l, Condition c){
        this.readSocket = rs;
        this.lock = l;
        this.cond = c;
    }

    public void run() {
        try {
            String line;

            while((line = readSocket.readUTF()) != null) {
                if (line.equals("Registado")) {
                    System.out.println("Registado!");
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                } else if (line.equals("Registo falhou")) {
                    System.out.println("Registo falhou, username já existe!");
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                } else if (line.equals("Sessão iniciada como Admin!")) {
                    System.out.println("Sessão iniciada!");
                    this.lock.lock();
                    AuthenticationSingleton.getInstance().setAuthenticated(true);
                    AuthenticationSingleton.getInstance().setAdmin(true);
                    cond.signal();
                    this.lock.unlock();
                } else if (line.equals("Sessão iniciada!")) {
                    System.out.println("Sessão iniciada!");
                    this.lock.lock();
                    AuthenticationSingleton.getInstance().setAuthenticated(true);
                    cond.signal();
                    this.lock.unlock();
                } else if (line.equals("ERRO: Username não existe") || line.equals("ERRO: Password incorreta")
                          || line.startsWith("Reserva efetuada com código de reserva")
                          || line.startsWith("Dia bloqueado: ") || line.equals("ERRO: Reserva impossível.")
                          || line.equals("ERRO: A reserva não lhe pertence.")) { //Erro ao iniciar sessão
                    System.out.println(line);
                    this.lock.lock();
                    cond.signal();
                    this.lock.unlock();
                } else if (line.equals("Todos os voos até 2 escalas: ")) {
                    this.lock.lock();
                    System.out.println(line);
                    while (!(line = readSocket.readUTF()).equals("FIM")) {
                        System.out.println(line);
                    }
                    cond.signal();
                    this.lock.unlock();
                } else if (line.startsWith("Reserva num. ") || line.equals("Todos os voos disponíveis: ")) {
                    System.out.println(line);
                    System.out.println(readSocket.readUTF());
                    this.lock.lock();
                    this.cond.signal();
                    this.lock.unlock();
                } else {
                    this.lock.lock();
                    System.out.println(line);
                    this.cond.signal();
                    this.lock.unlock();
                }
            }
        } catch (Exception ignored){}
    }
}