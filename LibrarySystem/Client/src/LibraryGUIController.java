package src;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LibraryGUIController {
    @FXML
    public ListView checkedOutList;
    @FXML
    public Button logoutButton;
    @FXML
    public CheckBox bookCheckBox;
    @FXML
    public CheckBox gameCheckBox;
    @FXML
    public CheckBox comicCheckBox;
    @FXML
    private ListView<String> libraryItemList;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button returnButton;
    @FXML
    private TextField usernameField;

    private LibraryClient libraryClient;

    public void setLibraryClient(LibraryClient libraryClient) {
        this.libraryClient = libraryClient;
        setupButtonActions();
    }

    private void setupButtonActions(){
        // checkout and return system
        checkoutButton.setOnAction(e -> handleCheckout());
        returnButton.setOnAction(e -> handleReturn());

        // tagging system
        bookCheckBox.setOnAction(e -> updateLibraryItemList());
        gameCheckBox.setOnAction(e -> updateLibraryItemList());
        comicCheckBox.setOnAction(e -> updateLibraryItemList());
    }

    private void handleCheckout(){
       String selectedItem = libraryItemList.getSelectionModel().getSelectedItem();
       if(selectedItem != null){
           new Thread(() -> {
               boolean success = libraryClient.checkoutItem(usernameField.getText(), selectedItem);
               if(success){
                   Platform.runLater(() -> {
                       checkedOutList.getItems().add(selectedItem);
                   });
               }
           }).start();
       }
    }

    private void handleReturn(){
        String selectedItem = (String) checkedOutList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            new Thread(() -> {
                boolean success = libraryClient.returnItem(usernameField.getText(), selectedItem);
                if(success){
                    Platform.runLater(() -> {
                        checkedOutList.getItems().remove(selectedItem);
                    });
                }
            }).start();
        }
    }

    private void updateLibraryItemList(){
        libraryItemList.getItems().clear();
        if (bookCheckBox.isSelected()) {
            libraryItemList.getItems().addAll(getLibraryItemsByType("Book"));
        }
        if (gameCheckBox.isSelected()) {
            libraryItemList.getItems().addAll(getLibraryItemsByType("Game"));
        }
        if (comicCheckBox.isSelected()) {
            libraryItemList.getItems().addAll(getLibraryItemsByType("Comic"));
        }

        if (!bookCheckBox.isSelected() && !gameCheckBox.isSelected() && !comicCheckBox.isSelected()) {
            for(LibraryItem item : libraryClient.getAllLibraryItems()){
                libraryItemList.getItems().add(item.getTitle());
            }
        }
    }

    private List<String> getLibraryItemsByType(String type){
        List<String> itemsByType = new ArrayList<>();
        for(LibraryItem item : libraryClient.getAllLibraryItems()){
            if(item.getItemType().equals(type)){
                itemsByType.add(item.getTitle());
            }
        }
        return itemsByType;
    }

    public void updateCheckedOutList(List<String> items) {
        checkedOutList.getItems().clear();
        checkedOutList.getItems().addAll(items);
    }

    public void addItemToLibrary(String item){
        libraryItemList.getItems().add(item);
    }

    public void setUsernameField(String username) {
        usernameField.setText(username);
    }

}