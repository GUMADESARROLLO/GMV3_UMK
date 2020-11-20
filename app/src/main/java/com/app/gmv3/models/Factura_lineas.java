package com.app.gmv3.models;
public class Factura_lineas {
    String ARTICULO;
    String DESCRIPCION;
    String CANTIDAD;
    String VENTA;
    String FECHA;
    String IMAGEN;
    String OBSERVACIONES;

    public String getOBSERVACIONES() {
        return OBSERVACIONES;
    }

    public String getIMAGEN() {
        return IMAGEN;
    }

    public String getARTICULO() {
        return ARTICULO;
    }

    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    public String getCANTIDAD() {
        return CANTIDAD;
    }

    public String getVENTA() {
        return VENTA;
    }

    public String getFECHA() {
        return FECHA;
    }
}