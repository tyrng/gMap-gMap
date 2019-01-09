package com.example.ongty.gmap.models;

public class item {
    String name;
    String category;
    Double price;
    place itemPlace;
    String receipt;

    public item() {
    }

    public item(String name, String category, Double price, place itemPlace, String receipt) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.itemPlace = itemPlace;
        this.receipt = receipt;
    }

    public item(String name, String category, Double price, place itemPlace) { // no picture
        this.name = name;
        this.category = category;
        this.price = price;
        this.itemPlace = itemPlace;
        this.receipt = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public place getItemPlace() {
        return itemPlace;
    }

    public void setItemPlace(place itemPlace) {
        this.itemPlace = itemPlace;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
