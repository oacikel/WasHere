package com.example.washere.models;
public class Was {
    private String fileLocation;
    private String fileName;
    private double locationLatitude;
    private double locationLongitude;
    private double locationAltitude;
    private int markerDirectory;

    public Was(String fileLocation, String fileName, double locationLatitude, double locationLongitude, double locationAltitude, int markerDirectory) {
        this.fileLocation = fileLocation;
        this.fileName = fileName;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAltitude = locationAltitude;
        this.markerDirectory = markerDirectory;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public int getMarkerDirectory() {
        return markerDirectory;
    }

    public void setMarkerDirectory(int markerDirectory) {
        this.markerDirectory = markerDirectory;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getLocationAltitude() {
        return locationAltitude;
    }

    public void setLocationAltitude(double locationAltitude) {
        this.locationAltitude = locationAltitude;
    }
}
