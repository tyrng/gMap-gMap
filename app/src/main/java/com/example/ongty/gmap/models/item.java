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
}
