package com.funerarias;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestionUsuarios {
    private static final Logger LOGGER = Logger.getLogger(GestionUsuarios.class.getName());
    private static GestionUsuarios instancia;
    private final SupabaseService supabaseService;

    private GestionUsuarios() {
        this.supabaseService = SupabaseConfig.getService();
        inicializarUsuarioAdmin();
    }
    
    private void inicializarUsuarioAdmin() {
        try {
            // Verificar si el usuario admin existe usando autenticación
            if (!supabaseService.autenticarUsuario("admin", "1234")) {
                // Crear usuario administrador por defecto
                boolean exito = supabaseService.agregarUsuario("admin", "1234", true);
                if (exito) {
                    LOGGER.info("Usuario administrador creado exitosamente");
                } else {
                    LOGGER.warning("No se pudo crear el usuario administrador");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar el usuario administrador", e);
        }
    }

    public static GestionUsuarios getInstancia() {
        if (instancia == null) {
            instancia = new GestionUsuarios();
        }
        return instancia;
    }

    public boolean autenticar(String nombreUsuario, String contrasena) {
        return supabaseService.autenticarUsuario(nombreUsuario, contrasena);
    }

    public boolean esAdmin(String nombreUsuario) {
        return supabaseService.esAdmin(nombreUsuario);
    }

    public boolean agregarUsuario(String nombreUsuario, String contrasena, boolean esAdmin) {
        return supabaseService.agregarUsuario(nombreUsuario, contrasena, esAdmin);
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            List<Map<String, Object>> usuariosData = supabaseService.getUsuarios();
            for (Map<String, Object> usuarioData : usuariosData) {
                String nombre = (String) usuarioData.get("nombre_usuario");
                String contrasena = (String) usuarioData.get("contrasena");
                boolean esAdmin = (boolean) usuarioData.get("es_admin");
                usuarios.add(new Usuario(nombre, contrasena, esAdmin));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }
    
    // El método usuarioExiste ha sido eliminado ya que no se utiliza en el código
    // y su funcionalidad ya está implementada en SupabaseService

    public boolean eliminarUsuario(String nombreUsuario) {
        try {
            // Implementación para eliminar un usuario
            // Nota: Necesitarás implementar este método en SupabaseService
            LOGGER.warning("Método eliminarUsuario no implementado aún");
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al intentar eliminar el usuario: " + nombreUsuario, e);
            return false;
        }
    }

    public boolean actualizarUsuario(Usuario usuario) {
        try {
            // Implementación para actualizar un usuario
            // Nota: Necesitarás implementar este método en SupabaseService
            LOGGER.warning("Método actualizarUsuario no implementado aún");
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar el usuario: " + usuario.getNombreUsuario(), e);
            return false;
        }
    }
}
