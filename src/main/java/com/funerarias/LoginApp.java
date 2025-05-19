package com.funerarias;

import javax.swing.SwingUtilities;

public class LoginApp {
    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear y mostrar la ventana de inicio de sesión
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al iniciar la aplicación: ");
                e.printStackTrace();
            }
        });
    }
}
