package com.example.scrapingtings.model;

public class ScrapingBook {
    private String title;
    private String price;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ScrapingModel{" +
                "title='" + title + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
