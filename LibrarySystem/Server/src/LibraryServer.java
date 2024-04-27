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
                try(ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    //send all books to the client
                    for (LibraryItem item : catalogue.getItems().values()) {
                        if (item instanceof Book) {
                            oos.writeObject((Book) item);
                            oos.reset();
                        }
                    }

                    //send "end" message to indicate all books have been sent
                    oos.writeObject("end");
                    oos.reset();

                    // handle checkout request from client
                    while (true) {
                        String message = reader.readLine();
                        if (message != null) {
                            String[] parts = message.split(":");
                            if (parts.length == 2) {
                                String username = parts[0];
                                String bookTitle = parts[1];
                                boolean result = catalogue.checkOutItem(bookTitle, username);
                                System.out.println("checkout request received for book: " + bookTitle + " by user: " + username);
                                System.out.println("sending response: " + result);
                                oos.writeObject(result);
                                oos.reset();
                                oos.flush();
                            }
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }
}