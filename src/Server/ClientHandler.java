package Server;

import Server.Exceptions.IncorrectPasswordException;
import Server.Exceptions.UserAlreadyExistsException;
import Server.Exceptions.UserNotFoundException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

            System.out.println("1");
        }

        public void run(){
            System.out.println("2");
            try{
                String input;
                while((input = readSocket.readUTF()) != null) {
                    System.out.println(input);
                    if (input.equals("Registo")) {
                        System.out.println(input);
                        String username, password;
                        boolean isAdmin;
                        username = readSocket.readUTF();
                        password = readSocket.readUTF();
                        isAdmin = readSocket.readBoolean();
                        try {
                            UserListSingleton.getInstance().signUp(username, password, isAdmin);
                            System.out.println("Registado: " + username + " com a password " + password + " e é admin? " + isAdmin);
                            writeSocket.writeUTF("Registado");
                            writeSocket.flush();
                        } catch (UserAlreadyExistsException e) {
                            System.out.println("Já existe o utilizador");
                            writeSocket.writeUTF("Registo falhou");
                            writeSocket.flush(); //User já existe
                        }
                    } else if (input.equals("login")) {
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
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
