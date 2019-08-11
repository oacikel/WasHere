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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.washere.R;
import com.example.washere.helpers.PlayAudioHelper;
import com.example.washere.helpers.RecordAudioHelper;
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

    private String intentName;
    private Context context;
    private MutableLiveData<GeoCoordinate> currentLocation = new MutableLiveData<>();
    private PositioningManager positioningManager;
    private MutableLiveData<List<Was>> wasList = new MutableLiveData<>();
    private boolean isUpdating = false;
    private List<MapObject> markerList = new ArrayList<MapObject>();
    private PlayAudioHelper playAudioHelper;
    private RecordAudioHelper recordAudioHelper;
    private WasRepository wasRepository;

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
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        wasRepository.setCurrentLocation(positioningManager.getPosition().getCoordinate());
        updateCurrentLocation();
        //System.out.println("Will update map marker");
        //updateMarkerList();
        //System.out.println("Updated map marker");
        PositioningManager.OnPositionChangedListener positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                updateCurrentLocation();
                wasRepository.setCurrentLocation(positioningManager.getPosition().getCoordinate());
            }

            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
                updateCurrentLocation();
                wasRepository.setCurrentLocation(positioningManager.getPosition().getCoordinate());
            }
        };
        positioningManager.addListener(new WeakReference<>(positionListener));
    }

    public MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return currentLocation;
    }

    public void updateCurrentLocation() {
        currentLocation.setValue(wasRepository.getCurrentLocation());

    }

    public void init() {
        //Iterate through all elements of the received Mutable Was Arraylist and create a list of Map Markers
        wasRepository = WasRepository.getInstance();
        wasList.setValue(wasRepository.getWasList().getValue());
        playAudioHelper = new PlayAudioHelper();


    }

    public void updateMarkerList() {
        if (markerList.size() != 0) {
            markerList.clear();
        }
        for (int i = 0; i < wasList.getValue().size(); i++) {
            Was was = wasList.getValue().get(i);
            MapMarker marker = new MapMarker();
            Image image = new Image();
            try {
                image.setImageResource(was.getMarkerDirectory());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
            GeoCoordinate coordinate = new GeoCoordinate(was.getLocationLatitude(), was.getLocationLongitude(), was.getLocationAltitude());
            marker.setCoordinate(coordinate);
            marker.setIcon(image);
            marker.setTitle(String.valueOf(i));
            markerList.add(marker);

        }
        setMarkerList(markerList);
    }

    public List<MapObject> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<MapObject> markerList) {
        this.markerList = markerList;
    }

    public MutableLiveData<List<Was>> getWasList() {
        wasRepository = WasRepository.getInstance();
        wasRepository.getWasList();
        return wasList;
    }

    public void setWasList(MutableLiveData<List<Was>> wasList) {
        this.wasList = wasList;
    }

    public void addAnotherWasItem() {
        List<Was> currentWasItems = wasList.getValue();
        currentWasItems.add(new Was(2, R.raw.tekerleme03, "", "", 40.977047, 29.0518113, 0.0, R.drawable.place_holder_icon)); //Sancaktepe Additional
        wasRepository.setCount(wasRepository.getCount() + 1);
        currentWasItems.add(new Was(3, R.raw.tekerleme04, "", "", 40.985381, 29.042111, 0.0, R.drawable.place_holder_icon));  //Kızıltoprak Additional
        wasList.postValue(currentWasItems);
        wasRepository.setCount(wasRepository.getCount() + 1);
    }

    //Map Management Part End

    //Audio Management Part Start

    public void playAudio(List<Was> wasList, MapMarker marker) {
        playAudioHelper.startPlaying(wasList.get(wasRepository.getCount()));
        //playAudioHelper.play(context, wasList.get(Integer.parseInt(marker.getTitle())).getResId());
    }

    //Audio Management Part End


}
