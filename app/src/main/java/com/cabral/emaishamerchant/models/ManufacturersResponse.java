package com.cabral.emaishamerchant.models;

import java.util.List;

public class ManufacturersResponse {
    List<Manufacturer> manufacturers;

    public ManufacturersResponse(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }
}
