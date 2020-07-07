package com.example.suncoffee.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Order {
    public String uid;
    public String author;
    public String title;
    public String price;
    public String place;
    public Order(){}

    public Order(String uid, String author, String place, String title, String price) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.price = price;
        this.place = place;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("price", price);
        result.put("place", place);
        return result;
    }
}
