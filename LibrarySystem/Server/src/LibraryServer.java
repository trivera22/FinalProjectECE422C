package src;

import java.io.ObjectOutputStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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

                    //send all library items to client
                    for (LibraryItem item : catalogue.getItems().values()) {
                        oos.writeObject(item); //send item as serialized object
                        oos.reset(); //reset to avoid caching issues
                    }

                    //send "end" message to indicate all items have been sent
                    oos.writeObject("end");
                    oos.reset();

                    //continually check for messages from client
                    while (true) {
                        if(clientSocket.isClosed()){
                            break;
                        }
                        try {
                            String message = reader.readLine();

                            if (message != null && message.startsWith("checkout:")) {
                                String[] parts = message.substring(9).split(":");
                                String username = parts[0];
                                String itemTitle = parts[1];
                                boolean result = catalogue.checkOutItem(itemTitle, username);
                                System.out.println("checkout request received for item: " + itemTitle + " by user: " + username);
                                System.out.println("sending response: " + result);
                                oos.writeObject(result);
                                oos.reset();
                                oos.flush();

                                // if checkout is successful, send the image file
//                                if (result) {
//                                    LibraryItem item = catalogue.getItems().get(itemTitle);
//                                    File imageFile = new File(item.getImagePath());
//                                    byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
//                                    oos.writeObject(imageBytes);
//                                    oos.reset();
//                                    oos.flush();
//                                }

                                if (result) {
                                    LibraryItem item = catalogue.getItems().get(itemTitle);
                                    InputStream is = getClass().getClassLoader().getResourceAsStream(item.getImagePath());
                                    if (is == null) {
                                        System.err.println("Could not find file: " + item.getImagePath());
                                        return;
                                    }

                                    try {
                                        byte[] imageBytes = readBytesFromInputStream(is);
                                        oos.writeObject(imageBytes);  // Assuming 'oos' is your ObjectOutputStream
                                        oos.reset();
                                        oos.flush();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            } else if (message.startsWith("return:") && message != null) {
                                String[] parts = message.substring(7).split(":");
                                String username = parts[0];
                                String itemTitle = parts[1];
                                boolean result = catalogue.returnItem(itemTitle, username);
                                System.out.println("return request received for book: " + itemTitle + " by user: " + username);
                                System.out.println("sending response: " + result);
                                oos.writeObject(result);
                                oos.reset();
                                oos.flush();
                            } else if (message != null && message.startsWith("refresh:")) {
                                System.out.println("refresh request received");
                                String username = message.split(":")[1];
                                List<String> checkedOutItems = catalogue.getCheckedOutItems(username);
                                System.out.println("sending checked out items: " + checkedOutItems);
                                oos.writeObject(checkedOutItems);
                                oos.reset();
                                oos.flush();
                            }
                        }catch (SocketException se){
                            break;
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
        }

        public byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                throw new IllegalArgumentException("Input stream must not be null");
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];  // Size can be adjusted according to needs

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        }
    }
}