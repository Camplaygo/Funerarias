package com.funerarias;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servicio para interactuar con la API de Supabase.
 * Maneja la autenticación y operaciones CRUD de usuarios.
 */
public class SupabaseService {
    private static final Logger LOGGER = Logger.getLogger(SupabaseService.class.getName());
    // Nombres de las tablas y columnas en Supabase
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMNA_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COLUMNA_CONTRASENA = "contrasena";
    private static final String COLUMNA_ES_ADMIN = "es_admin";
    
    // URL base de la API de Supabase
    private static final String HEADER_PREFER = "Prefer";
    private static final String RETURN_REPRESENTATION = "return=representation";
    
    private static final Gson gson = new GsonBuilder().create();
    
    // Configuración de Supabase
    private String supabaseUrl;
    private String supabaseKey;
    private boolean configurado = false;
    
    /**
     * Constructor por defecto.
     * La configuración debe establecerse usando setSupabaseConfig() antes de usar el servicio.
     */
    public SupabaseService() {
        // La configuración se establecerá más tarde mediante setSupabaseConfig
    }
    
    /**
     * Establece la configuración de Supabase.
     * @param url URL de la API de Supabase
     * @param key Clave de API de Supabase
     * @throws IllegalStateException si el servicio ya ha sido configurado
     */
    public synchronized void setSupabaseConfig(String url, String key) {
        if (configurado) {
            throw new IllegalStateException("La configuración de Supabase ya ha sido establecida");
        }
        this.supabaseUrl = url;
        this.supabaseKey = key;
        this.configurado = true;
        LOGGER.info("Configuración de Supabase establecida");
    }
    
    private void verificarConfiguracion() {
        if (!configurado) {
            throw new IllegalStateException("La configuración de Supabase no ha sido establecida");
        }
    }
    
    /**
     * Ejecuta una petición de ping al servidor para verificar la conexión.
     * @return La respuesta del servidor o null si hay un error
     * @throws IllegalStateException si la configuración no ha sido establecida
     */
    public String ejecutarPing() {
        verificarConfiguracion();
        try {
            // Usamos el endpoint raíz para verificar la conexión
            return executeRequest("GET", "", null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar ping al servidor", e);
            return null;
        }
    }

    // Método genérico para realizar peticiones HTTP
    private String executeRequest(String method, String endpoint, String requestBody) {
        return executeRequest(method, endpoint, requestBody, null);
    }
    
    private String executeRequest(String method, String endpoint, String requestBody, Map<String, String> headers) {
        verificarConfiguracion();
        
        HttpURLConnection connection = null;
        
        try {
            // Construir la URL correctamente
            String baseUrl = supabaseUrl.endsWith("/") ? supabaseUrl : supabaseUrl + "/";
            String apiEndpoint = endpoint.startsWith("/") ? endpoint.substring(1) : endpoint;
            String url = baseUrl + apiEndpoint;
            
            // Codificar la URL para manejar caracteres especiales
            URI uri = new URI(url);
            URL requestUrl = uri.toURL();
            
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("apikey", supabaseKey);
            connection.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            connection.setRequestProperty("Content-Type", "application/json");
            
            // Agregar headers adicionales si los hay
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            // Configurar timeouts
            connection.setConnectTimeout(10000); // 10 segundos
            connection.setReadTimeout(10000);    // 10 segundos

            // Para métodos que envían datos (POST, PUT, PATCH)
            if ((method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) && requestBody != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int status = connection.getResponseCode();
            
            // Leer la respuesta
            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    status >= 400 ? connection.getErrorStream() : connection.getInputStream(), "utf-8"))) {
                
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (status >= 400) {
                    String errorMsg = "Error HTTP " + status + " en " + method + " " + endpoint + ": " + response.toString();
                    LOGGER.severe(errorMsg);
                    throw new IOException(errorMsg);
                }
                
                return response.toString();
            }
        } catch (Exception e) {
            String errorMsg = "Error en la petición HTTP a " + endpoint;
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new RuntimeException("Error al conectar con el servidor: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Obtener todos los usuarios
    public List<Map<String, Object>> getUsuarios() {
        try {
            String response = executeRequest("GET", TABLE_USUARIOS + "?select=*", null);
            List<Map<String, Object>> usuarios = gson.fromJson(response, new TypeToken<List<Map<String, Object>>>(){}.getType());
            
            // Asegurarse de que las contraseñas no se devuelvan en la lista
            if (usuarios != null) {
                for (Map<String, Object> usuario : usuarios) {
                    usuario.remove(COLUMNA_CONTRASENA);
                }
            }
            
            return usuarios != null ? usuarios : new ArrayList<>();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
            return new ArrayList<>();
        }
    }

    // Autenticar un usuario
    public boolean autenticarUsuario(String nombreUsuario, String contrasena) {
        verificarConfiguracion();
        
        try {
            LOGGER.info("Iniciando autenticación para: " + nombreUsuario);
            
            // Construir la consulta para buscar el usuario
            String query = String.format("%s?%s=eq.%s", 
                TABLE_USUARIOS, 
                COLUMNA_NOMBRE_USUARIO, 
                URLEncoder.encode(nombreUsuario, "UTF-8"));
            
            LOGGER.info("Realizando consulta: " + query);
            String response = executeRequest("GET", query, null, null);
            
            // Analizar la respuesta
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            
            if (jsonArray.size() == 0) {
                LOGGER.warning("Usuario no encontrado: " + nombreUsuario);
                return false;
            }
            
            // Obtener el primer usuario (debería ser el único con ese nombre de usuario)
            JsonObject usuario = jsonArray.get(0).getAsJsonObject();
            
            if (!usuario.has(COLUMNA_CONTRASENA)) {
                LOGGER.severe("El usuario no tiene contraseña configurada: " + nombreUsuario);
                return false;
            }
            
            String contrasenaAlmacenada = usuario.get(COLUMNA_CONTRASENA).getAsString();
            boolean contrasenaValida = contrasena.equals(contrasenaAlmacenada);
            
            if (contrasenaValida) {
                LOGGER.info("Autenticación exitosa para: " + nombreUsuario);
            } else {
                LOGGER.warning("Contraseña incorrecta para el usuario: " + nombreUsuario);
            }
            
            return contrasenaValida;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al autenticar usuario: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Verificar si un usuario es administrador
    public boolean esAdmin(String nombreUsuario) {
        try {
            String query = String.format("%s?%s=eq.%s",
                TABLE_USUARIOS,
                COLUMNA_NOMBRE_USUARIO, URLEncoder.encode(nombreUsuario, "UTF-8"));
            
            String response = executeRequest("GET", query + "&select=es_admin", null);
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            
            if (jsonArray.size() > 0) {
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                return jsonObject.has("es_admin") && jsonObject.get("es_admin").getAsBoolean();
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar rol de administrador", e);
            return false;
        }
    }
    
    // Agregar un nuevo usuario
    public boolean agregarUsuario(String nombreUsuario, String contrasena, boolean esAdmin) {
        verificarConfiguracion();
        
        try {
            // Verificar si el usuario ya existe
            String checkQuery = String.format("%s?%s=eq.%s", 
                TABLE_USUARIOS, 
                COLUMNA_NOMBRE_USUARIO, 
                URLEncoder.encode(nombreUsuario, "UTF-8"));
                
            String checkResponse = executeRequest("GET", checkQuery, null, null);
            JsonArray existingUsers = JsonParser.parseString(checkResponse).getAsJsonArray();
            
            if (existingUsers.size() > 0) {
                LOGGER.warning("El usuario ya existe: " + nombreUsuario);
                return false;
            }
            
            // Crear el objeto JSON para el nuevo usuario
            JsonObject usuario = new JsonObject();
            usuario.addProperty(COLUMNA_NOMBRE_USUARIO, nombreUsuario);
            usuario.addProperty(COLUMNA_CONTRASENA, contrasena);
            usuario.addProperty(COLUMNA_ES_ADMIN, esAdmin);
            
            // Headers para la petición
            Map<String, String> headers = new HashMap<>();
            headers.put("Prefer", "return=representation");
            headers.put("Content-Type", "application/json");
            
            LOGGER.info("Intentando crear usuario: " + usuario.toString());
            
            // Enviar la petición
            String response = executeRequest("POST", TABLE_USUARIOS, usuario.toString(), headers);
            LOGGER.info("Respuesta del servidor: " + response);
            
            // Verificar si se creó el usuario
            JsonArray result = JsonParser.parseString(response).getAsJsonArray();
            boolean exito = result.size() > 0;
            
            if (exito) {
                LOGGER.info("Usuario creado exitosamente: " + nombreUsuario);
            } else {
                LOGGER.warning("No se pudo crear el usuario: " + response);
            }
            
            return exito;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al agregar usuario: " + e.getMessage(), e);
            return false;
        }
    }
    
    // Eliminar un usuario
    public boolean eliminarUsuario(String nombreUsuario) {
        try {
            // No permitir eliminar al usuario admin
            if ("admin".equalsIgnoreCase(nombreUsuario)) {
                LOGGER.warning("No se puede eliminar al usuario administrador principal");
                return false;
            }
            
            String query = String.format("%s?%s=eq.%s", 
                TABLE_USUARIOS, 
                COLUMNA_NOMBRE_USUARIO, 
                URLEncoder.encode(nombreUsuario, "UTF-8"));
                
            // Configurar headers para que retorne los registros eliminados
            Map<String, String> headers = new HashMap<>();
            headers.put(HEADER_PREFER, RETURN_REPRESENTATION);
                
            String response = executeRequest("DELETE", query, null, headers);
            JsonArray result = JsonParser.parseString(response).getAsJsonArray();
            
            // Verificar que se eliminó correctamente
            return result.size() > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario", e);
            Logger.getLogger(SupabaseService.class.getName()).log(Level.SEVERE, "Error al verificar existencia de usuario", e);
            return false;
        }
    }
    
    /**
     * Verifica la conexión con el servidor Supabase
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public boolean verificarConexion() {
        try {
            String response = executeRequest("GET", "", null);
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar conexión con Supabase", e);
            return false;
        }
    }
    
    /**
     * Método de cierre para compatibilidad con versiones anteriores
     * @deprecated No es necesario cerrar nada ya que usamos HttpURLConnection que se cierra automáticamente
     */
    @Deprecated
    public void close() {
        // Método mantenido por compatibilidad
    }
}
