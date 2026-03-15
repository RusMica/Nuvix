package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.utils.dataReader.DataReaderFactory;
import com.covielloDevs.SistemaDeVerificacion.utils.saveFiles.SaveSpreadSheet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class DataReaderService {

    private final SaveSpreadSheet saveSpreadSheet;

    public DataReaderService(SaveSpreadSheet saveSpreadSheet) {
        this.saveSpreadSheet = saveSpreadSheet;
    }

    public String readDataFromBytes(byte[] fileBytes, String fileName) throws Exception {
        return DataReaderFactory.getReader(fileBytes, fileName);
    }

    public List<Participante> formatList(String data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, mapper.getTypeFactory().constructCollectionType(List.class, Participante.class));
    }

    public String saveFile(MultipartFile file) throws Exception {

        return saveSpreadSheet.save(file);
    }
}