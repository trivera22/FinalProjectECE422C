package src;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import java.net.URL;
import javafx.scene.image.ImageView;


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
    public Button refreshButton;
    @FXML
    public Button searchButton;
    @FXML
    public TextField searchBar;
    @FXML
    public ImageView itemImage;
    @FXML
    private ListView<String> libraryItemList;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button returnButton;
    @FXML
    private TextField usernameField;

    private LibraryClient libraryClient;
    private AudioClip buttonPressSound;

    public void setLibraryClient(LibraryClient libraryClient) {
        initialize();
        this.libraryClient = libraryClient;
        setupButtonActions();
    }

    public void initialize() {
        URL soundResource = getClass().getResource("press_sound.mp3");
        buttonPressSound = new AudioClip(soundResource.toString());
    }

    private void setupButtonActions(){
        // checkout and return system
        checkoutButton.setOnAction(e -> {
            handleCheckout();
            buttonPressSound.play();
        });
        returnButton.setOnAction(e -> {
            handleReturn();
            buttonPressSound.play();
        });
        logoutButton.setOnAction(e -> {
            buttonPressSound.play();
            Platform.exit();
        });
        refreshButton.setOnAction(e -> {
            String username = usernameField.getText();
            libraryClient.refreshCheckedOutItems(username);
        });
        searchButton.setOnAction(e -> {
            updateLibraryItemListSearch(searchBar.getText());
        });


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
               } else{
                   Platform.runLater(() -> {
                       showErrorDialog("Item is already checked out");
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

    private void updateLibraryItemListSearch(String search){
        libraryItemList.getItems().clear();
        for(LibraryItem item : libraryClient.getAllLibraryItems()){
            if (item.getTitle().toLowerCase().contains(search.toLowerCase())) {
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

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateItemImage(Image image) {
        itemImage.setImage(image);
    }
}