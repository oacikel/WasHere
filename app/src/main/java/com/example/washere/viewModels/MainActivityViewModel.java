package com.example.washere.viewModels;

/*
This class will serve to manipulate audio files.
    Methods:
        RecordAudio - records the audio input in realtime from device.
        Play Audio - plays a selected audio file from user.
        ...
*/


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.washere.R;
import com.example.washere.models.WHAudioData;
import com.example.washere.models.Was;
import com.example.washere.repositories.WasRepository;
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
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private String diskCacheRoot;
    private String intentName;
    private Context context;
    private boolean success;
    public MutableLiveData<GeoCoordinate> currentLocation = new MutableLiveData<>();
    private PositioningManager positioningManager;
    private PositioningManager.OnPositionChangedListener positionListener;
    private MutableLiveData<List<Was>> wasList;
    private WasRepository wasRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    //Map Management Part Start
    public boolean isSuccess() {

        diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(context.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);

        return success;
    }

    public void onMapEngineInitialized() {
        positioningManager = PositioningManager.getInstance();
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        positionListener = new PositioningManager.OnPositionChangedListener() {
            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {

                if (currentLocation.getValue() != positioningManager.getPosition().getCoordinate()) {
                    currentLocation.setValue(updateCurrentLocation().getValue());
                    System.out.println("Position updated and changed");
                } else {
                    System.out.println("Position just updated");
                }
            }

            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
                System.out.println("Position Fix Changed");
            }
        };
        positioningManager.addListener(new WeakReference<>(positionListener));

    }


    public MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(MutableLiveData<GeoCoordinate> currentLocation) {
        this.currentLocation = currentLocation;
    }

    public MutableLiveData<GeoCoordinate> updateCurrentLocation() {
        currentLocation.setValue(positioningManager.getPosition().getCoordinate());
        return currentLocation;
    }

    public void init() {
        //Iterate through all elements of the received Mutable Was Arraylist and create a list of Map Markers

        if (wasList != null) {
            return;
        }
        wasRepository = WasRepository.getInstance();
        wasList = wasRepository.getWasList();
    }

    public  List<MapObject> getWasMapMarkers() {
        List<MapObject> markerList = new ArrayList<MapObject>();
        for (int i = 0; i < wasList.getValue().size(); i++) {
            Was was=wasList.getValue().get(i);
            MapMarker marker = new MapMarker();
            Image image = new Image();
            try {
                image.setImageResource(was.getMarkerDirectory());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
            GeoCoordinate coordinate=new GeoCoordinate(was.getLocationLatitude(),was.getLocationLongitude(),was.getLocationAltitude());
            marker.setCoordinate(coordinate);
            marker.setIcon(image);
            markerList.add(marker);
        }
        return markerList;
    }

    //Map Management Part End

    //Audio Management Part Start

    public void playAudio(WHAudioData whAudioData) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        String path = whAudioData.getPath();
        String fileName = whAudioData.getFileName();
        try {
            mediaPlayer.setDataSource(path + File.separator + fileName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    //Audio Management Part End


}
