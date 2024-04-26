package src.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public abstract class LibraryItem implements Serializable{
    private String title;
    private String author;
    private String summary;
    private String image;
    private LocalDateTime lastCheckedOut;
    private LocalDateTime dueDate;
    private List<String> pastBorrowers;
    private String currentBorrower;
    private List<String> tags;
    private List<String> reviews;

    public LibraryItem(String title, String author, String summary, String image){
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.image = image;
        this.pastBorrowers = new ArrayList<>();
        this.currentBorrower = null;
        this.lastCheckedOut = null;
        this.tags = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public synchronized void borrowItem(String memberId){
        if(this.currentBorrower == null && this.dueDate == null){
            this.currentBorrower = memberId;
            this.pastBorrowers.add(memberId);
            this.lastCheckedOut = LocalDateTime.now();
            this.dueDate = this.lastCheckedOut.plusDays(14); //due in 2 weeks
            System.out.println(title + " has been borrowed by " + memberId);
        }else{
            System.out.println(title + " is current not available.");
        }
    }

    public synchronized void returnItem(String memberId){
        if(this.currentBorrower != null && this.currentBorrower.equals(memberId)){
            this.currentBorrower = null;
            this.dueDate = null;
            System.out.println(title + " has been returned by " + memberId);
        }else{
            System.out.println("Error: This item was not borrowed by " + memberId);
        }
    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    public void addReview(String review){
        this.reviews.add(review);
    }

    class Book extends LibraryItem{
        private int pages;

        public Book(String title, String author, String summary, String image, int pages){
            super(title, author, summary, image);
            this.pages = pages;
        }
    }

    class Audiobook extends LibraryItem{
        private int duration; //duration in minutes

        public Audiobook(String title, String author, String summary, String image, int duration){
            super(title, author, summary, image);
            this.duration = duration;
        }
    }

    class DVD extends LibraryItem{
        private int duration; //duration in minutes

        public DVD(String title, String author, String summary, String image, int duration){
            super(title, author, summary, image);
            this.duration = duration;
        }
    }
    class Game extends LibraryItem{
        private String platform; //like pc,ps4

        public Game(String title, String author, String summary, String image, String platform){
            super(title, author, summary, image);
            this.platform = platform;
        }
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
}
