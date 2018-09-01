package com.app.chendurfincorp.data;

public class HomeMenu {

    private int image;
    private String title;

    public HomeMenu(String title, int image) {
        this.title = title;
        this.image = image;
    }
    public String getTitle() {
        return title;
    }
    public int getImage() {
        return image;
    }
}
