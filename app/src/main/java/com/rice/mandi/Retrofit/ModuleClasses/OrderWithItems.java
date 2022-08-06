package com.rice.mandi.Retrofit.ModuleClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rice.mandi.room.CartTableClass;

import java.util.List;

public class OrderWithItems {
    @SerializedName("order_data")
    @Expose
    public OrdersDataClass ordersDataClass;

    @SerializedName("order_items_data")
    @Expose
    public List<CartTableClass> orderItems;



    public OrdersDataClass getOrdersDataClass() {
        return ordersDataClass;
    }

    public void setOrdersDataClass(OrdersDataClass ordersDataClass) {
        this.ordersDataClass = ordersDataClass;
    }

    public List<CartTableClass> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CartTableClass> orderItems) {
        this.orderItems = orderItems;
    }
}
