package com.example.qcollect.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {

    String save(MultipartFile file) throws Exception;

    void delete(String path) throws Exception;

    Resource load(String path) throws Exception;

}