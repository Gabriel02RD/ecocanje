package ecocanje.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa un usuario del sistema EcoCanje.
 * Serializable para permitir persistencia sencilla.
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String cedula;
    private String correo;
    private int puntos;
    private String idUsuario;
    private LocalDateTime fechaRegistro;
    private List<String> historialMovimientos; // historial de movimientos

    public Usuario(String nombre, String cedula, String correo) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.correo = correo;
        this.puntos = 0;
        this.idUsuario = "ECO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.fechaRegistro = LocalDateTime.now();
        this.historialMovimientos = new ArrayList<>();
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }
    public String getCorreo() { return correo; }
    public int getPuntos() { return puntos; }
    public String getIdUsuario() { return idUsuario; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public List<String> getHistorialMovimientos() { return new ArrayList<>(historialMovimientos); }

    // Mutadores de puntos
    public void agregarPuntos(int puntos) {
        if (puntos > 0) this.puntos += puntos;
    }

    public boolean canjearPuntos(int puntos) {
        if (puntos <= 0) return false;
        if (this.puntos >= puntos) {
            this.puntos -= puntos;
            return true;
        }
        return false;
    }

    // Historial
    public void agregarMovimiento(String descripcion) {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            historialMovimientos.add(time + " - " + descripcion);
        }
    }

    // Formato amigable de fecha
    public String getFechaRegistroFormateada() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return fechaRegistro.format(fmt);
    }

    @Override
    public String toString() {
        return nombre + " (" + cedula + ") - Puntos: " + puntos;
    }
}
