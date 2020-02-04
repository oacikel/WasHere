package ocul.longestlovestoryever.washere.repositories;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.models.Was;
import ocul.longestlovestoryever.washere.models.eDownloadingState;
import ocul.longestlovestoryever.washere.models.eFollowState;
import ocul.longestlovestoryever.washere.models.eLocationStatus;
import ocul.longestlovestoryever.washere.models.ePermissionStatus;
import ocul.longestlovestoryever.washere.models.ePrivacyStatus;
import ocul.longestlovestoryever.washere.models.eUploadingState;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.models.eViewMode;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WasRepository extends AppCompatActivity {

    private boolean wasListenerInvokedOnce;
    private static WasRepository instance;
    private static MutableLiveData<eRecordState> wasRecordingState = new MutableLiveData<>();
    private MutableLiveData<eUploadingState> uploadingState = new MutableLiveData<>();
    private MutableLiveData<eDownloadingState> downloadingState = new MutableLiveData<>();
    private MutableLiveData<eLocationStatus> locationStatus = new MutableLiveData<>();
    private MutableLiveData<eFollowState> followState = new MutableLiveData<>();
    private eViewMode viewMode;
    private ArrayList<Was> allWasList;
    private ArrayList<Was> myWasList;
    private MutableLiveData<GeoCoordinate> currentLocation = new MutableLiveData<>();
    private ClusterLayer existingClusterLayer;
    private MutableLiveData<ClusterViewObject> clusterViewObject = new MutableLiveData<>();
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
    private ArrayList<Was> selectedWasList;
    private String downloadUrl;
    private Map map;
    private ClusterLayer allClusterLayer, myClusterLayer;
    private static long MAX_WAS_LENGTH = 35000;
    private CollectionReference wasItemsReference;
    private SupportMapFragment supportMapFragment;
    private EventListener<QuerySnapshot> wasChangeListener;
    private CollectionReference wasCollectionReference;
    private GeoCoordinate lastKnownLocation;
    private MutableLiveData<ePermissionStatus> permissionStatus = new MutableLiveData<>();
    private ePrivacyStatus privacyStatus;

    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    //States
    //Recording/Playing State
    public void setWasRecordingState(eRecordState state) {
        wasRecordingState.setValue(state);
    }

    public MutableLiveData<eRecordState> getWasRecordingState() {
        return wasRecordingState;
    }

    //Regarding Location Status
    /*
    1-Location Available
    2-Location Not Available
     */
    public MutableLiveData<eLocationStatus> getLocationStatus() {
        return locationStatus;
    }

    public void updateLocationStatus(eLocationStatus status) {
        locationStatus.setValue(status);
    }

    public void setUpdateLocationStatus(eLocationStatus status){
        locationStatus.setValue(status);
    }

    //Regarding Was Object Upload / Download


    public MutableLiveData<eUploadingState> getUploadingState() {
        return uploadingState;
    }

    public void setUploadingState(MutableLiveData<eUploadingState> uploadingState) {
        this.uploadingState = uploadingState;
    }

    public void setUpdateUploadingState(eUploadingState eUploadingState) {
        uploadingState.setValue(eUploadingState);
    }

    public void postUpdateUploadingState(eUploadingState eUploadingState) {
        uploadingState.postValue(eUploadingState);
    }

    //Downloading State


    public MutableLiveData<eDownloadingState> getDownloadingState() {
        return downloadingState;
    }

    public void setDownloadingState(MutableLiveData<eDownloadingState> downloadingState) {
        this.downloadingState = downloadingState;
    }

    public eViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(eViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public void setUpdateDownloadingState(eDownloadingState state) {
        downloadingState.setValue(state);
    }

    public void postUpdateDownloadingState(eDownloadingState state) {
        downloadingState.postValue(state);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public EventListener<QuerySnapshot> getWasChangeListener() {
        return wasChangeListener;
    }

    public void setWasChangeListener(EventListener<QuerySnapshot> wasChangeListener) {
        this.wasChangeListener = wasChangeListener;
    }

    public CollectionReference getWasCollectionReference() {
        return wasCollectionReference;
    }

    public void setWasCollectionReference(CollectionReference wasCollectionReference) {
        this.wasCollectionReference = wasCollectionReference;
    }

    //Regarding Map Management


    public MutableLiveData<eFollowState> getFollowState() {
        return followState;
    }

    public void setUpdateFollowState(eFollowState state) {
        followState.setValue(state);
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public SupportMapFragment getSupportMapFragment() {
        return supportMapFragment;
    }

    public void setSupportMapFragment(SupportMapFragment supportMapFragment) {
        this.supportMapFragment = supportMapFragment;
    }

    public MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return currentLocation;
    }

    public void setUpdateCurrentLocation(GeoCoordinate currentLocation){
        this.currentLocation.setValue(currentLocation);
    }

    public ClusterLayer getExistingClusterLayer() {
        return existingClusterLayer;
    }

    public void setExistingClusterLayer(ClusterLayer existingClusterLayer) {
        this.existingClusterLayer = existingClusterLayer;
    }

    //Regarding Was Objects

    //Global Was List
    public ArrayList<Was> getAllWasList() {
        return allWasList;
    }

    public void setAllWasList(ArrayList<Was> allWasList) {
        this.allWasList = allWasList;
    }

    //My Was List


    public ArrayList<Was> getMyWasList() {
        return myWasList;
    }

    public void setMyWasList(ArrayList<Was> myWasList) {
        this.myWasList = myWasList;
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

    public ClusterLayer getAllClusterLayer() {
        return allClusterLayer;
    }

    public void setAllClusterLayer(ClusterLayer allClusterLayer) {
        this.allClusterLayer = allClusterLayer;
    }

    public ClusterLayer getMyClusterLayer() {
        return myClusterLayer;
    }

    public void setMyClusterLayer(ClusterLayer myClusterLayer) {
        this.myClusterLayer = myClusterLayer;
    }

    public ArrayList<Was> getSelectedWasList() {
        return selectedWasList;
    }

    public void setSelectedWasList(ArrayList<Was> selectedWasList) {
        this.selectedWasList = selectedWasList;
    }

    public void clearWasItems() {
        allWasList = null;
        allClusterLayer = null;
    }

    public CollectionReference getWasItemsReference() {
        return wasItemsReference;
    }

    public void setWasItemsReference(CollectionReference wasItemsReference) {
        this.wasItemsReference = wasItemsReference;
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

    public boolean isWasListenerInvokedOnce() {
        return wasListenerInvokedOnce;
    }

    public void setWasListenerInvokedOnce(boolean wasListenerInvokedOnce) {
        this.wasListenerInvokedOnce = wasListenerInvokedOnce;
    }

    public GeoCoordinate getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(GeoCoordinate lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public MutableLiveData<ePermissionStatus> getPermissionStatus() {
        return permissionStatus;
    }

    public void setUpdatePermissionStatus(ePermissionStatus permissionStatus) {
        this.permissionStatus.setValue(permissionStatus);
    }

    public ePrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(ePrivacyStatus privacyStatus) {
        this.privacyStatus = privacyStatus;

    }
}
