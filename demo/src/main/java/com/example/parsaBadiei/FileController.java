package com.example.parsaBadiei;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    //private final StorageService;
    // do we need the constructor for that has storageservice as input ?

    @Autowired
    StorageService FileStorageService;
    @Autowired
    FileUploadProperties UploadedFilesProperties;

    @GetMapping("")
    public String[] listDirectory(){
        return new File(UploadedFilesProperties.getLocation()).list();
    }

    @PostMapping("/upload")
    public ResponseEntity<List<FileResponse>> uploadFiles(@RequestParam("file") MultipartFile[] files){
        List<FileResponse> responses = Arrays.asList(files).stream().map(file -> {
          String uploadedFileName = FileStorageService.saveFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/download/")
                    .path(uploadedFileName)
                    .toUriString();
            return new FileResponse(uploadedFileName, fileDownloadUri, "Successfully Uploaded!");
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

//    @PostMapping("/upload")
//    //@RequestMapping(value = "/upload")
//    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file){
//        String uploadedFile = FileStorageService.saveFile(file);
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/file/download/")
//                .path(uploadedFile)
//                .toUriString();
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new FileResponse(uploadedFile,fileDownloadUri,"File uploaded successfully!"));
//    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable() String fileName){
        Resource resource = FileStorageService.loadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
