package com.happymeals.Models;

public class Coupon {
    private String coupon_id;
    private String coupon_code;
    private String coupon_limit;
    private String coupon_percentage;
    private String owner_id;
    private String owner_name;
    private String timestamp;

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_limit() {
        return coupon_limit;
    }

    public void setCoupon_limit(String coupon_limit) {
        this.coupon_limit = coupon_limit;
    }

    public String getCoupon_percentage() {
        return coupon_percentage;
    }

    public void setCoupon_percentage(String coupon_percentage) {
        this.coupon_percentage = coupon_percentage;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
