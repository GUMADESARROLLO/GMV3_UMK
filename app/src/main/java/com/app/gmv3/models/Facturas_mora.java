package com.app.gmv3.models;

public class Facturas_mora {
    String Codigo;
    String Saldo;
    String Fecha;

    public Facturas_mora(){

    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public void setSaldo(String saldo) {
        Saldo = saldo;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getCodigo() {
        return Codigo;
    }

    public String getSaldo() {
        return Saldo;
    }

    public String getFecha() {
        return Fecha;
    }
}
