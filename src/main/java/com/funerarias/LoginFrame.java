package com.funerarias;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private JFrame adminFrame;
    private DefaultTableModel tableModel;
    private GestionUsuarios gestionUsuarios;
    
    public LoginFrame() {
        // Configuración de la ventana
        setTitle("Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Inicializar gestión de usuarios
        gestionUsuarios = GestionUsuarios.getInstancia();

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel tituloLabel = new JLabel("INICIO DE SESIÓN");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(tituloLabel, gbc);

        // Etiqueta y campo de usuario
        JLabel usuarioLabel = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(usuarioLabel, gbc);

        usuarioField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(usuarioField, gbc);

        // Etiqueta y campo de contraseña
        JLabel contrasenaLabel = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(contrasenaLabel, gbc);

        contrasenaField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(contrasenaField, gbc);

        // Botón de inicio de sesión
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = usuarioField.getText();
                String contrasena = new String(contrasenaField.getPassword());
                
                if (gestionUsuarios.autenticar(usuario, contrasena)) {
                    boolean esAdmin = gestionUsuarios.esAdmin(usuario);
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "¡Bienvenido, " + usuario + "!" + 
                        (esAdmin ? "\nTienes privilegios de administrador." : ""),
                        "Inicio de Sesión Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Mostrar la pantalla de inicio
                    HomeFrame homeFrame = new HomeFrame(LoginFrame.this, usuario, esAdmin);
                    homeFrame.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Usuario o contraseña incorrectos",
                        "Error de Inicio de Sesión",
                        JOptionPane.ERROR_MESSAGE);
                    // Limpiar campos
                    contrasenaField.setText("");
                    usuarioField.requestFocus();
                }
            }
        });
        
        // Botón de salir
        JButton salirButton = new JButton("Salir");
        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(salirButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        // Agregar panel a la ventana
        add(panel);
    }

    
    public void mostrarPanelAdmin() {
        if (adminFrame != null) {
            adminFrame.dispose();
        }
        
        adminFrame = new JFrame("Panel de Administración");
        adminFrame.setSize(600, 400);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla de usuarios
        String[] columnas = {"Usuario", "Es Administrador"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        JTable tablaUsuarios = new JTable(tableModel);
        actualizarTablaUsuarios();
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel botonesPanel = new JPanel(new BorderLayout(10, 10));
        
        // Panel para botones de la izquierda
        JPanel leftButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Botón de regresar al inicio
        JButton regresarBtn = new JButton("← Regresar al Inicio");
        regresarBtn.setBackground(new Color(220, 220, 255));
        regresarBtn.setFont(new Font("Arial", Font.BOLD, 12));
        regresarBtn.addActionListener(e -> {
            adminFrame.dispose();
            HomeFrame homeFrame = new HomeFrame(this, usuarioField.getText(), true);
            homeFrame.setVisible(true);
        });
        leftButtonsPanel.add(regresarBtn);
        
        // Panel para botones centrales
        JPanel centerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Botones de administración
        JButton agregarBtn = new JButton("Agregar Usuario");
        agregarBtn.addActionListener(e -> agregarUsuario());
        
        JButton eliminarBtn = new JButton("Eliminar Usuario");
        eliminarBtn.addActionListener(e -> {
            int filaSeleccionada = tablaUsuarios.getSelectedRow();
            if (filaSeleccionada >= 0) {
                String usuario = (String) tableModel.getValueAt(filaSeleccionada, 0);
                if (!usuario.equals("admin")) { // No permitir eliminar al admin principal
                    int confirmacion = JOptionPane.showConfirmDialog(
                        adminFrame,
                        "¿Está seguro de eliminar al usuario " + usuario + "?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        if (gestionUsuarios.eliminarUsuario(usuario)) {
                            actualizarTablaUsuarios();
                            JOptionPane.showMessageDialog(adminFrame, "Usuario eliminado exitosamente.");
                        } else {
                            JOptionPane.showMessageDialog(adminFrame, 
                                "No se pudo eliminar el usuario.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(adminFrame, 
                        "No se puede eliminar al administrador principal.", 
                        "Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(adminFrame, 
                    "Por favor seleccione un usuario para eliminar.", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton cerrarBtn = new JButton("Cerrar Sesión");
        cerrarBtn.addActionListener(e -> {
            adminFrame.dispose();
            usuarioField.setText("");
            contrasenaField.setText("");
            usuarioField.requestFocus();
            setVisible(true);
        });
        
        // Agregar botones al panel central
        centerButtonsPanel.add(agregarBtn);
        centerButtonsPanel.add(eliminarBtn);
        centerButtonsPanel.add(cerrarBtn);
        
        // Configurar paneles de botones
        botonesPanel.add(leftButtonsPanel, BorderLayout.WEST);
        botonesPanel.add(centerButtonsPanel, BorderLayout.CENTER);
        
        // Agregar borde y margen al panel de botones
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(botonesPanel, BorderLayout.SOUTH);
        
        adminFrame.add(panel);
        adminFrame.setVisible(true);
        setVisible(false);
    }
    
    private void actualizarTablaUsuarios() {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Usuario> usuarios = gestionUsuarios.getUsuarios();
        for (Usuario usuario : usuarios) {
            tableModel.addRow(new Object[]{
                usuario.getNombreUsuario(),
                usuario.esAdmin() ? "Sí" : "No"
            });
        }
    }
    
    private void agregarUsuario() {
        JTextField usuarioField = new JTextField(20);
        JPasswordField contrasenaField = new JPasswordField(20);
        JCheckBox esAdminCheck = new JCheckBox("Es Administrador");
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nombre de Usuario:"));
        panel.add(usuarioField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);
        panel.add(esAdminCheck);
        
        int resultado = JOptionPane.showConfirmDialog(
            adminFrame,
            panel,
            "Agregar Nuevo Usuario",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
            
        if (resultado == JOptionPane.OK_OPTION) {
            String usuario = usuarioField.getText().trim();
            String contrasena = new String(contrasenaField.getPassword());
            boolean esAdmin = esAdminCheck.isSelected();
            
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(adminFrame, 
                    "Por favor complete todos los campos.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (gestionUsuarios.agregarUsuario(usuario, contrasena, esAdmin)) {
                actualizarTablaUsuarios();
                JOptionPane.showMessageDialog(adminFrame, "Usuario agregado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(adminFrame, 
                    "El nombre de usuario ya existe. Por favor elija otro.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            }
        });
    }
}
