package com.cabral.emaishamerchantApp.models;

public class Product {
    private Integer products_id;
    private String products_slug;

    public Product(Integer products_id, String products_slug) {
        this.products_id = products_id;
        this.products_slug = products_slug;
    }

    public Integer getProducts_id() {
        return products_id;
    }

    public String getProducts_slug() {
        return products_slug;
    }
}
