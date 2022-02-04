package com.example.parsaBadiei;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    private String location;

    public void setLocation(String location){this.location=location;}
    public String getLocation(){return this.location;}
}
