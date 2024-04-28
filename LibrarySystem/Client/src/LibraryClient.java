package src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class LibraryClient extends Application {
    @FXML
    private Button logoutButton;

    private Stage window;
    private LibraryGUIController libraryGUIController;
    private PrintWriter writer;
    private String username;
    private ObjectInputStream ois;
    private Socket socket;
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Library Client");

        //login layout
        GridPane loginLayout = new GridPane();
        loginLayout.setVgap(10);
        loginLayout.setHgap(10);
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            this.username = usernameInput.getText(); //gets username
            showLibraryInterface();
            Platform.runLater(() -> {
                try {
                    setupNetworking(); //pass the username to the setupNetworking method
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
        });

        loginLayout.add(new Label("Username:"), 0, 0);
        loginLayout.add(usernameInput, 1, 0);
        loginLayout.add(new Label("Password (Optional):"), 0, 1);
        loginLayout.add(passwordInput, 1, 1);
        loginLayout.add(loginButton, 1, 2);

        Scene loginScene = new Scene(loginLayout, 250, 150);
        window.setScene(loginScene);
        window.show();
    }

    private void showLibraryInterface(){
        //Main library layout
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LibraryLayout.fxml"));
            Parent libraryLayout = loader.load();
            libraryGUIController = loader.getController();
            if(libraryGUIController != null) {
                libraryGUIController.setLibraryClient(this);
            }
            Scene libraryScene = new Scene(libraryLayout, 640, 400);
            window.setScene(libraryScene);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        launch(args);
    }
    private void setupNetworking() throws IOException, ClassNotFoundException {
        socket = new Socket(InetAddress.getLocalHost(), 4242);
        System.out.println("network established");

        writer = new PrintWriter(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

        try {
            Object obj;
            while ((obj = ois.readObject()) != null) {
                if (obj instanceof String && obj.equals("end")) {
                    break;
                }
                Book book = (Book) obj;
                System.out.println("got the book: " + book);
                libraryGUIController.addBook(book.getTitle());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public boolean checkoutBook(String username, String bookTitle){
        try{
            String message  = username + ":" + bookTitle;
            System.out.println("sending checkout request: " + message);
            writer.println(message);
            writer.flush();

            Boolean response = (Boolean) ois.readObject();
            System.out.println("received response: " + response);
            return response;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean returnBook(String username, String bookTitle){
        try{
            String message = "return:" + username + ":" + bookTitle;
            System.out.println("sending return request: " + message);
            writer.println(message);
            writer.flush();

            Boolean response = (Boolean) ois.readObject();
            System.out.println("received response: " + response);
            return response;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Error: " + message);
        alert.showAndWait();
    }

    public String getUsername(){
        return this.username;
    }

    public Socket getSocket() {
        return socket;
    }
}
