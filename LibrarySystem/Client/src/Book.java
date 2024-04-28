package src;


public class Book extends LibraryItem{
    private int pages;
    private String author;

    public Book(String title, String author, String summary, int pages, String imagePath){
        super("Book", title, summary, imagePath);
        this.pages = pages;
        this.author = author;
    }

    public String toString(){
        return super.toString() + " : " + this.author + " : " + this.pages + " pages";
    }
    public String getAuthor(){
        return this.author;
    }
    public int getPages(){
        return this.pages;
    }
    public void setAuthor(String author){
        this.author = author;
    }
}
