package src;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class LibraryGUIController {
    @FXML
    public ListView checkedOutList;
    @FXML
    public Button logoutButton;
    @FXML
    private ListView<String> bookList;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button returnButton;
    @FXML
    private TextField usernameField;


    private LibraryClient libraryClient;

    public void setLibraryClient(LibraryClient libraryClient) {
        this.libraryClient = libraryClient;
        checkoutButton.setOnAction(e -> checkoutBook());
        returnButton.setOnAction(e->returnBook());

        // Add action event to logout button
        logoutButton.setOnAction(e -> {
            try {
                if (libraryClient.getSocket() != null) {
                    libraryClient.getSocket().close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
        });
    }
    public void addBook(String book) {
        bookList.getItems().add(book);
    }

    private void checkoutBook(){
        System.out.println("checkout button pressed");
        String selectedBook = bookList.getSelectionModel().getSelectedItem();
        if(selectedBook != null){
            new Thread(() -> {
                boolean canCheckout = libraryClient.checkoutBook(libraryClient.getUsername(), selectedBook);
                if (canCheckout) {
                    Platform.runLater(() -> {
                        checkedOutList.getItems().add(selectedBook);
                    });
                }
            }).start();
        }
    }

    private void returnBook(){
        String selectedBook = (String) checkedOutList.getSelectionModel().getSelectedItem();
        if(selectedBook != null){
            new Thread(() -> {
                boolean canReturn = libraryClient.returnBook(libraryClient.getUsername(), selectedBook);
                if (canReturn) {
                    Platform.runLater(() -> {
                        checkedOutList.getItems().remove(selectedBook);
                    });
                }
            }).start();
        }
    }

    public void updateCheckedOutList(List<String> checkedOutBooks) {
        checkedOutList.getItems().clear();
        checkedOutList.getItems().addAll(checkedOutBooks);
    }

    public void setUsernameField(String username) {
        usernameField.setText(username);
    }

}