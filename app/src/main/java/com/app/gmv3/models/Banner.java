package com.app.gmv3.models;

public class Banner {
    private  String titulo;
    private  String descripcion;
    private  String fechaInicio;

    private  String Imagen;

    public String getImagen() {
        return Imagen;
    }
    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    private  String fechaFinal;

}
