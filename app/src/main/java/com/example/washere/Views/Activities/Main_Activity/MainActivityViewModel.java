package com.example.washere.Views.Activities.Main_Activity;

/*
This class will serve to manipulate audio files.
    Methods:
        RecordAudio - records the audio input in realtime from device.
        Play Audio - plays a selected audio file from user.
        ...
*/


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.R;
import com.example.washere.Views.Dialogs.Record_WAS_Dialog.RecordWasDialog;
import com.example.washere.helpers.PlayAudioHelper;
import com.example.washere.models.Was;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private String intentName;
    private Context context;
    private MutableLiveData<GeoCoordinate> currentLocation = WasRepository.getInstance().getCurrentLocation();
    private PositioningManager positioningManager;
    private MutableLiveData<List<Was>> wasList;
    private boolean isUpdating = false;
    private List<MapObject> markerList = new ArrayList<>();
    private PlayAudioHelper playAudioHelper;
    private WasRepository wasRepository;
    private ClusterLayer clusterLayer;
    private Application mainApplication;

    public void setUpdating(boolean updating) {
        isUpdating = updating;
    }

    private MutableLiveData<PositioningManager.LocationStatus> locationStatus = new MutableLiveData<>();
    private MutableLiveData<ClusterViewObject> clusterViewObject = new MutableLiveData<>();

    public boolean isUpdating() {
        return isUpdating;
    }

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        mainApplication = application;
    }

    //Location Status
    public MutableLiveData<Integer> getLocationStatus() {
        return WasRepository.getInstance().getLocationStatus();
    }

    public void updateLocationStatus(int status) {
        WasRepository.getInstance().getLocationStatus().setValue(status);
    }

    //Was Recording / Playing State
    public MutableLiveData<eWasUploadState> getWasRecordingState() {
        return WasRepository.getInstance().getWasRecordingState();
    }

    public void updateWasRecordingState(eWasUploadState state) {
        WasRepository.getInstance().setWasRecordingState(state);
    }

    //Was Recording Playing
    public void initWasUploadDialog (FragmentTransaction fragmentTransaction,Fragment previousFragment){
        RecordWasDialog recordWasDialog;

        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        recordWasDialog = new RecordWasDialog();
        recordWasDialog.show(fragmentTransaction, "WAS_DIALOG");
    }

    //Map Management Part Start
    public boolean isSuccess() {

        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(context.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        return com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
    }

    public void onMapEngineInitialized() {
        positioningManager = PositioningManager.getInstance();
        clusterLayer = new ClusterLayer();
        PositioningManager.OnPositionChangedListener positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                currentLocation.setValue(geoPosition.getCoordinate());
                Log.w("OCUL - Position Updated", "The position is updated");
            }

            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
                if (locationStatus == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    Log.w("OCUL - Position Fix", "Location Status is OUT OF SERVICE");
                    updateLocationStatus(2);
                } else if (locationStatus == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    Log.w("OCUL - Position Fix", "Location Status is TEMPORARILY UNAVAILABLE");
                    updateLocationStatus(2);
                } else if (locationStatus == PositioningManager.LocationStatus.AVAILABLE) {
                    Log.w("OCUL - Position Fix", "Location Status is AVAILABLE");
                    updateLocationStatus(1);
                }
                currentLocation.setValue(PositioningManager.getInstance().getPosition().getCoordinate());
            }
        };
        positioningManager.addListener(new WeakReference<>(positionListener));
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);

        while (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
            Log.w("OCUL - While Loop", "Location Status is TEMPORARILY UNAVAILABLE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        }

        if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
            Log.w("OCUL - Positioning", "Location Status is OUT OF SERVICE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        } else if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.AVAILABLE) {
            Log.w("OCUL - Positioning", "Location Status is AVAILABLE.");
            currentLocation.setValue(PositioningManager.getInstance().getPosition().getCoordinate());
            updateLocationStatus(1);
        }
    }

    public MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return WasRepository.getInstance().getCurrentLocation();
    }


    public void init() {
        wasRepository = WasRepository.getInstance();
        wasRepository.continouslyUpdateWasObjects();
        playAudioHelper = new PlayAudioHelper();
        getClusterViewObject();
    }

    //Updates the Markerlist and adds Map Markers to Was Objects:
    public void updateMarkerList() {
        Log.d("OCUL - Marker Cluster", "Creating Cluster layer");
        if (markerList.size() != 0) {
            markerList.clear();
        }
        clusterLayer.removeMarkers(clusterLayer.getMarkers());
        for (int i = 0; i < wasRepository.getWasList().getValue().size(); i++) {
            Was was = wasList.getValue().get(i);
            MapMarker marker = new MapMarker();
            Image image = new Image();
            try {
                image.setImageResource(R.drawable.place_holder_icon);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
            GeoCoordinate coordinate = new GeoCoordinate(was.getLocationLatitude(), was.getLocationLongitude(), was.getLocationAltitude());
            marker.setCoordinate(coordinate);
            marker.setIcon(image);
            marker.setTitle(String.valueOf(i));
            markerList.add(marker);
            was.setMapMarker(marker);
            clusterLayer.addMarker(marker);
            setExistingClusterLayer(clusterLayer);
        }
        System.out.println("OCUL - Updated cluster layer size is: " + clusterLayer.getMarkers().size());
        System.out.println("OCUL - ////////////////////");
        setMarkerList(markerList);
    }

    public void setMarkerList(List<MapObject> markerList) {
        this.markerList = markerList;
    }

    public MutableLiveData<List<Was>> getWasList() {
        wasList = WasRepository.getInstance().getWasList();
        return wasList;
    }

    public ClusterLayer getExistingClusterLayer() {
        return WasRepository.getInstance().getExistingClusterLayer();
    }

    public void setExistingClusterLayer(ClusterLayer clusterLayer) {
        WasRepository.getInstance().setExistingClusterLayer(clusterLayer);
    }

    public MutableLiveData<ClusterViewObject> getClusterViewObject() {
        return WasRepository.getInstance().getClusterViewObject();
    }

    public void setWasObjectsInCluster() {
        Collection<MapMarker> markerList = getClusterViewObject().getValue().getMarkers();
        String adressToBeSearched;
        String adress;
        ArrayList<MapMarker> mapMarkerArrayList = new ArrayList<>(markerList);
        ArrayList<Was> wasListOfCluster = new ArrayList<>();
        for (int i = 0; i < mapMarkerArrayList.size(); i++) {
            adressToBeSearched = mapMarkerArrayList.get(i).toString();
            for (int a = 0; a < wasList.getValue().size(); a++) {
                adress = wasList.getValue().get(a).getMapMarker().toString();
                if (adress.equals(adressToBeSearched)) {
                    wasListOfCluster.add(wasList.getValue().get(a));
                }
            }
        }
        WasRepository.getInstance().getSelectedClusterViewWasList().setValue(wasListOfCluster);
    }

    public MutableLiveData<ArrayList<Was>> getSelectedClusterViewWasList() {
        return WasRepository.getInstance().getSelectedClusterViewWasList();
    }

    //Audio Management Part Start
    public void playAudio(List<Was> wasList, MapMarker marker) {
        String addressToBeSearched = marker.toString();
        String address;
        for (int i = 0; i < wasList.size(); i++) {
            address = wasList.get(i).getMapMarker().toString();
            if (address.equals(addressToBeSearched)) {
                playAudioHelper.startPlaying(context, wasList.get(i));
                break;
            }
        }
    }

    public void playAudio(Was was) {
        playAudioHelper.startPlaying(context, was);
    }
    //Audio Management Part End
}
