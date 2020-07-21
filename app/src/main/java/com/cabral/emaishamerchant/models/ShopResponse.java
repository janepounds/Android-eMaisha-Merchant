package com.cabral.emaishamerchant.models;

public class ShopResponse {
    Integer shop_id;
    String message;

    public ShopResponse(Integer shop_id, String message) {
        this.shop_id = shop_id;
        this.message = message;
    }

    public Integer getShop_id() {
        return shop_id;
    }

    public String getMessage() {
        return message;
    }
}
