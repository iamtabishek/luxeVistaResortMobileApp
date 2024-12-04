package com.example.luxevistaapp;


public class Service {
    private String name;
    private String description;
    private String pictureUrl;
    private double price;

    // Default constructor required for Firebase
    public Service() {
    }

    public Service(String name, String description,String pictureUrl, double price) {
        this.name = name;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.price = price;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
