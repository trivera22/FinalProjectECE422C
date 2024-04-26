package src;

import java.io.ObjectOutputStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LibraryServer {
    private LibraryCatalogue catalogue = new LibraryCatalogue();

    public static void main(String[] args) {
        new LibraryServer().setupNetworking();
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
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            Book book = new Book("A Tale of Two Cities", "Charles Dickens", "Some book", 93, null);
            try {
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.writeObject(book);
                oos.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}