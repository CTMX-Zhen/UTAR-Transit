package my.edu.utar.utartransit;

public class Item {

    String date;
    String title;
    String imageURL;


    public Item(String date, String title, String imageURL) {
        this.date = date;
        this.title = title;
        this.imageURL = imageURL;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageURL() {
        return imageURL;
    }


}