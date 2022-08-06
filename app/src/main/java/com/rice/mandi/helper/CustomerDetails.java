package com.rice.mandi.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Display;

public class CustomerDetails {
    public static final String CUSTOMER = "";
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    Context context;

    public CustomerDetails(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(CUSTOMER, Context.MODE_PRIVATE);
    }

    public void setCustomerId(String id) {

        this.editor = sharedPreferences.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public String getCustomerID() {
        return sharedPreferences.getString("id", "");
    }

    public void setCustomerName(String customer_name) {

        this.editor = sharedPreferences.edit();
        editor.putString("customer_name", customer_name);
        editor.apply();
    }

    public String getCustomerName() {
        return sharedPreferences.getString("customer_name", "");
    }

    public void setCustomerPhone(String customer_phone) {

        this.editor = sharedPreferences.edit();
        editor.putString("customer_phone", customer_phone);
        editor.apply();
    }

    public String getCustomerPhone() {
        return sharedPreferences.getString("customer_phone", "");
    }

    public void setCustomerAddress(String customer_address) {

        this.editor = sharedPreferences.edit();
        editor.putString("customer_address", customer_address);
        editor.apply();
    }

    public String getCustomerAddress() {
        return sharedPreferences.getString("customer_address", "");
    }

    public void setCustomerCity(String customer_city) {

        this.editor = sharedPreferences.edit();
        editor.putString("customer_city", customer_city);
        editor.apply();
    }



    public String getCustomerCity() {
        return sharedPreferences.getString("customer_city", "");
    }

    public void setCustomerFCM(String customer_city) {

        this.editor = sharedPreferences.edit();
        editor.putString("token", customer_city);
        editor.apply();
    }



    public String getCustomerFCM() {
        return sharedPreferences.getString("token", "");
    }

    public void removeCustomerDetails() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
