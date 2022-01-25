package Server;

import PlataformaVoos.AuxReservation;
import PlataformaVoos.Company;
import PlataformaVoos.UserListSingleton;
import Exceptions.IncorrectPasswordException;
import Exceptions.UserAlreadyExistsException;
import Exceptions.UserNotFoundException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{

        private Socket s;
        private Company company;
        private DataInputStream readSocket;
        private DataOutputStream writeSocket;

        public ClientHandler (Socket s, Company c) throws IOException {
            this.s = s;
            this.company = c;
            this.readSocket = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            this.writeSocket = new DataOutputStream(s.getOutputStream());
        }

        public void run(){
            try {
                String input;
                while((input = readSocket.readUTF()) != null) {
                    System.out.println(input);
                    switch (input) {
                        case "Registo" -> {
                            System.out.println(input);
                            String username, password;
                            boolean isAdmin;
                            username = readSocket.readUTF();
                            password = readSocket.readUTF();
                            isAdmin = readSocket.readBoolean();
                            try {
                                UserListSingleton.getInstance().signUp(username, password, isAdmin);
                                System.out.println("Registado: " + username + " com a password " + password + " e é admin? " + isAdmin);
                                writeSocket.writeUTF("Registado");writeSocket.flush();
                            } catch (UserAlreadyExistsException e) {
                                System.out.println("Já existe o utilizador");
                                writeSocket.writeUTF("Registo falhou");writeSocket.flush(); // User já existe
                            }
                        }
                        case "login" -> {
                            String user, pass;
                            user = readSocket.readUTF();
                            pass = readSocket.readUTF();
                            try {
                                UserListSingleton.getInstance().signIn(user, pass);
                                if (UserListSingleton.getInstance().isAdmin(user)) {
                                    writeSocket.writeUTF("Sessão iniciada como Admin!");
                                    writeSocket.flush();
                                } else {
                                    writeSocket.writeUTF("Sessão iniciada!");
                                    writeSocket.flush();
                                }
                            } catch (UserNotFoundException e) {
                                writeSocket.writeUTF("ERRO: Username não existe");
                            } catch (IncorrectPasswordException e) {
                                writeSocket.writeUTF("ERRO: Password incorreta");
                            }
                        }
                        case "NovaCompra" -> {
                            List<String> airports = new ArrayList<>();

                            String user = readSocket.readUTF();
                            String line = readSocket.readUTF();

                            while (!line.equals("FIN")) {
                                airports.add(line);
                                line = readSocket.readUTF();
                            }

                            LocalDate start = LocalDate.of(readSocket.readInt(), readSocket.readInt(), readSocket.readInt());
                            LocalDate end = LocalDate.of(readSocket.readInt(), readSocket.readInt(), readSocket.readInt());

                            AuxReservation aux = new AuxReservation(user, airports, start, end);

                            try {
                                int reservationID = company.makeReservation(aux.getUsername(), aux.getFlights(), aux.getDataInicio(), aux.getDataFinal());
                                writeSocket.writeUTF("Reserva efetuada com código de reserva " + reservationID);

                            } catch (Exception e) {
                                writeSocket.writeUTF("ERRO: Reserva impossível.");
                                e.printStackTrace();
                            } finally {
                                writeSocket.flush();
                            }
                        }
                        case "BloquearDia" -> {
                            LocalDate bloq = LocalDate.of(readSocket.readInt(), readSocket.readInt(), readSocket.readInt());
                            company.closeDay(bloq);
                            writeSocket.writeUTF("Dia bloqueado: " + bloq);
                            writeSocket.flush();
                        }
                        case "ConsultarReserva" -> {
                            String username = readSocket.readUTF();
                            int reservationID = readSocket.readInt();

                            if (company.ownsReservation(username, reservationID)) {
                                writeSocket.writeUTF("Reserva num. " + reservationID + "\n");
                                writeSocket.writeUTF(company.getReservation(reservationID).toString());
                            } else {
                                writeSocket.writeUTF("ERRO: A reserva não foi encontrada ou não lhe pertence.");
                            }
                            writeSocket.flush();
                        }
                        case "TodosVoos" -> {
                            writeSocket.writeUTF("Todos os voos disponíveis: ");
                            writeSocket.writeUTF(company.getAllFlights());
                            writeSocket.flush();
                        }
                        case "CancelaReserva" -> {
                            String username = readSocket.readUTF();
                            int reservationID = readSocket.readInt();
                            try {
                                System.out.println(username + " " + reservationID);
                                company.cancelReservation(reservationID, username);
                                writeSocket.writeUTF("A reserva foi anulada com sucesso!");
                            } catch (Exception e) {
                                e.printStackTrace();
                                writeSocket.writeUTF("ERRO: Reserva não cancelada.");
                            } finally {
                                writeSocket.flush();
                            }
                        }
                        case "NovoVoo" -> {
                            String code = readSocket.readUTF();
                            String departure = readSocket.readUTF();
                            String arrival = readSocket.readUTF();
                            int capacity = readSocket.readInt();
                            company.addNewRecurrentFlight(code, departure, arrival, capacity);
                            writeSocket.writeUTF("Voo adicionado!");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
