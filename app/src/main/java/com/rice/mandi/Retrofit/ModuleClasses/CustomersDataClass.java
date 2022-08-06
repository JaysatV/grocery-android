package com.rice.mandi.Retrofit.ModuleClasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomersDataClass {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("customer_name")
    @Expose
    private String customer_name;

    @SerializedName("customer_phone")
    @Expose
    private String customer_phone;

    @SerializedName("customer_address")
    @Expose
    private String customer_address;

    @SerializedName("customer_city")
    @Expose
    private String customer_city;

    @SerializedName("wasRecentlyCreated")
    @Expose
    private boolean wasRecentlyCreated;

    @SerializedName("fcm_token")
    @Expose
    private String fcm_token;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_city() {
        return customer_city;
    }

    public void setCustomer_city(String customer_city) {
        this.customer_city = customer_city;
    }

    public boolean isWasRecentlyCreated() {
        return wasRecentlyCreated;
    }

    public void setWasRecentlyCreated(boolean wasRecentlyCreated) {
        this.wasRecentlyCreated = wasRecentlyCreated;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
