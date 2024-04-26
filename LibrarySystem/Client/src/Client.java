package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        new Client().setupNetworking();
    }

    private void setupNetworking() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 4242);
            System.out.println("network established");

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader((new InputStreamReader(socket.getInputStream())));

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                writer.println(input);
                writer.flush();

                String received = reader.readLine();
                System.out.println("I RECEIVED BACK: " + received);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
