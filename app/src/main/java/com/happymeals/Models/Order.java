package com.happymeals.Models;

import java.util.List;

public class Order {
    private String userId;
    private List<String> dishIds;
    private List<Long> quantities;
    private List<Integer> prices;

    private String orderId;

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String userId, List<String> dishIds, List<Long> quantities, List<Integer> prices) {
        this.userId = userId;
        this.dishIds = dishIds;
        this.quantities = quantities;
        this.prices = prices;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<String> dishIds) {
        this.dishIds = dishIds;
    }

    public List<Long> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Long> quantities) {
        this.quantities = quantities;
    }

    public List<Integer> getPrices() {
        return prices;
    }

    public void setPrices(List<Integer> prices) {
        this.prices = prices;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
