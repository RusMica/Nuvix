package com.covielloDevs.SistemaDeVerificacion.utils.dataReader;

import java.util.List;
import java.util.Map;

public interface IDataReader {
    List<Map<String, Object>> read(String filePath) throws Exception;

    List<Map<String, Object>> read(byte[] fileBytes, String filePath) throws Exception;

}
