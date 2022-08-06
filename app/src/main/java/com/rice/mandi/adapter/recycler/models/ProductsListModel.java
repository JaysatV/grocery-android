package com.rice.mandi.adapter.recycler.models;

import java.util.List;

public class ProductsListModel {
    String title;
    List<ProductModel> productsList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProductModel> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<ProductModel> productsList) {
        this.productsList = productsList;
    }
}
