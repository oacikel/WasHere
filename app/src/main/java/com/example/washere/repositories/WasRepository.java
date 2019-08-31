package com.example.washere.repositories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.helpers.FirebaseFireStoreHelper;
import com.example.washere.helpers.FirebseStorageHelper;
import com.example.washere.models.Was;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WasRepository extends AppCompatActivity {

    private static WasRepository instance;
    private FirebseStorageHelper firebseStorageHelper = new FirebseStorageHelper();
    private FirebaseFireStoreHelper firebaseFireStoreHelper = new FirebaseFireStoreHelper();
    private MutableLiveData<List<Was>> wasList = new MutableLiveData<>();
    private MutableLiveData<GeoCoordinate> currentLocation=new MutableLiveData<>();
    private ClusterLayer existingClusterLayer;
    private MutableLiveData <ClusterViewObject> clusterViewObject =new MutableLiveData<>();
    private MutableLiveData<ArrayList<Was>> selectedClusterViewWasList=new MutableLiveData<>();


    private String userName = ("Ocul");

    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    //Regarding Was Object Upload / Download
    public FirebseStorageHelper getFirebseStorageHelper() {
        return firebseStorageHelper;
    }

    public FirebaseFireStoreHelper getFirebaseFireStoreHelper() {
        return firebaseFireStoreHelper;
    }

    public void uploadFilesToFirebaseStorage(File file) {
        firebseStorageHelper.uploadFilesToStorage(file);
    }

    public void addWasHashMapToFireStore(Was was) {
        firebaseFireStoreHelper.addWasMapToFireStore(was);
    }

    //Regarding Map Management
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

    public void continouslyUpdateWasObjects(){
        firebaseFireStoreHelper.updateWasObjects();
    }

    public MutableLiveData<ClusterViewObject> getClusterViewObject() {
        return clusterViewObject;
    }


    //Regarding User
    public String getUserName() {
        return userName;
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

    public String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String time = simpleDateFormat.format(new Date());

        return time;
    }


}
