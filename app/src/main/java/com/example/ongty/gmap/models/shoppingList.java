package com.example.ongty.gmap.models;

public class shoppingList {
    private String userId;
    private String name;
    private String category;
    private Double price;
    private place itemPlace;
    public shoppingList() {

    }
    public shoppingList(String userId, String name, String category, Double price, place itemPlace) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.itemPlace = itemPlace;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
