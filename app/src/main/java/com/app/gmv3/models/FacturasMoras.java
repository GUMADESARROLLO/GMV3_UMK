package com.app.gmv3.models;

public class FacturasMoras {
    String factura_cliente;
    String factura_nombre;
    String factura_id;
    String factura_cant;
    String factura_date;
    String factura_monto;
    String factura_devencidos;

    public String getFactura_devencidos() {
        return factura_devencidos;
    }

    public void setFactura_devencidos(String factura_devencidos) {
        this.factura_devencidos = factura_devencidos;
    }

    public FacturasMoras() {

    }

    public String getFactura_cliente() {
        return factura_cliente;
    }

    public void setFactura_cliente(String factura_cliente) {
        this.factura_cliente = factura_cliente;
    }

    public String getFactura_nombre() {
        return factura_nombre;
    }

    public void setFactura_nombre(String factura_nombre) {
        this.factura_nombre = factura_nombre;
    }

    public String getFactura_id() {
        return factura_id;
    }

    public void setFactura_id(String factura_id) {
        this.factura_id = factura_id;
    }

    public String getFactura_cant() {
        return factura_cant;
    }

    public void setFactura_cant(String factura_cant) {
        this.factura_cant = factura_cant;
    }

    public String getFactura_date() {
        return factura_date;
    }

    public void setFactura_date(String factura_date) {
        this.factura_date = factura_date;
    }

    public String getFactura_monto() {
        return factura_monto;
    }

    public void setFactura_monto(String factura_monto) {
        this.factura_monto = factura_monto;
    }
}
