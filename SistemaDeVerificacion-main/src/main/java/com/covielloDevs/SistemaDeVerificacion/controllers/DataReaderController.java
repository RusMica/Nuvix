package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.services.DataReaderService;
import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v1/data")
public class DataReaderController {

    private final DataReaderService dataReaderService;
    private final SupabaseStorageService storageService;
    public DataReaderController(DataReaderService dataReaderService, SupabaseStorageService storageService) {
        this.dataReaderService = dataReaderService;
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<String> getData(@RequestParam("filePath") String filePath) throws Exception {
        // Descargar archivo desde Supabase
        byte[] fileBytes = storageService.download(filePath);

        // Leer datos desde los bytes
        String data = dataReaderService.readDataFromBytes(fileBytes, filePath);
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<String> saveFile(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(dataReaderService.saveFile(file));
    }

    @GetMapping("/signed-url")
    public String signedUrl(@RequestParam("path") String path,
                            @RequestParam(defaultValue = "60") int expiresInSeconds) {
        return storageService.getSignedUrl(path, expiresInSeconds);
    }

}
