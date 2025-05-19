package com.funerarias;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de configuración para el servicio de Supabase.
 * Sigue el patrón Singleton para asegurar una única instancia.
 * Proporciona acceso global a la configuración de Supabase.
 */
public final class SupabaseConfig {
    // URL base de la API de Supabase con el endpoint de la API REST
    // Formato: https://[project-ref].supabase.co/rest/v1
    private static final String SUPABASE_URL = "https://bjdyzuuxqgwjgscjmfzy.supabase.co/rest/v1";
    
    // Clave de API de Supabase (API Key)
    // Se usa para autenticar las peticiones a la API
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJqZHl6dXV4cWd3amdzY2ptZnp5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc2MTE4ODYsImV4cCI6MjA2MzE4Nzg4Nn0.dNr0KMvZ9tiibpCYz0G-ugVOWk44h1jq4FSbKFAROQA";
    
    // Instancia única del servicio (patrón Singleton)
    private static SupabaseService instance;
    
    // Logger para registrar eventos
    private static final Logger LOGGER = Logger.getLogger(SupabaseConfig.class.getName());
    
    /**
     * Constructor privado para prevenir la instanciación directa.
     * Lanza una excepción si se intenta instanciar la clase.
     */
    private SupabaseConfig() {
        throw new IllegalStateException("Esta es una clase de utilidad y no puede ser instanciada");
    }
    
    /**
     * Obtiene la instancia única del servicio Supabase.
     * Si no existe, crea una nueva instancia.
     * 
     * @return Instancia configurada de SupabaseService
     */
    public static synchronized SupabaseService getService() {
        // Si no existe una instancia, crea una nueva
        if (instance == null) {
            instance = new SupabaseService();
            // Configura la URL y la clave de la API
            instance.setSupabaseConfig(SUPABASE_URL, SUPABASE_KEY);
        }
        return instance;
    }
    
    /**
     * Verifica la conexión con Supabase.
     * 
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public static boolean verificarConexion() {
        try {
            // Intenta obtener el servicio para verificar la conexión
            SupabaseService service = getService();
            // Realiza una petición de prueba
            String response = service.ejecutarPing();
            return response != null && response.contains("Bienvenido a Supabase");
        } catch (Exception e) {
            // Registra el error y devuelve false
            LOGGER.log(Level.SEVERE, "Error al verificar la conexión con Supabase", e);
            return false;
        }
    }
}
