package com.example.miprimeraapp;

public class producto {

    private String idProducto;
    private String codigo;
    private String descripcion;
    private String marca;
    private String presentacion;
    private double costo;
    private double precio;
    private double ganancia;
    private int stock;
    private String foto;

    public producto(String idProducto, String codigo, String descripcion, String marca,
                    String presentacion, double costo, double precio, int stock, String foto) {

        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.presentacion = presentacion;
        this.costo = costo;
        this.precio = precio;
        this.stock = stock;
        this.foto = foto;

        this.ganancia = calcularGananciaPorcentaje();
    }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) {
        this.costo = costo;
        this.ganancia = calcularGananciaPorcentaje();
    }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        this.precio = precio;
        this.ganancia = calcularGananciaPorcentaje();
    }

    public double getGanancia() { return ganancia; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public double calcularGananciaPorcentaje() {
        if (costo > 0) {
            return ((precio - costo) / costo) * 100;
        }
        return 0.0;
    }
}


