package com.example.scrapingtings.Model;

public class ScrapingModel {
    private String title;

    public ScrapingModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ScrapingModel{title='" + title + "'}";
    }
}
