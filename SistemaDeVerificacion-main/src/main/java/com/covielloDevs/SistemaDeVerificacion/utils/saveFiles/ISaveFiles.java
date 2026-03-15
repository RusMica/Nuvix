package com.covielloDevs.SistemaDeVerificacion.utils.saveFiles;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public interface ISaveFiles {

    String save(MultipartFile file) throws Exception;
}
