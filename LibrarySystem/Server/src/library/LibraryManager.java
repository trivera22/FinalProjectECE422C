package src.library;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class LibraryManager {

    public List<LibraryItem> loadLibraryItems(String filename) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Type listType = new TypeToken<List<LibraryItem>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveLibraryItems(List<LibraryItem> items, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(items);

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
