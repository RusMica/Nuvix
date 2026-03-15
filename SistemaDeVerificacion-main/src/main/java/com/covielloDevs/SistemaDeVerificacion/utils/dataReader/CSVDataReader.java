package com.covielloDevs.SistemaDeVerificacion.utils.dataReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataReader implements IDataReader{

    @Override
    public List<Map<String, Object>> read(String filePath) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();

        try(FileReader reader = new FileReader(filePath)){
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                                                            .withAllowMissingColumnNames()
                                                            .parse(reader);
            records.forEach(record -> {
                Map<String, Object> row = new HashMap<>();
                record.toMap().keySet().
                        forEach(header -> row.put(header, record.get(header)));
                data.add(row);
            });

        }
        return data;
    }

    public List<Map<String, Object>> read(byte[] fileBytes, String fileName) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        try (java.io.InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes);
             java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .withAllowMissingColumnNames()
                    .parse(reader);
            records.forEach(record -> {
                Map<String, Object> row = new HashMap<>();
                boolean hasValue = false;
                for (String header : record.toMap().keySet()) {
                    String value = record.get(header);
                    if (value != null && !value.trim().isEmpty()) {
                        hasValue = true;
                        row.put(header, value.trim());
                    } else row.put(header, null);
                }
                if (hasValue) data.add(row);
            });
        }
        return data;
    }
}
