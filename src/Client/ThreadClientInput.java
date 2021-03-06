package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadClientInput implements Runnable{
    private BufferedReader stringIn;
    private DataOutputStream writeSocket;
    private Socket socket;
    private ReentrantLock lock;
    private Condition cond;

    private static final String[] opcoes1 = {
            "Autenticar-se",
            "Registar-se"};
    private static final String[] opcoes2 = { // Utilizador
            "Reservar um Voo", // 1
            "Cancelar uma reserva", // 2
            "Ver todos os voos diretos", //3
            "Consultar reserva" //4
    };
    private static final String[] opcoes3 = { // Administrador
            "Adicionar um novo voo", //1
            "Ver todos os voos diretos", //2
            "Bloquear um dia" //3
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
        try {
            while(true) {
                if (!(AuthenticationSingleton.getInstance().isAuthenticated()) ) {
                    Menu menu = new Menu(opcoes1);
                    menu.execute();

                    if (menu.getOpcao() == 1) {
                        String password = null;
                        String username = null;
                        try {
                            System.out.print("\nInsira o seu nome de utilizador: ");
                            username = stringIn.readLine();
                            System.out.print("Insira a sua password: ");
                            password = stringIn.readLine();
                        } catch (InputMismatchException e) {
                            System.out.println(e.getMessage());
                        }
                        writeSocket.writeUTF("login");writeSocket.flush();
                        //Enviar dados para o servidor -> Username
                        writeSocket.writeUTF(username);writeSocket.flush();
                        //Enviar dados para o servidor -> Password
                        writeSocket.writeUTF(password);writeSocket.flush();

                        //Aguardar resposta do servidor
                        lock.lock();
                        cond.await();
                        lock.unlock();

                        if (AuthenticationSingleton.getInstance().isAuthenticated()) {
                            AuthenticationSingleton.getInstance().setPassword(password);
                            AuthenticationSingleton.getInstance().setUsername(username);
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
                            System.out.println("?? admin? 1 se sim, qualquer outra coisa se n??o!");
                            isAdmin = stringIn.readLine().equals("1");
                        } catch (InputMismatchException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("Registo");
                        writeSocket.writeUTF("Registo");writeSocket.flush();
                        //Enviar dados para o servidor -> Username
                        System.out.println(username);
                        writeSocket.writeUTF(username);writeSocket.flush();
                        //Enviar dados para o servidor -> Password
                        System.out.println(password);
                        writeSocket.writeUTF(password);writeSocket.flush();
                        //Enviar isAdmin
                        System.out.println(isAdmin);
                        writeSocket.writeBoolean(isAdmin);writeSocket.flush();

                        //Aguardar resposta do servidor
                        lock.lock();
                        cond.await();
                        lock.unlock();
                    } else if (menu.getOpcao() == 0) break;
                } else {
                    if (AuthenticationSingleton.getInstance().isAdmin()) {
                        Menu menu = new Menu(opcoes3);
                        menu.execute();
                        int k = menu.getOpcao();
                        if (k == 0) break;
                        switch (k) {
                            case 1 -> {
                                System.out.println("Qual a cidade de origem?");
                                String origin = stringIn.readLine();
                                System.out.println("Qual a cidade de destino?");
                                String destination = stringIn.readLine();
                                System.out.println("Qual a capacidade do avi??o?");
                                String capacidade = stringIn.readLine();
                                System.out.println("C??digo do voo?");
                                String code = stringIn.readLine();
                                writeSocket.writeUTF("NovoVoo");writeSocket.flush();
                                writeSocket.writeUTF(code);writeSocket.flush();
                                writeSocket.writeUTF(origin);writeSocket.flush();
                                writeSocket.writeUTF(destination);writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(capacidade));writeSocket.flush();
                            }
                            case 2 -> {
                                writeSocket.writeUTF("TodosVoos");
                                writeSocket.flush();
                            }
                            case 3 -> {
                                System.out.println("Dia a bloquear:");
                                String dia = stringIn.readLine();
                                System.out.println("M??s do dia a bloquear:");
                                String mes = stringIn.readLine();
                                System.out.println("Ano do dia a bloquear:");
                                String ano = stringIn.readLine();
                                writeSocket.writeUTF("BloquearDia");writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(ano));writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(mes));writeSocket.flush();
                                writeSocket.writeInt(Integer.parseInt(dia));writeSocket.flush();
                            }
                        }

                        // Aguardar resposta do servidor
                        lock.lock();
                        cond.await();
                        lock.unlock();
                    } else {
                        Menu menu = new Menu(opcoes2);
                        menu.execute();
                        int k = menu.getOpcao();
                        if (k == 0) break;

                        switch (k) {
                            case 1 -> { // Pode comprar 1 voo para j??, alterar para poder enviar v??rios voos
                                List<String> airports = new ArrayList<>();
                                System.out.println("Introduza o n??mero de voos que quer comprar: ");
                                int numberOfFlights = Integer.parseInt(stringIn.readLine());
                                if (numberOfFlights < 1) {
                                    System.out.println("Erro: N??mero de voos tem de ser positivo.");
                                } else {
                                    int j = 0;
                                    while (j < numberOfFlights + 1) {
                                        System.out.println("Introduza a cidade seguinte:");
                                        airports.add(stringIn.readLine());
                                        j++;
                                    }

                                    System.out.println("Dia m??nimo da viagem:");
                                    String dia = stringIn.readLine();
                                    System.out.println("M??s m??nimo da viagem:");
                                    String mes = stringIn.readLine();
                                    System.out.println("Ano m??nimo da viagem:");
                                    String ano = stringIn.readLine();
                                    System.out.println("Dia m??ximo da viagem:");
                                    String diaMax = stringIn.readLine();
                                    System.out.println("M??s m??ximo da viagem:");
                                    String mesMax = stringIn.readLine();
                                    System.out.println("Ano m??ximo da viagem:");
                                    String anoMax = stringIn.readLine();
                                    writeSocket.writeUTF("NovaCompra");

                                    writeSocket.writeUTF(AuthenticationSingleton.getInstance().getUsername());
                                    j = 0;
                                    while (j < numberOfFlights + 1) {
                                        writeSocket.writeUTF(airports.get(j));
                                        writeSocket.flush();
                                        j++;
                                    }
                                    writeSocket.writeUTF("FIN");writeSocket.flush();

                                    writeSocket.writeInt(Integer.parseInt(ano));writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(mes));writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(dia));writeSocket.flush();

                                    writeSocket.writeInt(Integer.parseInt(anoMax));writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(mesMax));writeSocket.flush();
                                    writeSocket.writeInt(Integer.parseInt(diaMax));writeSocket.flush();
                                }
                            }
                            case 2 -> {
                                System.out.println("Qual o c??digo da reserva que quer cancelar?");
                                int reservationID = Integer.parseInt(stringIn.readLine());
                                writeSocket.writeUTF("CancelaReserva");writeSocket.flush();
                                writeSocket.writeUTF(AuthenticationSingleton.getInstance().getUsername());writeSocket.flush();
                                writeSocket.writeInt(reservationID);writeSocket.flush();
                            }
                            case 3 -> {
                                writeSocket.writeUTF("TodosVoos");
                                writeSocket.flush();
                            }
                            case 4 -> {
                                System.out.println("Qual a reserva que quer consultar ?");
                                int reservID = Integer.parseInt(stringIn.readLine());
                                writeSocket.writeUTF("ConsultarReserva");
                                writeSocket.writeUTF(AuthenticationSingleton.getInstance().getUsername());
                                writeSocket.writeInt(reservID);
                            }
                        }

                        // Aguardar resposta do servidor
                        lock.lock();
                        cond.await();
                        lock.unlock();
                    }
                }
            }
            System.out.println("TAP Portugal - Volte sempre.");
            writeSocket.close();
            socket.shutdownOutput();
        } catch(Exception ignored) {
        }
    }
}