package com.cabral.emaishamerchantApp.models;

import java.util.List;

public class ProductResponse {
    private List<Product> Products;

    public ProductResponse(List<Product> products) {
        Products = products;
    }

    public List<Product> getProducts() {
        return Products;
    }
}
