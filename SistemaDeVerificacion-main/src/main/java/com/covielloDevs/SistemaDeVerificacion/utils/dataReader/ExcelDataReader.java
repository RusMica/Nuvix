package com.covielloDevs.SistemaDeVerificacion.utils.dataReader;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelDataReader implements IDataReader {
    @Override
    public List<Map<String, Object>> read(String filePath) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 0) { // Check if sheet is empty
                return data;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) { // Check if header row exists
                return data;
            }

            List<String> headers = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();

            for (Cell cell : headerRow) {
                headers.add(dataFormatter.formatCellValue(cell).trim());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, Object> map = new HashMap<>();
                boolean isRowEmpty = true;
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (!cellValue.isEmpty()) isRowEmpty = false;
                    map.put(headers.get(j), cellValue.isEmpty() ? null : cellValue);
                }
                if (!isRowEmpty) data.add(map);
            }
        }
        return data;
    }

    public List<Map<String, Object>> read(byte[] fileBytes, String fileName) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        try (java.io.InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getLastRowNum() < 0) return data;

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return data;

            List<String> headers = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            for (Cell cell : headerRow) headers.add(dataFormatter.formatCellValue(cell).trim());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, Object> rowData = new HashMap<>();
                boolean hasValue = false;
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String value = cell != null ? dataFormatter.formatCellValue(cell).trim() : null;
                    if (value != null && !value.isEmpty()) {
                        hasValue = true;
                        rowData.put(headers.get(j), value);
                    } else rowData.put(headers.get(j), null);
                }
                if (hasValue) data.add(rowData);
            }
        }
        return data;
    }
}
