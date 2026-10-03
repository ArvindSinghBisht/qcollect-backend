package com.example.qcollect.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PhotoStorageServiceImpl
        implements PhotoStorageService {

    private final FileStorageService fileStorageService;

    @Override
    public String save(MultipartFile file) throws Exception {
        return fileStorageService.save(file);
    }

    @Override
    public void delete(String path) throws Exception {
        fileStorageService.delete(path);
    }

    @Override
    public Resource load(String path) throws Exception {

        return fileStorageService.load(path);

    }
}