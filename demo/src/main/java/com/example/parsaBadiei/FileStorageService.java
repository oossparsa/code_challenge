package com.example.parsaBadiei;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService implements StorageService {
    private final Path directory;

    @Autowired
    public FileStorageService(FileUploadProperties fileUploadProperties) {
        directory = Paths.get(fileUploadProperties.getLocation()).toAbsolutePath().normalize();
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        /// save file uploaded by the user
        String fileName = file.getOriginalFilename();
        Path dirFile = directory.resolve(fileName);
        // copying the file uploaded by user onto the system storage
        try {
            Files.copy(file.getInputStream(), dirFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public Resource loadFile(String fileName) {
        /// return the requested file to the user
        Path file = directory.resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            else throw new FileNotFoundException("Requested file doesn't exist!");

        } catch (MalformedURLException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
