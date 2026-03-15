package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.utils.saveFiles.SavePDF;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFService {

    private final SavePDF savePDF;

    public PDFService(SavePDF savePDF) {
        this.savePDF = savePDF;
    }

    public String save(MultipartFile file) throws Exception {
        return savePDF.save(file);
    }
}
