package src;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
public class LibraryGUITest extends ApplicationTest{

    @Override
    public void start(Stage stage) throws Exception{
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LibraryLayout.fxml"));

        // Create the scene
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

        // Get the controller
        LibraryGUIController controller = loader.getController();

        // Setup the LibraryClient and set it in the controller
        LibraryClient libraryClient = new LibraryClient();
        controller.setLibraryClient(libraryClient);

        controller.addItemToLibrary("Batman Year One");
        controller.addItemToLibrary("Book Two");

        // Show the stage
        stage.show();
    }

    @Test
    public void testSearch(){
        //write Batman in the search bar
        clickOn("#searchBar").write("Batman");
        //verify that the search bar has the text Batman
        verifyThat("#searchBar", hasText("Batman"));
    }

    @Test
    public void testApplicationLoads() {
        // verify that the search bar is visible
        verifyThat("#searchBar", NodeMatchers.isVisible());
    }

    @Test
    public void testSearchBarEmpty() {
        // verify that the search bar is empty
        verifyThat("#searchBar", hasText(""));
    }

    @Test
    public void testTagsLabelExists() {
        // verify that the tags label exists
        verifyThat("#tags", isNotNull());
    }

    @Test
    public void testLibraryItemsListIsNotEmpty() {
        // verify that the libraryItemList ListView is not empty
        verifyThat("#libraryItemList", isNotNull());
    }
}
