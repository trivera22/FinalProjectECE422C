package src;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;

public class LibraryGUIController {
    @FXML
    public ListView checkedOutList;
    @FXML
    private ListView<String> bookList;
    @FXML
    private Button checkoutButton;

    private LibraryClient libraryClient;

    public void setLibraryClient(LibraryClient libraryClient) {
        this.libraryClient = libraryClient;
        checkoutButton.setOnAction(e -> checkoutBook());
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
                        bookList.getItems().remove(selectedBook);
                        checkedOutList.getItems().add(selectedBook);
                    });
                }
            }).start();
        }
    }
}