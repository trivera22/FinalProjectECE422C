package src;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
public class LibraryCatalogue {
    private HashMap<String, LibraryItem> items;
    private ReentrantLock lock = new ReentrantLock();
    public LibraryCatalogue(){
        this.items = new HashMap<>();
    }

    public synchronized void addItem(LibraryItem item){
        items.put(item.getTitle(), item);
    }

    public synchronized LibraryItem checkOutItem(String title, String member){
        lock.lock();
        try{
            LibraryItem item = items.get(title);
            if(item != null && item.getCurrentHolder() == null){
                item.setCurrentHolder(member);
                return item;
            }
            return null;
        } finally {
            lock.unlock();
        }
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
}
