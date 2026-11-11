package ecocanje.services;

import ecocanje.models.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Servicio principal del sistema EcoCanje.
 * - Mantiene listas y mapas de usuarios, materiales y productos.
 * - Provee persistencia ligera mediante serialización.
 * - Implementa validaciones y operaciones de negocio.
 */
public class EcoCanjeService implements Serializable {
    private static final long serialVersionUID = 1L;

    // Datos en memoria
    private Map<String, Usuario> usuariosPorCedula; // clave: cedula
    private List<Material> materiales;
    private List<Producto> productos;

    // Archivo de persistencia
    private static final String DATA_FILE = "ecocanje_data.ser";

    public EcoCanjeService() {
        // Intentar cargar datos; si no, inicializar por defecto
        if (!cargarDatos()) {
            usuariosPorCedula = new LinkedHashMap<>();
            materiales = new ArrayList<>();
            productos = new ArrayList<>();
            inicializarDatosPorDefecto();
            guardarDatos(); // crear archivo inicial
        }
    }

    // Inicialización por defecto (cuando no hay archivo)
    private void inicializarDatosPorDefecto() {
        materiales.add(new Material("Plástico", 30, "Botellas PET, envases plásticos"));
        materiales.add(new Material("Vidrio", 15, "Botellas de vidrio, frascos"));
        materiales.add(new Material("Cartón", 12, "Cajas, empaques de cartón"));
        materiales.add(new Material("Aluminio", 50, "Latas de bebidas, aluminio limpio"));
        materiales.add(new Material("Papel", 10, "Periódico, revistas, papel blanco"));

        productos.add(new Producto("Arroz 1kg", 100, "Granos", 20));
        productos.add(new Producto("Aceite 1lt", 200, "Aceites", 15));
        productos.add(new Producto("Lentejas 500g", 80, "Granos", 25));
        productos.add(new Producto("Atún enlatado", 120, "Enlatados", 30));
        productos.add(new Producto("Leche 1lt", 150, "Lácteos", 18));
        productos.add(new Producto("Azúcar 1kg", 90, "Endulzantes", 22));
        productos.add(new Producto("Café 500g", 240, "Bebidas", 12));
        productos.add(new Producto("Jabón de baño", 50, "Aseo", 40));
    }

    // ============================
    // Persistencia: guardar y cargar
    // ============================
    public boolean guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean cargarDatos() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof EcoCanjeService) {
                EcoCanjeService loaded = (EcoCanjeService) obj;
                this.usuariosPorCedula = loaded.usuariosPorCedula != null ? loaded.usuariosPorCedula : new LinkedHashMap<>();
                this.materiales = loaded.materiales != null ? loaded.materiales : new ArrayList<>();
                this.productos = loaded.productos != null ? loaded.productos : new ArrayList<>();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================
    // Validaciones y utilidades
    // ============================
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
        Pattern.CASE_INSENSITIVE
    );

    public boolean esCorreoValido(String correo) {
        return correo != null && EMAIL_PATTERN.matcher(correo).matches();
    }

    // ============================
    // Operaciones con usuarios
    // ============================
    /**
     * Registra un usuario. Devuelve:
     *  - true si se registró correctamente.
     *  - false si cédula ya existe o correo inválido.
     */
    public boolean registrarUsuario(String nombre, String cedula, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (cedula == null || cedula.trim().isEmpty()) return false;
        if (!esCorreoValido(correo)) return false;
        if (usuariosPorCedula.containsKey(cedula)) return false;

        Usuario u = new Usuario(nombre.trim(), cedula.trim(), correo.trim());
        usuariosPorCedula.put(cedula.trim(), u);
        guardarDadosafe();
        return true;
    }

    // small fix: helper method to keep try-catch single (original code used guardarDatos())
    private void guardarDadosafe() {
        try {
            guardarDatos();
        } catch (Exception e) {
            // swallow to avoid breaking flow (persist failure already handled in guardarDatos)
        }
    }

    public Usuario buscarUsuario(String cedula) {
        if (cedula == null) return null;
        return usuariosPorCedula.get(cedula.trim());
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuariosPorCedula.values());
    }

    // ============================
    // Inicio de sesión
    // ============================
    public Usuario iniciarSesion(String cedula, String correo) {
        Usuario u = buscarUsuario(cedula);
        if (u != null) {
            if (correo == null || correo.trim().isEmpty()) return u;
            if (u.getCorreo().equalsIgnoreCase(correo.trim())) return u;
        }
        return null;
    }

    public Usuario iniciarSesion(String cedula) {
        return buscarUsuario(cedula);
    }

    // ============================
    // Materiales
    // ============================
    public List<Material> getMateriales() {
        return new ArrayList<>(materiales);
    }

    public int calcularPuntos(String tipoMaterial, double peso) {
        if (tipoMaterial == null || peso <= 0) return 0;
        for (Material m : materiales) {
            if (m.getTipo().equalsIgnoreCase(tipoMaterial)) {
                return m.calcularPuntos(peso);
            }
        }
        return 0;
    }

    // ============================
    // Productos / Canje
    // ============================
    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public List<Producto> getProductosDisponibles() {
        List<Producto> disponibles = new ArrayList<>();
        for (Producto p : productos) if (p.getStock() > 0) disponibles.add(p);
        return disponibles;
    }

    public boolean canjearProducto(String cedulaUsuario, String nombreProducto) {
        Usuario u = buscarUsuario(cedulaUsuario);
        if (u == null) return false;

        Producto seleccionado = null;
        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombreProducto)) {
                seleccionado = p;
                break;
            }
        }
        if (seleccionado == null) return false;
        if (seleccionado.getStock() <= 0) return false;
        if (u.getPuntos() < seleccionado.getPuntosRequeridos()) return false;

        if (u.canjearPuntos(seleccionado.getPuntosRequeridos()) && seleccionado.reducirStock()) {
            u.agregarMovimiento("Canjeó: " + seleccionado.getNombre() + " (" + seleccionado.getPuntosRequeridos() + " pts)");
            guardarDadosafe();
            return true;
        }
        return false;
    }

    public boolean canjearProducto(Usuario usuario, String nombreProducto) {
        if (usuario == null) return false;
        return canjearProducto(usuario.getCedula(), nombreProducto);
    }

    public boolean registrarReciclaje(String cedulaUsuario, String tipoMaterial, double pesoKg) {
        Usuario u = buscarUsuario(cedulaUsuario);
        if (u == null) return false;
        if (pesoKg <= 0) return false;
        int puntos = calcularPuntos(tipoMaterial, pesoKg);
        if (puntos <= 0) return false;
        u.agregarPuntos(puntos);
        u.agregarMovimiento("Recicló " + pesoKg + " kg de " + tipoMaterial + " (" + puntos + " pts)");
        guardarDadosafe();
        return true;
    }

    public void agregarProducto(Producto p) {
        if (p != null) {
            productos.add(p);
            guardarDadosafe();
        }
    }

    public void agregarMaterial(Material m) {
        if (m != null) {
            materiales.add(m);
            guardarDadosafe();
        }
    }
}
