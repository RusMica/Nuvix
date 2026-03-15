package com.covielloDevs.SistemaDeVerificacion.services.storage;

import com.covielloDevs.SistemaDeVerificacion.utils.SupabaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class SupabaseStorageService {

    private final WebClient webClient;
    private final SupabaseProperties properties;
    private final String bucket;

    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);

    public SupabaseStorageService(SupabaseProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
        this.bucket = properties.getBucket();
    }

    public String upload(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No se puede subir un archivo vacío.");
        }

        String path = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String fullPath = StringUtils.hasText(folder) ? folder + "/" + path : path;

        try {
            webClient.post()
                    .uri("/storage/v1/object/{bucket}/{path}", bucket, fullPath)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                    .header("apikey", properties.getKey())
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(Map.class) // Supabase devuelve un JSON con el 'Key'
                    .block(Duration.ofSeconds(30)); // Es mejor usar block con timeout
        } catch (Exception e) {
            logger.error("Error al subir el archivo a Supabase: {}", fullPath, e);
            throw new RuntimeException("Error al subir el archivo: " + e.getMessage(), e);
        }

        return fullPath;
    }

    public byte[] download(String path) {
        return webClient.get()
                .uri("/storage/v1/object/" + bucket + "/" + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header("apikey", properties.getKey())
                .retrieve()
                .bodyToMono(byte[].class)
                .block(Duration.ofSeconds(10));
    }

    /**
     * Borra archivos de un bucket de Supabase basado en una lista de prefijos (carpetas).
     * @param pathsToDelete Lista de rutas a eliminar. Ejemplo: ["pdfs/file1.pdf", "spreadsheets/"]
     */
    public void deleteFiles(List<String> pathsToDelete) {
        Map<String, List<String>> body = Map.of("prefixes", pathsToDelete);
        
        
        webClient.method(HttpMethod.DELETE)
                .uri("/storage/v1/object/{bucket}", bucket)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header("apikey", properties.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(30)); // Espera a que la operación termine
    }

    /**
     * Lista todos los archivos dentro de una carpeta (prefijo) específica en el bucket.
     * @param folder La carpeta de la cual listar los archivos.
     * @return Una lista de rutas completas de los archivos encontrados.
     */
    public List<String> listFiles(String folder) {
        // El cuerpo de la petición para listar archivos en Supabase
        Map<String, Object> body = Map.of(
                "prefix", folder,
                "limit", 1000, // Aumenta si esperas más de 1000 archivos por carpeta
                "offset", 0,
                "sortBy", Map.of("column", "name", "order", "asc")
        );

        List<Map<String, Object>> response = webClient.post()
                .uri("/storage/v1/object/list/{bucket}", bucket)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header("apikey", properties.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(List.class)
                .block(Duration.ofSeconds(20));

        if (response == null) {
            return List.of();
        }

        return response.stream()
                .map(fileInfo -> folder + "/" + fileInfo.get("name"))
                .toList();
    }

    public String getSignedUrl(String filePath, int expiresInSeconds) {
        String cleanPath = filePath.trim().replaceAll("\r|\n", "");
        String endpoint = "/storage/v1/object/sign/" + bucket + "/" + cleanPath;
        Map<String, Integer> body = Map.of("expiresIn", expiresInSeconds);

        var response = webClient.post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .header("apikey", properties.getKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block(Duration.ofSeconds(10));

        if (response != null && response.containsKey("signedURL")) {
            return properties.getUrl() + response.get("signedURL");
        }
        return null;
    }

    public String getPublicUrl(String path) {
        return properties.getUrl() + "/storage/v1/object/public/" + bucket + "/" + path;
    }
}
