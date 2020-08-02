package com.cabral.emaishamerchantsapp.models;

import java.util.List;

public class CategoriesResponse {
    private List<Category> Categories;

    public CategoriesResponse(List<Category> categories) {
        Categories = categories;
    }

    public List<Category> getCategories() {
        return Categories;
    }


}
