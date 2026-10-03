package com.example.qcollect.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String save(MultipartFile file)
            throws Exception {

        Path path = Paths.get(uploadDir);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String fileName =
                UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destination =
                path.resolve(fileName);

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        return destination.toString();
    }
    public void delete(String filePath) throws Exception {

        Path path = Paths.get(filePath);

        Files.deleteIfExists(path);

    }
    public Resource load(String filePath) throws Exception {

        Path path = Paths.get(filePath);

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {

            throw new RuntimeException("File not found");

        }

        return resource;
    }
}