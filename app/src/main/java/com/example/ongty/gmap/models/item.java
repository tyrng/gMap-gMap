package com.example.ongty.gmap.models;

public class item {
    String name;
    Double price;
    byte[] receipt;

    public item() {
    }

    public item(String name, Double price, byte[] receipt) {
        this.name = name;
        this.price = price;
        this.receipt = receipt;
    }
}
