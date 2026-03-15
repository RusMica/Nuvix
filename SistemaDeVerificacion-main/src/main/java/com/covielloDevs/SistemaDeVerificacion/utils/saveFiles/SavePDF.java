package com.covielloDevs.SistemaDeVerificacion.utils.saveFiles;

import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class SavePDF implements ISaveFiles{

    private final SupabaseStorageService storage;

    public SavePDF(SupabaseStorageService storage) {
        this.storage = storage;
    }

    @Override
    public String save(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename();

        if(originalName == null) throw new RuntimeException("Archivo sin nombre");

        if(!originalName.toLowerCase().endsWith(".pdf"))
            throw new RuntimeException("Archivo no valido, solo se permite PDF");

        return storage.upload(file, "pdfs");
    }
}
