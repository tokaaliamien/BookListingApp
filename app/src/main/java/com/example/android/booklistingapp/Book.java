package com.example.android.booklistingapp;

/**
 * Created by Demo on 2017-08-09.
 */

public class Book {

    private String title;
    private String auther;

    public Book(String title, String auther) {
        this.title = title;
        this.auther = auther;
    }

    public String getTitle() {
        return title;
    }

    public String getAuther() {
        return auther;
    }
}
