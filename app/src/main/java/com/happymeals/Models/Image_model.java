package com.happymeals.Models;

public class Image_model {
    String dish_id;
    String image_id, imageurl, restaurant_id;

    public Image_model() {
        // Default constructor required for calls to DataSnapshot.getValue(Image_model.class)
    }

    public Image_model(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDish_id() {
        return dish_id;
    }

    public void setDish_id(String dish_id) {
        this.dish_id = dish_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }








}
