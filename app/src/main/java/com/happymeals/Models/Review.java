package com.happymeals.Models;

public class Review {
    private String reviewId;
    private String dishId;
    private String userid;
    private String reviewText;
    private String timestamp;

    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String reviewId, String dishId, String userid, String reviewText, String timestamp) {
        this.reviewId = reviewId;
        this.dishId = dishId;
        this.userid = userid;
        this.reviewText = reviewText;
        this.timestamp = timestamp;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
