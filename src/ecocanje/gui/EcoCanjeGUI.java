package ecocanje.gui;

import ecocanje.gui.components.RoundedButton;
import ecocanje.gui.components.GradientPanel;
import ecocanje.services.EcoCanjeService;
import ecocanje.models.Usuario;
import ecocanje.models.Material;
import ecocanje.models.Producto;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Interfaz gráfica principal (versión ajustada para persistencia y refresco).
 * Mantiene tu diseño original, organiza elementos para evitar solapamientos,
 * añade login/logout y muestra la fecha en el header.
 */
public class EcoCanjeGUI extends JFrame {
    private EcoCanjeService servicio;
    private Usuario usuarioActual;

    // Componentes de la interfaz
    private JTabbedPane tabbedPane;
    private JTextField txtNombre, txtCedula, txtCorreo, txtPeso;
    private JComboBox<String> cmbMaterial;
    private JLabel lblPuntos, lblUsuario, lblLogo, lblFecha;
    private JTextArea txtAreaProductos;
    private JComboBox<String> cmbProductos;
    private JPanel headerPanel;

    public EcoCanjeGUI() {
        servicio = new EcoCanjeService(); // carga datos al crear
        initComponents();
        applyModernDesign();
        setTitle("EcoCanje - Recicla y Gana Puntos");
        setSize(900, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());

        // Guardar al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                servicio.guardarDatos();
                super.windowClosing(e);
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        createHeader();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DesignConstants.SUBHEADER_FONT);

        // Usar paneles tal cual tu diseño, pero con layouts seguros
        tabbedPane.addTab("🏠 Inicio", createHomePanel());
        tabbedPane.addTab("👤 Registro", createPanelRegistro());
        tabbedPane.addTab("♻ Reciclar", createPanelReciclaje());
        tabbedPane.addTab("🛒 Canjear", createPanelCanje());
        tabbedPane.addTab("ℹ Información", createPanelInformacion());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void createHeader() {
        headerPanel = new GradientPanel(DesignConstants.PRIMARY_GREEN, DesignConstants.SECONDARY_GREEN);
        headerPanel.setLayout(new BorderLayout(10, 10));
        headerPanel.setPreferredSize(new Dimension(0, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left: logo + slogan (vertical)
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        lblLogo = new JLabel("🌱 EcoCanje");
        lblLogo.setFont(DesignConstants.TITLE_FONT);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel slogan = new JLabel("Recicla y gana puntos para tu canasta familiar");
        slogan.setFont(DesignConstants.SUBHEADER_FONT);
        slogan.setForeground(DesignConstants.LIGHT_GREEN);
        slogan.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(lblLogo);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(slogan);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Right: date, user label and login/logout buttons arranged horizontally
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.EAST;

        // Date label (static at load)
        lblFecha = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        lblFecha.setFont(DesignConstants.NORMAL_FONT);
        lblFecha.setForeground(DesignConstants.LIGHT_GREEN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(lblFecha, gbc);

        // Usuario label (multi-line allowance using HTML)
        lblUsuario = new JLabel("<html>No hay usuario<br/>registrado</html>");
        lblUsuario.setFont(DesignConstants.NORMAL_FONT);
        lblUsuario.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        rightPanel.add(lblUsuario, gbc);

        // Login button
        RoundedButton btnLogin = new RoundedButton("Iniciar sesión",
                DesignConstants.ACCENT_GREEN, DesignConstants.SECONDARY_GREEN, 12);
        btnLogin.setPreferredSize(new Dimension(140, 32));
        btnLogin.addActionListener(e -> mostrarDialogoLogin());
        gbc.gridx = 2;
        gbc.gridy = 0;
        rightPanel.add(btnLogin, gbc);

        // Logout button
        RoundedButton btnLogout = new RoundedButton("Cerrar sesión",
                DesignConstants.SUN_YELLOW, DesignConstants.SUN_YELLOW.darker(), 12);
        btnLogout.setPreferredSize(new Dimension(140, 32));
        btnLogout.addActionListener(e -> {
            usuarioActual = null;
            lblUsuario.setText("<html>No hay usuario<br/>registrado</html>");
            actualizarInicioYCatalogo();
            tabbedPane.setSelectedIndex(0);
        });
        gbc.gridx = 3;
        gbc.gridy = 0;
        rightPanel.add(btnLogout, gbc);

        headerPanel.add(rightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * DIALOGO DE LOGIN CORREGIDO:
     * - Usa GridBagLayout
     * - Campos con columnas y preferredSize para evitar "cuadrito diminuto"
     * - Request focus en el campo de cédula
     */
    private void mostrarDialogoLogin() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setPreferredSize(new Dimension(420, 130));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Cédula
        c.gridx = 0; c.gridy = 0; c.weightx = 0.0;
        pnl.add(new JLabel("Cédula:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0;
        JTextField cedulaField = new JTextField(20);
        cedulaField.setPreferredSize(new Dimension(240, 28));
        pnl.add(cedulaField, c);

        // Correo (opcional)
        c.gridx = 0; c.gridy = 1; c.weightx = 0.0;
        pnl.add(new JLabel("Correo (opcional):"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1.0;
        JTextField correoField = new JTextField(20);
        correoField.setPreferredSize(new Dimension(240, 28));
        pnl.add(correoField, c);

        // Mostrar dialogo (modal)
        int option = JOptionPane.showConfirmDialog(this, pnl, "Iniciar sesión",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String cedula = cedulaField.getText().trim();
            String correo = correoField.getText().trim();
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar la cédula.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Usuario u = !correo.isEmpty() ? servicio.iniciarSesion(cedula, correo) : servicio.iniciarSesion(cedula);
            if (u != null) {
                usuarioActual = u;
                lblUsuario.setText("<html>Usuario: " + usuarioActual.getNombre() + "<br/>Puntos: " + usuarioActual.getPuntos() + "</html>");
                actualizarInicioYCatalogo();
                JOptionPane.showMessageDialog(this, "¡Bienvenido, " + usuarioActual.getNombre() + "!", "Sesión iniciada", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario o el correo no coincide.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createHomePanel() {
        GradientPanel panel = new GradientPanel(DesignConstants.LIGHT_GREEN, Color.WHITE);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome area (top)
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel welcomeTitle = new JLabel("¡Bienvenido a EcoCanje!");
        welcomeTitle.setFont(DesignConstants.HEADER_FONT);
        welcomeTitle.setForeground(DesignConstants.DARK_GREEN);

        JLabel welcomeText = new JLabel("<html><center>Transforma tus residuos en beneficios para tu familia<br>Únete a la revolución verde y comienza a ganar puntos hoy mismo</center></html>");
        welcomeText.setFont(DesignConstants.NORMAL_FONT);
        welcomeText.setForeground(DesignConstants.EARTH_BROWN);

        JPanel welcomeBox = new JPanel();
        welcomeBox.setOpaque(false);
        welcomeBox.setLayout(new BoxLayout(welcomeBox, BoxLayout.Y_AXIS));
        welcomeBox.add(welcomeTitle);
        welcomeBox.add(Box.createVerticalStrut(6));
        welcomeBox.add(welcomeText);

        top.add(welcomeBox, BorderLayout.CENTER);

        // Stats and quick actions arranged horizontally
        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);

        JPanel statsPanel = createStatsPanel();
        bottom.add(statsPanel, BorderLayout.CENTER);

        JPanel quickPanel = createQuickActionsPanel();
        bottom.add(quickPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel userCard = createStatCard("👥 Usuarios", String.valueOf(servicio.getUsuarios().size()), "Registrados");
        JPanel materialCard = createStatCard("♻ Materiales", String.valueOf(servicio.getMateriales().size()), "Tipos aceptados");
        JPanel productCard = createStatCard("🛒 Productos", String.valueOf(servicio.getProductos().size()), "Disponibles");

        panel.add(userCard);
        panel.add(materialCard);
        panel.add(productCard);
        return panel;
    }

    private JPanel createStatCard(String title, String value, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DesignConstants.LIGHT_GREEN, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(DesignConstants.NORMAL_FONT);
        titleLabel.setForeground(DesignConstants.DARK_GREEN);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(DesignConstants.PRIMARY_GREEN);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(DesignConstants.NORMAL_FONT);
        descLabel.setForeground(DesignConstants.EARTH_BROWN);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(descLabel);

        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        panel.setOpaque(false);

        RoundedButton btnRegistro = new RoundedButton("👤 Registrar Usuario",
                DesignConstants.ACCENT_GREEN, DesignConstants.SECONDARY_GREEN, 20);
        btnRegistro.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        btnRegistro.setPreferredSize(new Dimension(200, 45));

        RoundedButton btnReciclar = new RoundedButton("♻ Depositar Material",
                DesignConstants.ACCENT_GREEN, DesignConstants.SECONDARY_GREEN, 20);
        btnReciclar.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        btnReciclar.setPreferredSize(new Dimension(200, 45));

        RoundedButton btnCanjear = new RoundedButton("🛒 Canjear Puntos",
                DesignConstants.SUN_YELLOW, DesignConstants.SUN_YELLOW.darker(), 20);
        btnCanjear.setForeground(DesignConstants.DARK_GREEN);
        btnCanjear.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        btnCanjear.setPreferredSize(new Dimension(200, 45));

        panel.add(btnRegistro);
        panel.add(btnReciclar);
        panel.add(btnCanjear);
        return panel;
    }

    private JPanel createPanelRegistro() {
        GradientPanel mainPanel = new GradientPanel(DesignConstants.LIGHT_GREEN, Color.WHITE);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(DesignConstants.PRIMARY_GREEN, 2),
                        "Registro de Usuario",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        DesignConstants.HEADER_FONT, DesignConstants.PRIMARY_GREEN),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createFormLabel("Nombre completo:"), gbc);
        gbc.gridx = 1;
        txtNombre = createStyledTextField();
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        txtCedula = createStyledTextField();
        formPanel.add(txtCedula, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Correo electrónico:"), gbc);
        gbc.gridx = 1;
        txtCorreo = createStyledTextField();
        formPanel.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        RoundedButton btnRegistrar = new RoundedButton("📝 Registrar Usuario",
                DesignConstants.PRIMARY_GREEN, DesignConstants.SECONDARY_GREEN, 15);
        btnRegistrar.setPreferredSize(new Dimension(DesignConstants.BUTTON_WIDTH, DesignConstants.BUTTON_HEIGHT));
        btnRegistrar.addActionListener(this::registrarUsuario);
        formPanel.add(btnRegistrar, gbc);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0; outer.gridy = 0;
        mainPanel.add(formPanel, outer);

        return mainPanel;
    }

    private JPanel createPanelReciclaje() {
        GradientPanel mainPanel = new GradientPanel(DesignConstants.LIGHT_GREEN, Color.WHITE);
        mainPanel.setLayout(new BorderLayout(14, 14));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(DesignConstants.ACCENT_GREEN, 2),
                        "Depositar Material Reciclable",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        DesignConstants.HEADER_FONT, DesignConstants.ACCENT_GREEN),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createFormLabel("Tipo de material:"), gbc);
        gbc.gridx = 1;
        cmbMaterial = createStyledComboBox();
        cmbMaterial.setPrototypeDisplayValue("XXXXXXXXXXXXXXXX");
        for (Material material : servicio.getMateriales()) {
            cmbMaterial.addItem(material.getTipo());
        }
        formPanel.add(cmbMaterial, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Peso (kg):"), gbc);
        gbc.gridx = 1;
        txtPeso = createStyledTextField();
        formPanel.add(txtPeso, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        RoundedButton btnCalcular = new RoundedButton("🧮 Calcular Puntos",
                DesignConstants.ACCENT_GREEN, DesignConstants.SECONDARY_GREEN, 15);
        btnCalcular.setPreferredSize(new Dimension(DesignConstants.BUTTON_WIDTH, DesignConstants.BUTTON_HEIGHT));
        btnCalcular.addActionListener(this::calcularPuntos);
        formPanel.add(btnCalcular, gbc);

        gbc.gridy = 3;
        lblPuntos = new JLabel("Puntos ganados: 0", SwingConstants.CENTER);
        lblPuntos.setFont(DesignConstants.HEADER_FONT);
        lblPuntos.setForeground(DesignConstants.PRIMARY_GREEN);
        lblPuntos.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        formPanel.add(lblPuntos, gbc);

        formWrap.add(formPanel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(DesignConstants.NORMAL_FONT);
        infoArea.setBackground(Color.WHITE);
        infoArea.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(DesignConstants.EARTH_BROWN, 1),
                        "💡 Guía de Materiales",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        DesignConstants.SUBHEADER_FONT, DesignConstants.EARTH_BROWN),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        infoArea.setText(generarInfoMateriales());
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(360, 220));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formWrap, scrollPane);
        splitPane.setResizeWeight(0.6);
        splitPane.setOneTouchExpandable(true);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createPanelCanje() {
        GradientPanel mainPanel = new GradientPanel(DesignConstants.LIGHT_GREEN, Color.WHITE);
        mainPanel.setLayout(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(DesignConstants.SUN_YELLOW, 2),
                        "Canjear Puntos por Productos",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        DesignConstants.HEADER_FONT, DesignConstants.EARTH_BROWN),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        topPanel.add(createFormLabel("Seleccionar Producto:"));

        cmbProductos = createStyledComboBox();
        cmbProductos.setPreferredSize(new Dimension(220, 30));
        actualizarComboProductos();
        topPanel.add(cmbProductos);

        RoundedButton btnCanjear = new RoundedButton("🔄 Canjear Producto",
                DesignConstants.SUN_YELLOW, DesignConstants.SUN_YELLOW.darker(), 15);
        btnCanjear.setForeground(DesignConstants.DARK_GREEN);
        btnCanjear.setPreferredSize(new Dimension(180, 35));
        btnCanjear.addActionListener(this::canjearProducto);
        topPanel.add(btnCanjear);

        txtAreaProductos = new JTextArea();
        txtAreaProductos.setEditable(false);
        txtAreaProductos.setFont(DesignConstants.NORMAL_FONT);
        txtAreaProductos.setBackground(Color.WHITE);
        txtAreaProductos.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DesignConstants.LIGHT_GREEN, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        actualizarListaProductos();

        JScrollPane scrollPane = new JScrollPane(txtAreaProductos);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(DesignConstants.ACCENT_GREEN, 1),
                        "🛍️ Catálogo de Productos Disponibles",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        DesignConstants.SUBHEADER_FONT, DesignConstants.ACCENT_GREEN),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setPreferredSize(new Dimension(600, 360));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createPanelInformacion() {
        GradientPanel mainPanel = new GradientPanel(DesignConstants.LIGHT_GREEN, Color.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(DesignConstants.NORMAL_FONT);
        infoArea.setBackground(Color.WHITE);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DesignConstants.PRIMARY_GREEN, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        infoArea.setText(crearTextoInformacion());

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(null);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    // Component creation helpers (igual que tu versión)
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(DesignConstants.SUBHEADER_FONT);
        label.setForeground(DesignConstants.DARK_GREEN);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(DesignConstants.NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DesignConstants.ACCENT_GREEN, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(DesignConstants.NORMAL_FONT);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DesignConstants.ACCENT_GREEN, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return combo;
    }

    private void applyModernDesign() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("TabbedPane.background", DesignConstants.LIGHT_GREEN);
            UIManager.put("TabbedPane.foreground", DesignConstants.DARK_GREEN);
            UIManager.put("TabbedPane.selected", DesignConstants.PRIMARY_GREEN);
            UIManager.put("Panel.background", DesignConstants.LIGHT_GREEN);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("ComboBox.background", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image createAppIcon() {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(DesignConstants.PRIMARY_GREEN);
        g2d.fillOval(2, 2, size-4, size-4);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2d.drawString("♻", 8, 24);
        g2d.dispose();

        return image;
    }

    // ============================
    // Acciones: registrar, calcular, canjear
    // ============================
    private void registrarUsuario(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        String cedula = txtCedula.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || cedula.isEmpty() || correo.isEmpty()) {
            showMessage("Por favor, complete todos los campos.", "Campos Incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!servicio.esCorreoValido(correo)) {
            showMessage("Por favor, ingrese un correo válido.", "Correo inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = servicio.registrarUsuario(nombre, cedula, correo);
        if (ok) {
            usuarioActual = servicio.buscarUsuario(cedula); // cargar la instancia guardada
            lblUsuario.setText("<html>Usuario: " + usuarioActual.getNombre() + "<br/>Puntos: " + usuarioActual.getPuntos() + "</html>");
            showMessage("Usuario registrado exitosamente!\nID: " + usuarioActual.getIdUsuario(),
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

            txtNombre.setText("");
            txtCedula.setText("");
            txtCorreo.setText("");

            // refrescar interfaces que muestran usuarios/productos
            actualizarInicioYCatalogo();
            tabbedPane.setSelectedIndex(0);
        } else {
            showMessage("No se pudo registrar. Cédula existente o datos inválidos.", "Registro Fallido",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularPuntos(ActionEvent e) {
        if (usuarioActual == null) {
            showMessage("Primero debe registrar o buscar un usuario.", "Usuario No Registrado",
                    JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(1);
            return;
        }

        try {
            String material = (String) cmbMaterial.getSelectedItem();
            double peso = Double.parseDouble(txtPeso.getText());

            if (peso <= 0) {
                showMessage("El peso debe ser mayor a 0.", "Peso Inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // registrar reciclaje y persistir
            boolean ok = servicio.registrarReciclaje(usuarioActual.getCedula(), material, peso);
            if (ok) {
                // recargar usuario desde el servicio (asegura consistencia con datos persistidos)
                usuarioActual = servicio.buscarUsuario(usuarioActual.getCedula());
                lblPuntos.setText("Puntos ganados: " + servicio.calcularPuntos(material, peso));
                lblUsuario.setText("<html>Usuario: " + usuarioActual.getNombre() + "<br/>Puntos: " + usuarioActual.getPuntos() + "</html>");
                showMessage("¡Material registrado exitosamente!\n" +
                                "Material: " + material + "\n" +
                                "Peso: " + peso + " kg\n" +
                                "Puntos ganados: " + servicio.calcularPuntos(material, peso) + "\n" +
                                "Total de puntos: " + usuarioActual.getPuntos(),
                        "Depósito Exitoso", JOptionPane.INFORMATION_MESSAGE);

                txtPeso.setText("");
                actualizarInicioYCatalogo();
            } else {
                showMessage("No fue posible registrar el reciclaje.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            showMessage("Por favor, ingrese un peso válido.", "Peso Inválido",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void canjearProducto(ActionEvent e) {
        if (usuarioActual == null) {
            showMessage("Primero debe registrar un usuario.", "Usuario No Registrado",
                    JOptionPane.WARNING_MESSAGE);
            tabbedPane.setSelectedIndex(1);
            return;
        }

        if (cmbProductos.getSelectedItem() == null) {
            showMessage("No hay productos disponibles para canjear.", "Sin Productos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String productoSeleccionado = cmbProductos.getSelectedItem().toString();

        boolean ok = servicio.canjearProducto(usuarioActual.getCedula(), productoSeleccionado);
        if (ok) {
            usuarioActual = servicio.buscarUsuario(usuarioActual.getCedula());
            showMessage("¡Canje exitoso!\n" +
                            "Producto: " + productoSeleccionado + "\n" +
                            "Puntos restantes: " + usuarioActual.getPuntos(),
                    "Canje Exitoso", JOptionPane.INFORMATION_MESSAGE);

            actualizarListaProductos();
            actualizarComboProductos();
            lblUsuario.setText("<html>Usuario: " + usuarioActual.getNombre() + "<br/>Puntos: " + usuarioActual.getPuntos() + "</html>");
        } else {
            showMessage("No se pudo realizar el canje.\n" +
                            "Verifique que tenga puntos suficientes y que el producto esté disponible.",
                    "Error en Canje", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // ============================
    // Refrescos y utilitarios
    // ============================
    private String generarInfoMateriales() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATERIALES ACEPTADOS ===\n\n");
        for (Material material : servicio.getMateriales()) {
            sb.append("🌱 ").append(material.getTipo()).append("\n");
            sb.append("   ").append(material.getDescripcion()).append("\n");
            sb.append("   Puntos: ").append(material.getPuntosPorKg()).append(" por kg\n\n");
        }
        sb.append("💡 Consejo: Separe y limpie los materiales\nantes de depositarlos para obtener\nmáximos beneficios.");
        return sb.toString();
    }

    private void actualizarListaProductos() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PRODUCTOS DISPONIBLES ===\n\n");
        for (Producto producto : servicio.getProductosDisponibles()) {
            sb.append("🛒 ").append(producto.getNombre()).append("\n");
            sb.append("   Categoría: ").append(producto.getCategoria()).append("\n");
            sb.append("   Puntos requeridos: ").append(producto.getPuntosRequeridos()).append("\n");
            sb.append("   Stock disponible: ").append(producto.getStock()).append("\n\n");
        }
        txtAreaProductos.setText(sb.toString());
    }

    private void actualizarComboProductos() {
        cmbProductos.removeAllItems();
        for (Producto producto : servicio.getProductosDisponibles()) {
            cmbProductos.addItem(producto.getNombre());
        }
    }

    private void actualizarInicioYCatalogo() {
        // Actualiza estadísticas y catálogo
        actualizarListaProductos();
        actualizarComboProductos();
        // Repaint header to reflect usuario label changes
        headerPanel.repaint();
    }

    private String crearTextoInformacion() {
        return """
               🌱 ECOCANJE - RECICLA Y GANA PUNTOS 🌱

               Descripción del Proyecto:
               EcoCanje es un sistema innovador que combina tecnología, sostenibilidad 
               y beneficio social para transformar la cultura del reciclaje.

               🎯 Objetivos Principales:
               • Incentivar el reciclaje mediante recompensas tangibles
               • Reducir la acumulación de residuos sólidos en las comunidades
               • Mejorar la economía familiar con productos de canasta básica
               • Promover la economía circular y la responsabilidad ambiental

               🔧 Tecnología Implementada:
               • Recipientes inteligentes con sensores IoT
               • Registro automático de tipo y peso de materiales
               • App móvil y tarjetas QR para acumulación de puntos
               • Sistema de gestión de datos en tiempo real

               ♻ Materiales Aceptados:
               • Plástico (botellas PET, envases)
               • Vidrio (botellas, frascos)
               • Cartón (cajas, empaques)
               • Aluminio (latas, materiales limpios)
               • Papel (periódico, revistas, papel blanco)

               🏆 Beneficios para los Usuarios:
               • Puntos canjeables por productos de canasta familiar
               • Contribución directa al cuidado del medio ambiente
               • Participación en programas de educación ambiental
               • Acceso a empleos verdes y oportunidades de desarrollo

               📈 Visión a Futuro:
               Para el 2035, EcoCanje será una red nacional presente en todas 
               las principales ciudades, con miles de usuarios activos y alianzas 
               estratégicas con supermercados y empresas sostenibles.

               ¡Únete a la revolución verde y comienza a transformar 
               tus residuos en beneficios para tu familia!
               """;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EcoCanjeGUI().setVisible(true));
    }
}
