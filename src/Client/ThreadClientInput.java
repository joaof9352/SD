package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadClientInput implements Runnable{
    private final int size = 5;
    private BufferedReader stringIn;
    private DataOutputStream writeSocket;
    private Socket socket;
    private ReentrantLock lock;
    private Condition cond;

    private AuthenticationSingleton as;

    private static String[] opcoes1 = {
            "Autenticar-se",
            "Registar-se"};
    private static String[] opcoes2 = { // Utilizador
            "Reservar um Voo", // 1
            "Cancelar uma reserva", // 2
            "Ver todos os voos diretos", //3
            "Ver todos os voos possíveis até 2 escalas", //4
            "Consultar reserva" //5
    };
    private static String[] opcoes3 = { // Administrador
            "Adicionar um novo voo", //1
            "Ver todos os voos diretos", //2
            "Ver todos os voos possíveis até 2 escalas", //3
            "Bloquear um dia" //4
    };

    public ThreadClientInput (Socket s, ReentrantLock l, Condition c){
        try {
            this.stringIn = new BufferedReader(new InputStreamReader(System.in));
            this.writeSocket = new DataOutputStream(s.getOutputStream());
            this.socket = s;
            this.lock = l;
            this.cond = c;
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        int i = 1;
        boolean authenticated = false;
        try {
            while(true){
                if (!(AuthenticationSingleton.getInstance().isAuthenticated()) ) {
                    Menu menu = new Menu(opcoes1);
                    menu.execute();
                    if(menu.getOpcao() == 1){
                        while (!(AuthenticationSingleton.getInstance().isAuthenticated())) {
                            String password = null;
                            String username = null;
                            try {
                                System.out.print("\nInsira o seu nome de utilizador: ");
                                username = stringIn.readLine();
                                System.out.print("Insira a sua password: ");
                                password = stringIn.readLine();
                            } catch (InputMismatchException e) {
                                System.out.println(e.toString());
                            }
                            writeSocket.writeUTF("login");
                            writeSocket.flush();
                            //Enviar dados para o servidor -> Username
                            writeSocket.writeUTF(username);
                            writeSocket.flush();
                            //Enviar dados para o servidor -> Password
                            writeSocket.writeUTF(password);
                            writeSocket.flush();
                            //Aguardar resposta do servidor
                            lock.lock();
                            cond.await();
                            lock.unlock();

                            if (AuthenticationSingleton.getInstance().isAuthenticated()) {
                                AuthenticationSingleton.getInstance().setPassword(password);
                                AuthenticationSingleton.getInstance().setUsername(username);
                            }
                        }
                    } else if (menu.getOpcao() == 2){
                        String password = null;
                        String username = null;
                        boolean isAdmin = false;
                        try {
                            System.out.println("\nInsira o seu nome de utilizador: ");
                            username = stringIn.readLine();
                            System.out.println("Insira a sua password: ");
                            password = stringIn.readLine();
                            System.out.println("É admin? 1 se sim, qualquer outra coisa se não!");
                            isAdmin = stringIn.readLine().equals("1") ? true : false;
                        } catch (InputMismatchException e) {
                            System.out.println(e.toString());
                        }
                        System.out.println("Registo");
                        writeSocket.writeUTF("Registo");
                        writeSocket.flush();
                        //Enviar dados para o servidor -> Username
                        System.out.println(username);
                        writeSocket.writeUTF(username);
                        writeSocket.flush();
                        //Enviar dados para o servidor -> Password
                        System.out.println(password);
                        writeSocket.writeUTF(password);
                        writeSocket.flush();
                        //Enviar isAdmin
                        System.out.println(isAdmin);
                        writeSocket.writeBoolean(isAdmin);
                        writeSocket.flush();
                        //Aguardar resposta do servidor
                        lock.lock();
                        cond.await();
                        lock.unlock();
                    } else if(menu.getOpcao() == 0) {
                        break;
                    }
                } else {
                    if (AuthenticationSingleton.getInstance().isAdmin()){
                        Menu menu = new Menu(opcoes3);
                        menu.execute();
                        int k = menu.getOpcao();
                        if(k == 0) break;
                        switch(k){
                            case 1:
                                System.out.println("Qual a cidade de origem?");
                                String origin = stringIn.readLine();
                                System.out.println("Qual a cidade de destino?");
                                String destination = stringIn.readLine();
                                System.out.println("Qual a capacidade do avião?");
                                String capacidade = stringIn.readLine();
                                writeSocket.writeUTF("NovoVoo"); writeSocket.flush();
                                writeSocket.writeUTF(origin);
                                writeSocket.flush();
                                writeSocket.writeUTF(destination);
                                writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(capacidade));
                                writeSocket.flush();
                                break;
                            case 2:
                                writeSocket.writeUTF("TodosVoosEscalas");
                                writeSocket.flush();
                                lock.lock();
                                cond.await();
                                lock.unlock();
                                break;
                            case 3:
                                writeSocket.writeUTF("TodosVoos");
                                writeSocket.flush();
                                lock.lock();
                                cond.await();
                                lock.unlock();
                                break;
                            case 4:
                                System.out.println("Dia a bloquear:");
                                String dia = stringIn.readLine();
                                System.out.println("Mês do dia a bloquear:");
                                String mes = stringIn.readLine();
                                System.out.println("Ano do dia a bloquear:");
                                String ano = stringIn.readLine();
                                writeSocket.writeUTF("BloquearDia"); writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(ano)); writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(mes)); writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(dia)); writeSocket.flush();
                                lock.lock();
                                cond.await();
                                lock.unlock();
                                break;
                        }
                    } else {

                        Menu menu = new Menu(opcoes2);
                        menu.execute();
                        int k = menu.getOpcao();
                        if(k == 0) break;

                        switch(k){
                            case 1: //Pode comprar 1 voo para já, alterar para poder enviar vários voos
                                List<String> airports = new ArrayList<>();
                                System.out.println("Introduza o número de voos que quer comprar: ");
                                int numberOfFlights = Integer.parseInt(stringIn.readLine());
                                if(numberOfFlights < 1){
                                    System.out.println("Erro: Número de voos tem de ser positivo.");
                                } else {
                                    int j = 0;

                                    while (j < numberOfFlights+1) {
                                        System.out.println("Introduza a cidade seguinte:");
                                        airports.add(stringIn.readLine());
                                        j++;
                                    }

                                    System.out.println("Dia mínimo da viagem:");
                                    String dia = stringIn.readLine();
                                    System.out.println("Mês mínimo da viagem:");
                                    String mes = stringIn.readLine();
                                    System.out.println("Ano mínimo da viagem:");
                                    String ano = stringIn.readLine();
                                    System.out.println("Dia máximo da viagem:");
                                    String diaMax = stringIn.readLine();
                                    System.out.println("Mês máximo da viagem:");
                                    String mesMax = stringIn.readLine();
                                    System.out.println("Ano máximo da viagem:");
                                    String anoMax = stringIn.readLine();
                                    writeSocket.writeUTF("NovaCompra");

                                    writeSocket.writeUTF(AuthenticationSingleton.getInstance().getUsername());
                                    j=0;
                                    while (j < numberOfFlights+1) {
                                        writeSocket.writeUTF(airports.get(j)); writeSocket.flush();
                                        j++;
                                    }
                                    writeSocket.writeUTF("FIN"); writeSocket.flush();

                                    writeSocket.writeInt(Integer.parseInt(ano)); writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(mes)); writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(dia)); writeSocket.flush();

                                    writeSocket.writeInt(Integer.parseInt(anoMax)); writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(mesMax)); writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(diaMax)); writeSocket.flush();
                                }
                                break;
                            case 2:
                                System.out.println("Qual o código da reserva que quer cancelar?");
                                writeSocket.writeUTF("CancelaReserva"); writeSocket.flush();
                                writeSocket.writeUTF(stringIn.readLine()); writeSocket.flush();
                            case 3:
                                writeSocket.writeUTF("TodosVoos"); writeSocket.flush();
                                System.out.println("Todos os voos até 2 escalas: ");
                                break;
                            case 4:
                                writeSocket.writeUTF("TodosVoosEscalas"); writeSocket.flush();
                                System.out.println("Todos os voos disponíveis: ");
                                break;
                            case 5:
                                System.out.println("Qual a reserva que quer consultar?");
                                int reservID = Integer.parseInt(stringIn.readLine());
                                writeSocket.writeUTF("ConsultarReserva");
                                writeSocket.writeUTF(AuthenticationSingleton.getInstance().getUsername());
                                writeSocket.writeInt(reservID);
                                break;
                        }
                        lock.lock();
                        cond.await();
                        lock.unlock();

                    }
                }
            }
            System.out.println("TAP Portugal - Volte sempre.");
            writeSocket.close();
            socket.shutdownOutput();

        } catch(Exception e) {

        }
    }
}