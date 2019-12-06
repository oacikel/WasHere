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
import com.example.washere.helpers.AudioHelper;
import com.example.washere.helpers.FirebaseFireStoreHelper;
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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private static String LOG_TAG = ("OCUL- MainActivityViewModel");
    private String intentName;
    private Context context;
    private MutableLiveData<GeoCoordinate> currentLocation = WasRepository.getInstance().getCurrentLocation();
    private PositioningManager positioningManager;
    private MutableLiveData<List<Was>> wasList;
    private boolean isUpdating = false;
    private AudioHelper audioHelper;
    private WasRepository wasRepository=WasRepository.getInstance();
    private ClusterLayer clusterLayer;
    private Collection<MapMarker> mapMarkers;
    private RecordWasDialog recordWasDialog;
    private FirebaseFireStoreHelper firebaseFireStoreHelper =new FirebaseFireStoreHelper();

    public void setUpdating(boolean updating) {
        isUpdating = updating;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    //Location Status
    public MutableLiveData<Integer> getLocationStatus() {
        return wasRepository.getLocationStatus();
    }

    private void updateLocationStatus(int status) {
       wasRepository.getLocationStatus().setValue(status);
    }

    //Was Recording / Playing State
    MutableLiveData<eWasUploadState> getWasRecordingState() {
        return wasRepository.getWasRecordingState();
    }


    //Was Recording Playing
    void initWasUploadDialog(FragmentTransaction fragmentTransaction, Fragment previousFragment) {
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        recordWasDialog = new RecordWasDialog();
        recordWasDialog.show(fragmentTransaction, "WAS_DIALOG");
    }

    void dismissWasUploadDialog() {

        if (recordWasDialog != null) {
            recordWasDialog.dismiss();
        }
    }

    boolean isDialogOn() {
        boolean isDialogOn = false;
        if (recordWasDialog != null) {
            isDialogOn = recordWasDialog.isVisible();
        }
        return isDialogOn;
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

    void onMapEngineInitialized() {
        positioningManager = PositioningManager.getInstance();
        clusterLayer = new ClusterLayer();
        wasRepository.setClusterLayer(clusterLayer);
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
            //Log.w("OCUL - While Loop", "Location Status is TEMPORARILY UNAVAILABLE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        }

        if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
            Log.w("OCUL - Positioning", "Location Status is OUT OF SERVICE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        } else if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.AVAILABLE) {
            Log.w("OCUL - Positioning", "Location Status is AVAILABLE.");
            if (positioningManager.getPosition().getCoordinate().getLatitude() != -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() != -1.7976931348623157E308) {
                currentLocation.setValue(PositioningManager.getInstance().getPosition().getCoordinate());
                updateLocationStatus(1);
            } else {
                while (positioningManager.getPosition().getCoordinate().getLatitude() == -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() == -1.7976931348623157E308) {
                    Log.w("OCUL - Positioning", "Wrong location pulled");
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    updateLocationStatus(2);
                }


            }
        }
    }

    MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return wasRepository.getCurrentLocation();
    }


    public void init() {
        wasRepository = WasRepository.getInstance();
        firebaseFireStoreHelper.updateWasObjects();
        audioHelper = new AudioHelper();
        getClusterViewObject();
    }

    //Updates the Markerlist and adds Map Markers to Was Objects:
    void updateMarkerList() {

        Log.d("OCUL - Marker Cluster", "Creating Cluster layer");
        for (int i = 0; i < wasRepository.getWasList().getValue().size(); i++) {
            Was was = wasRepository.getWasList().getValue().get(i);
            MapMarker marker = new MapMarker();
            Image image = new Image();
            try {
                image.setImageResource(R.drawable.place_holder_icon);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
            GeoCoordinate coordinate = new GeoCoordinate(was.getLocationLatitude(), was.getLocationLongitude());
            marker.setCoordinate(coordinate);
            marker.setIcon(image);
            marker.setTitle(String.valueOf(i));
            was.setMapMarker(marker);
            wasRepository.getClusterLayer().addMarker(marker);
        }
        mapMarkers = wasRepository.getClusterLayer().getMarkers();
        wasRepository.getClusterLayer().removeMarkers(mapMarkers);
        wasRepository.getClusterLayer().addMarkers(mapMarkers);
        setExistingClusterLayer(wasRepository.getClusterLayer());
    }

    MutableLiveData<List<Was>> getWasList() {
        wasList = wasRepository.getWasList();
        return wasList;
    }

    ClusterLayer getExistingClusterLayer() {
        Log.d(LOG_TAG, "Cluster Layer Updated");
        return wasRepository.getExistingClusterLayer();
    }

    private void setExistingClusterLayer(ClusterLayer clusterLayer) {
        wasRepository.setExistingClusterLayer(clusterLayer);
    }

    MutableLiveData<ClusterViewObject> getClusterViewObject() {
        return wasRepository.getClusterViewObject();
    }

    void setWasObjectsInCluster() {
        Collection<MapMarker> markerList = getClusterViewObject().getValue().getMarkers();
        String addressToBeSearched;
        String address;
        ArrayList<MapMarker> mapMarkerArrayList = new ArrayList<>(markerList);
        ArrayList<Was> wasListOfCluster = new ArrayList<>();
        for (int i = 0; i < mapMarkerArrayList.size(); i++) {
            addressToBeSearched = mapMarkerArrayList.get(i).toString();
            for (int a = 0; a < wasList.getValue().size(); a++) {
                address = wasList.getValue().get(a).getMapMarker().toString();
                if (address.equals(addressToBeSearched)) {
                    wasListOfCluster.add(wasList.getValue().get(a));
                }
            }
        }
        wasRepository.getSelectedClusterViewWasList().setValue(wasListOfCluster);
    }

    void setSelectedMarker(MapMarker marker) {
        wasRepository.setMarkerSelected(marker);
    }

    void setWasObjectFromSelectedMapMarker() {
        MapMarker selectedMarker = wasRepository.getMarkerSelected();
        ArrayList<MapMarker> mapMarkerArrayList = new ArrayList<>();
        mapMarkerArrayList.add(selectedMarker);
        ArrayList<Was> wasListOfCluster = new ArrayList<>();
        wasListOfCluster.add(wasList.getValue().get(0));
        WasRepository.getInstance().getSelectedClusterViewWasList().setValue(wasListOfCluster);
    }

    MutableLiveData<ArrayList<Was>> getSelectedClusterViewWasList() {
        return WasRepository.getInstance().getSelectedClusterViewWasList();
    }

    //Audio Management Part Start
    void playAudio(List<Was> wasList, MapMarker marker) {
        String addressToBeSearched = marker.toString();
        String address;
        for (int i = 0; i < wasList.size(); i++) {
            address = wasList.get(i).getMapMarker().toString();
            if (address.equals(addressToBeSearched)) {
                audioHelper.preparePlayerForSelectedWas(wasList.get(i));
                audioHelper.startPlayingWasObject();
                break;
            }
        }
    }

    void playAudio(Was was) {
        Log.d(LOG_TAG,"Preparing player");
        audioHelper.preparePlayerForSelectedWas(was);
        audioHelper.startPlayingWasObject();
    }
    //Audio Management Part End
}
