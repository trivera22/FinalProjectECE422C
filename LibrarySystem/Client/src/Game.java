package src;

public class Game extends LibraryItem {
    private String platform;

    public Game(String title, String summary, String imagePath, String platform) {
        super("Game", title, summary, imagePath);
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}