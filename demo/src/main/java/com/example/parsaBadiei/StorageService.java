package com.example.parsaBadiei;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();
    String saveFile(MultipartFile file);
    Resource loadFile(String fileName);
}
