package src.network;

import src.library.LibraryItem;
import src.library.LibraryManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        new Server().setupNetworking();
    }

    List<Socket> sockets = new ArrayList<Socket>();

    private void setupNetworking() {
        try {
            ServerSocket server = new ServerSocket(4242);
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("incoming transmission");

                sockets.add(clientSocket);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        ClientHandler(Socket clientSocket){this.clientSocket = clientSocket;}

        public void run(){
            try{
                LibraryItem book = (LibraryItem) (new ObjectInputStream(clientSocket.getInputStream()).readObject());
                System.out.println("GOT THE BOOK " + book);
            } catch(IOException e){
                e.printStackTrace();
            } catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}