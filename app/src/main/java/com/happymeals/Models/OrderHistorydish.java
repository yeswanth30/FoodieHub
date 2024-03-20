package com.happymeals.Models;

import java.util.List;

public class OrderHistorydish {
    private String orderId;
    private List<Dish> dishes;

    private String uniqueid;

    private String timestamp;



    public OrderHistorydish(String orderId, List<Dish> dishes,String uniqueid,String timestamp) {
        this.orderId = orderId;
        this.dishes = dishes;
        this.uniqueid=uniqueid;
        this.timestamp=timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
