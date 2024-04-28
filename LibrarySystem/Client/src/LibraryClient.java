package src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.util.ArrayList;
import java.util.List;

public class LibraryClient extends Application {
    @FXML
    private Button logoutButton;

    private Stage window;
    private LibraryGUIController libraryGUIController;
    private PrintWriter writer;
    private String username;
    private ObjectInputStream ois;
    private List<LibraryItem> libraryItems = new ArrayList<>();
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
                    setupNetworking();
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

        Scene loginScene = new Scene(loginLayout, 300, 150);
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
                libraryGUIController.setUsernameField(username); //sets username in library interface
            }
            Scene libraryScene = new Scene(libraryLayout, 800, 430);
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
        writer = new PrintWriter(socket.getOutputStream(), true); //auto flush enabled
        ois = new ObjectInputStream(socket.getInputStream());
        libraryItems.clear();
        System.out.println("network established");

        try {
            Object obj;
            while ((obj = ois.readObject()) != null) {
                if (obj instanceof String && obj.equals("end")) {
                    break;
                }
                if(obj instanceof LibraryItem){
                    LibraryItem item = (LibraryItem) obj;
                    System.out.println("got the item: " + item.getTitle());
                    libraryItems.add(item);
                    Platform.runLater(() -> libraryGUIController.addItemToLibrary(item.getTitle()));
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public boolean checkoutItem(String username, String itemTitle){
        try{
            String message  = "checkout:" + username + ":" + itemTitle; //use checkout prefix
            System.out.println("sending checkout request: " + message);
            writer.println(message);

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

    public boolean returnItem(String username, String itemTitle){
        try{
            String message = "return:" + username + ":" + itemTitle; //use return prefix
            System.out.println("sending return request: " + message);
            writer.println(message);

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

    public void refreshCheckedOutItems(String username){
       Task<List<String>> task = new Task<List<String>>(){
           @Override
           protected List<String> call() throws Exception {
               System.out.println("sending refresh request");
               writer.println("refresh:" + username);
               writer.flush();

               Object response = ois.readObject();
               System.out.println("received response" + response);
               if(response instanceof List){
                   return(List<String>) response;
               }
               return new ArrayList<>();
           }
       };

       task.setOnSucceeded(e -> {
           Platform.runLater(() -> libraryGUIController.updateCheckedOutList(task.getValue()));
       });

       task.setOnFailed(e -> {
           Throwable exception = task.getException();
           exception.printStackTrace();
       });

       new Thread(task).start();
    }

    public void login(String username){
        try{
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("login:" + username);
            writer.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            List<String> checkedOutBooks = (List<String>) ois.readObject();
            Platform.runLater(() -> libraryGUIController.updateCheckedOutList(checkedOutBooks));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LibraryItem> getAllLibraryItems() {
        return libraryItems;
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
