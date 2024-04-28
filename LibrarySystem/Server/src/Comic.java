package src;

public class Comic extends Book {
    private String artist;

    public Comic(String title, String summary, String imagePath, int pages, String author, String artist) {
        super(title, author, summary, pages, imagePath);
        this.artist = artist;
        super.setAuthor(author);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}