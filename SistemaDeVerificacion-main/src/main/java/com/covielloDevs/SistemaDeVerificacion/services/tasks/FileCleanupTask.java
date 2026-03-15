package com.covielloDevs.SistemaDeVerificacion.services.tasks;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.repositories.EventoRepository;
import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupTask.class);
    private final SupabaseStorageService supabaseStorageService;
    private final EventoRepository eventoRepository;

    public FileCleanupTask(SupabaseStorageService supabaseStorageService, EventoRepository eventoRepository) {
        this.supabaseStorageService = supabaseStorageService;
        this.eventoRepository = eventoRepository;
    }

    /**
     * Tarea programada que realiza dos tipos de limpieza en Supabase:
     * 1. Elimina archivos asociados a eventos que ya han finalizado.
     * 2. Elimina archivos "huérfanos" de las carpetas 'pdfs' y 'spreadsheets' que no están asociados a ningún evento.
     * Se ejecuta todos los días a las 2:00 AM.
     * Cron: segundo minuto hora día-del-mes mes día-de-la-semana
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupSupabaseFiles() {
        logger.info("--- Iniciando Tarea de Limpieza de Supabase ---");
        try {
            cleanupFinishedEventFiles();
            cleanupOrphanFiles(List.of("pdfs", "spreadsheets"));
        } catch (Exception e) {
            logger.error("Error inesperado durante la ejecución de la tarea de limpieza principal.", e);
        }
        logger.info("--- Tarea de Limpieza de Supabase Finalizada ---");
    }

    private void cleanupFinishedEventFiles() {
        logger.info("Iniciando tarea de limpieza de archivos de eventos finalizados...");
    
        List<Evento> finishedEvents = eventoRepository.findFinishedEventsWithFiles(LocalDate.now());

        if (finishedEvents.isEmpty()) {
            logger.info("No se encontraron archivos de eventos finalizados para limpiar.");
            return;
        }

        List<String> filesToDelete = finishedEvents.stream()
                .flatMap(evento -> Stream.of(evento.getItinerario(), evento.getListaParticipantes()))
                .filter(StringUtils::hasText)
                .map(this::extractPathFromUrl)
                .distinct()
                .collect(Collectors.toList());

        if (filesToDelete.isEmpty()) {
            logger.info("No se extrajeron rutas de archivo válidas de eventos finalizados para eliminar.");
            return;
        }

        try {
            logger.info("Eliminando {} archivos de Supabase: {}", filesToDelete.size(), filesToDelete);
            supabaseStorageService.deleteFiles(filesToDelete);
            logger.info("Archivos eliminados de Supabase exitosamente.");

            // Opcional pero recomendado: Limpiar las rutas en la base de datos
            finishedEvents.forEach(evento -> {
                evento.setItinerario(null);
                evento.setListaParticipantes(null);
            });
            eventoRepository.saveAll(finishedEvents);
            logger.info("Rutas de archivos limpiadas en la base de datos para {} eventos.", finishedEvents.size());

        } catch (Exception e) {
            logger.error("Error durante la tarea de limpieza de archivos de eventos finalizados.", e);
        }
    }

    private void cleanupOrphanFiles(List<String> foldersToCheck) {
        logger.info("Iniciando tarea de limpieza de archivos huérfanos en carpetas: {}", foldersToCheck);

        // 1. Obtener todas las rutas de archivo que están en uso en la base de datos.
        Set<String> usedFilePaths = eventoRepository.findAllUsedFilePaths().stream()
                .map(this::extractPathFromUrl)
                .collect(Collectors.toSet());
        logger.info("Se encontraron {} rutas de archivo en uso en la base de datos.", usedFilePaths.size());

        List<String> orphanFiles = new ArrayList<>();
        for (String folder : foldersToCheck) {
            try {
                // 2. Listar todos los archivos en la carpeta actual de Supabase.
                List<String> filesInBucket = supabaseStorageService.listFiles(folder);
                logger.info("Se encontraron {} archivos en la carpeta '{}' de Supabase.", filesInBucket.size(), folder);

                // 3. Identificar los archivos huérfanos (en Supabase pero no en la BD).
                filesInBucket.stream()
                        .filter(filePath -> !usedFilePaths.contains(filePath))
                        .forEach(orphanFiles::add);

            } catch (Exception e) {
                logger.error("No se pudo listar o procesar archivos de la carpeta '{}'.", folder, e);
            }
        }

        if (!orphanFiles.isEmpty()) {
            logger.info("Se encontraron {} archivos huérfanos para eliminar: {}", orphanFiles.size(), orphanFiles);
            supabaseStorageService.deleteFiles(orphanFiles);
            logger.info("Archivos huérfanos eliminados exitosamente.");
        } else {
            logger.info("No se encontraron archivos huérfanos para eliminar.");
        }
    }

    /**
     * Extrae la ruta del archivo (ej: 'pdfs/nombre_archivo.pdf') de una URL pública de Supabase.
     */
    private String extractPathFromUrl(String url) {
        String publicUrlPart = "/storage/v1/object/public/";
        int startIndex = url.indexOf(publicUrlPart);
        if (startIndex != -1) {
            // Sumamos la longitud de la parte pública y el nombre del bucket + /
            // Asumimos que la URL no contiene el nombre del bucket, ya que el servicio lo añade.
            // Si la URL completa está en la BD, necesitaríamos el nombre del bucket.
            // Por ahora, asumimos que la ruta guardada es relativa al bucket.
            // Si la URL es del tipo .../public/bucket/folder/file.pdf
            String pathWithBucket = url.substring(startIndex + publicUrlPart.length());
            // Quitamos el nombre del bucket del inicio
            return pathWithBucket.substring(pathWithBucket.indexOf('/') + 1);
        }
        // Si la URL no es una URL pública de Supabase, devolvemos la cadena original
        // asumiendo que ya es la ruta del archivo.
        return url;
    }
}