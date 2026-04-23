package com.example.miprimeraapp;

public class producto {

    private String idProducto;
    private String codigo;
    private String descripcion;
    private String marca;
    private String presentacion;
    private String precio;
    private String foto;

    // Nuevos campos
    private String costo;
    private int stock;

    public producto(String idProducto, String codigo, String descripcion, String marca,
                    String presentacion, String precio, String foto,
                    String costo, int stock) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.presentacion = presentacion;
        this.precio = precio;
        this.foto = foto;
        this.costo = costo;
        this.stock = stock;
    }

    // Getters y Setters
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

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getCosto() { return costo; }
    public void setCosto(String costo) { this.costo = costo; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    // Método para calcular porcentaje de ganancia
    public double calcularGananciaPorcentaje() {
        try {
            double costoDouble = Double.parseDouble(costo);
            double precioDouble = Double.parseDouble(precio);
            if (costoDouble > 0) {
                return ((precioDouble - costoDouble) / costoDouble) * 100;
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return 0.0;
    }
}

