package com.happymeals.Models;

public class Sub_cat_model {
    String dish_category_id,imageurl,dish_category;

    private String dish_name;
    public String getDish_category_id() {
        return dish_category_id;
    }

    public void setDish_category_id(String dish_category_id) {
        this.dish_category_id = dish_category_id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDish_category() {
        return dish_category;
    }

    public void setDish_category(String dish_category) {
        this.dish_category = dish_category;
    }

    public String getDish_name() {
        return dish_name;
    }

    public void setDish_name(String dish_name) {
        this.dish_name = dish_name;
    }
}
