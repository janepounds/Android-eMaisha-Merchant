package com.cabral.emaishamerchant.models;

public class Manufacturers {
    private Integer manufacturers_id;
    private String manufacturer_name;

    public Manufacturers(Integer manufacturers_id, String manufacturer_name) {
        this.manufacturers_id = manufacturers_id;
        this.manufacturer_name = manufacturer_name;
    }

    public Integer getManufacturers_id() {
        return manufacturers_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }
}
