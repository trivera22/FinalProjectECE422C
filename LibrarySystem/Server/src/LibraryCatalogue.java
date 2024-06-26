package src;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LibraryCatalogue {
    private HashMap<String, LibraryItem> items = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    public LibraryCatalogue(){
        loadBooksFromJson("libraryitems.json");
    }

    public synchronized void addItem(LibraryItem item){
        items.put(item.getTitle(), item);
    }

    public synchronized boolean checkOutItem(String title, String member){
        lock.lock();
        try{
            LibraryItem item = items.get(title);
            if(item != null && item.getCurrentHolder() == null){
                item.setCurrentHolder(member);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public synchronized HashMap<String, LibraryItem> getItems() {
        return items;
    }

    public synchronized boolean returnItem(String title, String member){
        lock.lock();
        try{
            LibraryItem item = items.get(title);
            if(item != null && item.getCurrentHolder().equals(member)){
                item.setCurrentHolder(null);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void loadBooksFromJson(String filePath){
//        try{
//            Gson gson = new Gson();
//            Type bookListType = new TypeToken<ArrayList<Book>>(){}.getType();
//            List<Book> bookList = gson.fromJson(new FileReader(filePath), bookListType);
//            for(Book book : bookList){
//                addItem(book);
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//        }
        String resourcePath = "src/main/resources/libraryitems.json";  // Correct path for resources

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type bookListType = new TypeToken<ArrayList<Book>>(){}.getType();
            List<Book> bookList = gson.fromJson(reader, bookListType);
            for (Book book : bookList) {
                addItem(book);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized List<String> getCheckedOutItems(String member){
        List<String> checkedOutBooks = new ArrayList<>();
        for(LibraryItem item : items.values()){
            if(item.getCurrentHolder() != null && item.getCurrentHolder().equals(member)){
                checkedOutBooks.add(item.getTitle());
            }
        }
        return checkedOutBooks;
    }
}
