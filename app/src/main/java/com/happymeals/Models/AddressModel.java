package com.happymeals.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {
    private String addressId;
    private String userId;
    private String address;
    private String city;
    private String country;
    private String area;
    private String pincode;
    private String phone;
    private String timestamp;
    private String defaultAddress;

    public AddressModel() {
        // Default constructor required for calls to DataSnapshot.getValue(AddressModel.class)
    }

    public AddressModel(String addressId, String userId, String address, String city, String country, String area, String pincode,String phone, String timestamp) {
        this.addressId = addressId;
        this.userId = userId;
        this.address = address;
        this.city = city;
        this.country = country;
        this.area = area;
        this.pincode = pincode;
        this.phone = phone;
        this.timestamp = timestamp;
        // Assuming defaultAddress is optional, you can set a default value here if needed
        this.defaultAddress = "";
    }



    protected AddressModel(Parcel in) {
        addressId = in.readString();
        userId = in.readString();
        address = in.readString();
        city = in.readString();
        country = in.readString();
        area = in.readString();
        pincode = in.readString();
        phone = in.readString();
        timestamp = in.readString();
        defaultAddress = in.readString();
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressId);
        dest.writeString(userId);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(area);
        dest.writeString(pincode);
        dest.writeString(phone);
        dest.writeString(timestamp);
        dest.writeString(defaultAddress);
    }
}
