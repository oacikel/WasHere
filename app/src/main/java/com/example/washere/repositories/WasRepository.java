package com.example.washere.repositories;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.Was;
import com.example.washere.models.eUploadingState;
import com.example.washere.models.eWasUploadState;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WasRepository extends AppCompatActivity {


    private static WasRepository instance;
    private static MutableLiveData<eWasUploadState> wasRecordingState = new MutableLiveData<>();
    private MutableLiveData<eUploadingState>uploadingState=new MutableLiveData<>();
    private MutableLiveData<Integer> locationStatus = new MutableLiveData<>();
    private MutableLiveData<List<Was>> wasList = new MutableLiveData<>();
    private MutableLiveData<GeoCoordinate> currentLocation = new MutableLiveData<>();
    private ClusterLayer existingClusterLayer;
    private MutableLiveData<ClusterViewObject> clusterViewObject = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Was>> selectedClusterViewWasList = new MutableLiveData<>();
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private GeoCoordinate uploadLocation;
    private String uploadFilePath;
    private String uploadDate;
    private String uploadTime;
    private File uploadFile;
    private Was wasToUpload;
    private String uploadTitle;
    private MapMarker markerSelected;
    private String downloadUrl;
    private Map map;
    private ClusterLayer clusterLayer;
    private static long MAX_WAS_LENGTH = 35000;

    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    //States
    //Recording/Playing State
    public void setWasRecordingState(eWasUploadState state) {
        wasRecordingState.setValue(state);
    }

    public MutableLiveData<eWasUploadState> getWasRecordingState() {
        return wasRecordingState;
    }

    //Regarding Location Status
    /*
    1-Location Available
    2-Location Not Available
     */
    public MutableLiveData<Integer> getLocationStatus() {
        return locationStatus;
    }

    public void updateLocationStatus(int status) {
        locationStatus.setValue(status);
    }

    //Regarding Was Object Upload / Download


    public MutableLiveData<eUploadingState> getUploadingState() {
        return uploadingState;
    }

    public void setUploadingState(MutableLiveData<eUploadingState> uploadingState) {
        this.uploadingState = uploadingState;
    }

    public void setUpdateUploadingState(eUploadingState eUploadingState){
        uploadingState.setValue(eUploadingState);
    }

    public void postUpdateUploadingState(eUploadingState eUploadingState){
        uploadingState.postValue(eUploadingState);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    //Regarding Map Management


    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return currentLocation;
    }

    public ClusterLayer getExistingClusterLayer() {
        return existingClusterLayer;
    }

    public void setExistingClusterLayer(ClusterLayer existingClusterLayer) {
        this.existingClusterLayer = existingClusterLayer;
    }

    public MutableLiveData<ArrayList<Was>> getSelectedClusterViewWasList() {
        return selectedClusterViewWasList;
    }

    //Regarding Was Objects
    public MutableLiveData<List<Was>> getWasList() {
        return wasList;
    }

    public MutableLiveData<ClusterViewObject> getClusterViewObject() {
        return clusterViewObject;
    }

    public static long getMAX_WAS_LENGTH() {
        return MAX_WAS_LENGTH;
    }

    public Was getWasToUpload() {
        return wasToUpload;
    }

    public void setWasToUpload(Was wasToUpload) {
        this.wasToUpload = wasToUpload;
    }

    public File getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public MapMarker getMarkerSelected() {
        return markerSelected;
    }

    public void setMarkerSelected(MapMarker markerSelected) {
        this.markerSelected = markerSelected;
    }

    public ClusterLayer getClusterLayer() {
        return clusterLayer;
    }

    public void setClusterLayer(ClusterLayer clusterLayer) {
        this.clusterLayer = clusterLayer;
    }

    //Uploading Was Objects:
    //Regarding Recording

    public MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }

    public void setMediaRecorder(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public GeoCoordinate getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(GeoCoordinate uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadTitle() {
        return uploadTitle;
    }

    public void setUploadTitle(String uploadTitle) {
        this.uploadTitle = uploadTitle;
    }

    //Misc
    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timeStamp = simpleDateFormat.format(new Date());

        return timeStamp;
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(new Date());

        return date;
    }

    public String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String time = simpleDateFormat.format(new Date());

        return time;
    }


}
