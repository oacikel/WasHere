package ocul.longestlovestoryever.washere.models;

import com.here.android.mpa.mapping.MapMarker;

import java.io.File;


public class Was {
    private String fileName;
    private double locationLatitude;
    private double locationLongitude;
    private double locationAltitude;
    private int markerDirectory;
    private MapMarker mapMarker;
    private String downloadUrl;
    private File audioFile;
    private String uploaderName;
    private String uploadTime;
    private String uploadDate;
    private String uploadTitle;
    private String uniqueId;

    public Was() {
    }

    public Was(double locationLatitude, double locationLongitude, double locationAltitude, String uploadTitle, String uploaderName, String uploadTime, String uploadDate, File audioFile) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAltitude = locationAltitude;
        this.uploaderName = uploaderName;
        this.uploadTime = uploadTime;
        this.uploadDate = uploadDate;
        this.uploadTitle = uploadTitle;
        this.audioFile=audioFile;
    }

    public Was(double locationLatitude, double locationLongitude, double locationAltitude, String uploadTitle, String downloadUrl, String uploaderName, String uploadTime, String uploadDate) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAltitude = locationAltitude;
        this.downloadUrl = downloadUrl;
        this.uploaderName = uploaderName;
        this.uploadTime = uploadTime;
        this.uploadDate = uploadDate;
        this.uploadTitle = uploadTitle;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public MapMarker getMapMarker() {
        return mapMarker;
    }

    public void setMapMarker(MapMarker mapMarker) {
        this.mapMarker = mapMarker;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
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

    public String getUploadTitle() {
        return uploadTitle;
    }

    public void setUploadTitle(String uploadTitle) {
        this.uploadTitle = uploadTitle;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

}