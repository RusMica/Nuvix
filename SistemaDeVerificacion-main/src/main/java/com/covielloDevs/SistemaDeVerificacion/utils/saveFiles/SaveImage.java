package com.covielloDevs.SistemaDeVerificacion.utils.saveFiles;

import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class SaveImage implements ISaveFiles{

    private final SupabaseStorageService storage;

    public SaveImage(SupabaseStorageService storage) {
        this.storage = storage;
    }

    @Override
    public String save(MultipartFile image) throws Exception {
        String contentType = image.getContentType();
        if(contentType == null || !contentType.startsWith("image/"))
            throw new RuntimeException("Imagen no valida");

        return storage.upload(image, "images");
    }
}
