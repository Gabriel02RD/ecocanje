package ecocanje.models;

import java.io.Serializable;

/**
 * Producto canjeable por puntos.
 */
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private int puntosRequeridos;
    private String categoria;
    private int stock;

    public Producto(String nombre, int puntosRequeridos, String categoria, int stock) {
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
        this.categoria = categoria;
        this.stock = stock;
    }

    public String getNombre() { return nombre; }
    public int getPuntosRequeridos() { return puntosRequeridos; }
    public String getCategoria() { return categoria; }
    public int getStock() { return stock; }

    public boolean reducirStock() {
        if (stock > 0) {
            stock--;
            return true;
        }
        return false;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad > 0) stock += cantidad;
    }

    @Override
    public String toString() {
        return nombre + " - " + puntosRequeridos + " puntos (Stock: " + stock + ")";
    }
}
