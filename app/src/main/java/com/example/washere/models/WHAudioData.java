package com.example.washere.models;

import com.here.android.mpa.common.GeoCoordinate;

public class WHAudioData {
   private String path;
   private String fileName;
   private GeoCoordinate location;

    public WHAudioData(String path, String fileName, GeoCoordinate location) {
        this.path = path;
        this.fileName = fileName;
        this.location = location;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GeoCoordinate getLocation() {
        return location;
    }

    public void setLocation(GeoCoordinate location) {
        this.location = location;
    }
}
