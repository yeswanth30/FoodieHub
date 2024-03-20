package com.happymeals.Models;

import java.util.HashMap;
import java.util.Map;

public class Dish {
    private String dish_id;

     long quantity;
    private String cost;


    private String dish_name;
    String imageurl;

    private Map<String, String> rating; //


    private String review;

    private Map<String, String> reviews; //



    public Dish() {
        // Required empty public constructor
//        this.ratings = new HashMap<>();

    }

    public Dish(String dish_id, String dish_name, String imageurl, long quantity) {
        this.dish_id = dish_id;
        this.dish_name = dish_name;
        this.imageurl = imageurl;
        this.quantity = quantity;
    }


    public String getDish_id() {
        return dish_id;
    }

    public void setDish_id(String dish_id) {
        this.dish_id = dish_id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public Map<String, String> getRating() {
        return rating;
    }

    public void setRating(Map<String, String> rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    public Map<String, String> getReviews() {
        return reviews;
    }

    public void setReviews(Map<String, String> reviews) {
        this.reviews = reviews;
    }
}
