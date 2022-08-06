package com.rice.mandi.Retrofit.ModuleClasses;

import java.util.List;

public class BrandDataClass {
    String brand_name, updated_at, created_at, id;
    List<ProductsDataClass> brand_products;

    public List<ProductsDataClass> getBrand_products() {
        return brand_products;
    }

    public void setBrand_products(List<ProductsDataClass> brand_products) {
        this.brand_products = brand_products;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
