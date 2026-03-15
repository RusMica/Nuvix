package com.covielloDevs.SistemaDeVerificacion.utils.saveFiles;

import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.RemoteException;

@Component
public class SaveSpreadSheet implements ISaveFiles{

    private final SupabaseStorageService storage;

    public SaveSpreadSheet(SupabaseStorageService storage) {
        this.storage = storage;
    }

    @Override
    public String save(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename();
        if(originalName == null) throw new RuntimeException("Archivo sin nombre");

        if(!(originalName.endsWith(".csv") || originalName.endsWith(".xls") || originalName.endsWith(".xlsx")))
            throw new RemoteException("Archivo no valido, solo se permite CSV o excel");

        return storage.upload(file, "spreadsheets");
    }
}
