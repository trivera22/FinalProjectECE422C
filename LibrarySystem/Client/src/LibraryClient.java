package src;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class LibraryClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new LibraryClient().setupNetworking();
    }
    private void setupNetworking() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 4242);
        System.out.println("network established");

        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Book book = (Book)objectInputStream.readObject();
            System.out.println("got the book " + book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
