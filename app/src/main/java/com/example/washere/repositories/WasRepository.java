package com.example.washere.repositories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.helpers.FirebaseFireStoreHelper;
import com.example.washere.helpers.FirebseStorageHelper;
import com.example.washere.models.Was;
import com.here.android.mpa.common.GeoCoordinate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WasRepository extends AppCompatActivity {

    private static WasRepository instance;
    private ArrayList<Was> dataSet = new ArrayList<>();
    private GeoCoordinate currentLocation;
    private FirebseStorageHelper firebseStorageHelper = new FirebseStorageHelper();
    private FirebaseFireStoreHelper firebaseFireStoreHelper = new FirebaseFireStoreHelper();
    private boolean isUpdated = false;
    private MutableLiveData<String> downloadUrl = new MutableLiveData<>();
    private MutableLiveData<List<Was>> wasList=new MutableLiveData<>();


    private String userName = ("Ocul");


    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    public FirebseStorageHelper getFirebseStorageHelper() {
        return firebseStorageHelper;
    }

    public GeoCoordinate getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoCoordinate currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }


    public void uploadFilesToFirebaseStorage(File file) {
        firebseStorageHelper.uploadFilesToStorage(file);
    }

    public String getDownloadUri() {
        String downloadUri;

        downloadUri = firebseStorageHelper.getDownloadUri().getValue();

        return downloadUri;
    }

    public void addWasHashMapToFireStore(Was was) {
        firebaseFireStoreHelper.addWasMapToFireStore(was);
    }

    public FirebaseFireStoreHelper getFirebaseFireStoreHelper() {
        return firebaseFireStoreHelper;
    }

    public void setFirebaseFireStoreHelper(FirebaseFireStoreHelper firebaseFireStoreHelper) {
        this.firebaseFireStoreHelper = firebaseFireStoreHelper;
    }

    //Misc
    //Mock UserName
    public String getUserName() {
        return userName;
    }

    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timeStamp = simpleDateFormat.format(new Date());

        return timeStamp;
    }

    public MutableLiveData<String> getDownloadUrl() {
        MutableLiveData<String> downloadUrl = new MutableLiveData<>();
        return downloadUrl;
    }

    public MutableLiveData<List<Was>> getWasList() {
        return wasList;
    }

    public void setWasList(MutableLiveData<List<Was>> wasList) {
        this.wasList = wasList;
    }
}
