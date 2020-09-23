package com.app.ecommerce.models;

public class Clients {

    String CLIENTE;
    String NOMBRE;
    String DIRECCION;
    String DIPONIBLE;
    String LIMITE;
    String SALDO;
    String MOROSO;

    public String getMOROSO() {
        return MOROSO;
    }

    public String getLIMITE() {
        return LIMITE;
    }

    public String getSALDO() {
        return SALDO;
    }

    public String getDIPONIBLE() {
        return DIPONIBLE;
    }

    public String getCLIENTE() {
        return CLIENTE;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public String getDIRECCION() {
        return DIRECCION;
    }
}