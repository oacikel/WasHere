package com.example.washere.models;

import java.io.File;
import java.net.URI;

public class WasAudioFile extends File {
    private String uploadTime;
    private String uploadDate;
    private String uploaderName;


    public WasAudioFile(String pathname) {
        super(pathname);
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }
}
