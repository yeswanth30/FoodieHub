package com.happymeals.Models;

import java.util.List;

public class RestaurantModel {
    String restaurant_id, restaurant_name, address, city, country, imageurl, owner_id, owner_name, phone, pincode, timestamp, type,delivery_time;
    private List<String> foodTypes;

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public RestaurantModel(){

}
    public RestaurantModel(String restaurantId, String restaurantName, String address, String phone, String city, String country,
                           String ownerId, String ownerName, String pincode, String imageurl, String type, List<String> foodTypes) {
        this.restaurant_id = restaurantId;
        this.restaurant_name = restaurantName;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.owner_id = ownerId;
        this.owner_name = ownerName;
        this.pincode = pincode;
        this.imageurl = imageurl;
        this.type = type;
        this.foodTypes = foodTypes;
    }

    public RestaurantModel(String restroname, String restrotype, String city, String country, List<String> foodTypes, String imageurl) {
        this.restaurant_name = restroname;
        this.type = restrotype;
        this.city = city;
        this.country = country;
        this.foodTypes = foodTypes;
        this.imageurl = imageurl;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFoodTypes() {
        return foodTypes;
    }

    public void setFoodTypes(List<String> foodTypes) {
        this.foodTypes = foodTypes;
    }

}

