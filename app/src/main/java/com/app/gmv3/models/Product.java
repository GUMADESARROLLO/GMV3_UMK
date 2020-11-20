package com.app.gmv3.models;

public class Product {

    private String product_id;
    private String product_name;
    private String category_id;
    private String category_name;
    private double product_price;
    private String product_status;
    private String product_image;
    private String product_description;
    private String currency_code;
    private String product_bonificado;
    private String product_lotes;
    private String product_und;
    private String CALIFICATIVO;
    private double tax;
    private double product_quantity;

    public String getCALIFICATIVO() {
        return CALIFICATIVO;
    }

    public String getProduct_und() {
        return product_und;
    }

    public String getProduct_lotes() {
        return product_lotes;
    }

    public String getProduct_bonificado() {
        return product_bonificado;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public double getProduct_price() {
        return product_price;
    }

    public String getProduct_status() {
        return product_status;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public double getTax() {
        return tax;
    }

    public double getProduct_quantity() {
        return product_quantity;
    }
}