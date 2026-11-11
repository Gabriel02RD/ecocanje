package ecocanje.models;

import java.io.Serializable;

/**
 * Representa un tipo de material reciclable y su factor de puntos por kg.
 */
public class Material implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo;
    private int puntosPorKg;
    private String descripcion;

    public Material(String tipo, int puntosPorKg, String descripcion) {
        this.tipo = tipo;
        this.puntosPorKg = puntosPorKg;
        this.descripcion = descripcion;
    }

    public String getTipo() { return tipo; }
    public int getPuntosPorKg() { return puntosPorKg; }
    public String getDescripcion() { return descripcion; }

    public int calcularPuntos(double peso) {
        if (peso <= 0) return 0;
        return (int) Math.round(peso * puntosPorKg);
    }

    @Override
    public String toString() {
        return tipo + " - " + descripcion + " (" + puntosPorKg + " puntos/kg)";
    }
}
