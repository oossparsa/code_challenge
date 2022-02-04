package com.example.parsaBadiei;


public class FileResponse {

    private String fileName;
    private String fileUrl;
    private String message;

    public FileResponse(String fileName, String fileUrl, String message){
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.message = message;
    }

    public String getFileName(){return fileName;}
    public String getFileUrl(){return fileUrl;}
    public String getMessage(){return message;}
}
