package com.funerarias;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {
    private final String usuarioActual;
    private final LoginFrame loginFrameInstance;
    
    public HomeFrame(LoginFrame loginFrame, String usuario, boolean esAdmin) {
        this.loginFrameInstance = loginFrame;
        this.usuarioActual = usuario;
        
        // Configuración de la ventana
        setTitle("Bienvenido - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título de bienvenida
        JLabel tituloLabel = new JLabel("¡Bienvenido, " + usuarioActual + "!");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(tituloLabel, BorderLayout.NORTH);
        
        // Panel central con mensaje
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel mensajeLabel = new JLabel("Sistema de Gestión de Funerarias");
        mensajeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(mensajeLabel, gbc);
        
        // Mostrar botón de administración solo si es admin (el parámetro esAdmin se pasa correctamente)
        if (esAdmin) {
            JButton adminButton = new JButton("Panel de Administración");
            adminButton.setFont(new Font("Arial", Font.BOLD, 14));
            adminButton.setPreferredSize(new Dimension(200, 40));
            adminButton.addActionListener(e -> {
                // Mostrar el panel de administración
                loginFrameInstance.mostrarPanelAdmin();
                dispose(); // Cerrar la ventana actual
            });
            
            gbc.gridy = 1;
            gbc.insets = new Insets(20, 10, 10, 10);
            centerPanel.add(adminButton, gbc);
        }
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Panel para los botones de la derecha
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Botón de regresar
        JButton regresarButton = new JButton("Regresar");
        regresarButton.addActionListener(e -> {
            // Volver a la ventana de login
            loginFrame.setVisible(true);
            dispose();
        });
        
        // Botón de cerrar sesión
        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(e -> {
            // Volver a la ventana de login
            loginFrame.setVisible(true);
            dispose();
        });
        
        // Agregar botones al panel derecho
        rightButtonsPanel.add(regresarButton);
        rightButtonsPanel.add(Box.createHorizontalStrut(10)); // Espacio entre botones
        rightButtonsPanel.add(cerrarSesionButton);
        
        // Agregar el panel de botones al panel inferior
        bottomPanel.add(rightButtonsPanel, BorderLayout.EAST);
        
        // Agregar un borde al panel inferior
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Agregar panel a la ventana
        add(panel);
    }
}
