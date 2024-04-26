package src;

import java.io.Serializable;
import java.util.Date;

public abstract class LibraryItem implements Serializable{
    private static final long serialVersionUID = 1L;

    private String itemType;
    private String title;
    private String summary;
    private String currentHolder;
    private String[] pastHolders;
    private Date lastCheckedOut;
    private String imagePath;
    public LibraryItem(String itemType, String title, String summary, String imagePath) {
        this.itemType = itemType;
        this.title = title;
        this.summary = summary;
        this.imagePath = imagePath;
        this.currentHolder = null;
        this.pastHolders = new String[0];
        this.lastCheckedOut = null;
    }

    @Override
    public String toString() {
        return this.title;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }
    public String getSummary(){
        return this.summary;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public String getCurrentHolder(){
        return this.currentHolder;
    }
    public void setCurrentHolder(String currentHolder){
        this.currentHolder = currentHolder;
    }
    public String[] getPastHolders(){
        return this.pastHolders;
    }
    public void setPastHolders(String[] pastHolders){
        this.pastHolders = pastHolders;
    }
    public Date getLastCheckedOut(){
        return this.lastCheckedOut;
    }
    public void setLastCheckedOut(Date lastCheckedOut){
        this.lastCheckedOut = lastCheckedOut;
    }
    public String getImagePath(){
        return this.imagePath;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
    public String getItemType(){
        return this.itemType;
    }
    public void setItemType(String itemType){
        this.itemType = itemType;
    }
}

