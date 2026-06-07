package com.example.miprimerapp;

public class producto {

    private String idProducto;
    private String codigo;
    private String descripcion;
    private String marca;
    private String presentacion;
    private String precio;
    private String foto;
    private String costo;

    private String stock;

    private String ganancia;

    private String emocion;

    public producto(String idProducto, String codigo, String descripcion, String marca, String presentacion, String precio, String foto, String costo, String stock, String ganancia, String emocion) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.presentacion = presentacion;
        this.precio = precio;
        this.foto = foto;
        this.costo = costo;
        this.stock = stock;
        this. ganancia= ganancia;
        this. emocion= emocion;


    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCosto() {
        return costo;
    }
    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getStock() {
        return stock;
    }
    public void setStock(String stock) {
        this.stock = stock;
    }



    public String getGanancia() {
        return ganancia;
    }
    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }



    public String getEmocion() {
        return emocion;
    }
    public void setEmocion(String emocion) {
        this.emocion = emocion;
    }
}