package com.example.washere.viewModels;

/*
This class will serve to manipulate audio files.
    Methods:
        RecordAudio - records the audio input in realtime from device.
        Play Audio - plays a selected audio file from user.
        ...
*/


import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.washere.models.WHAudioData;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.PositioningManager;
import com.here.services.location.internal.PositionListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivityViewModel extends AndroidViewModel {

    private String diskCacheRoot;
    private String intentName;
    private Context context;
    private boolean success;
    public MutableLiveData<GeoCoordinate> currentLocation = new MutableLiveData<>();
    private PositioningManager positioningManager;
    private PositioningManager.OnPositionChangedListener positionListener;
    private MutableLiveData<Boolean> isPermissionGranted =new MutableLiveData<>();

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

    public void init(){

    }

    public void onMapEngineInitialized(){
        positioningManager = PositioningManager.getInstance();
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        positionListener= new PositioningManager.OnPositionChangedListener() {
            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {

                if (currentLocation.getValue()!=positioningManager.getPosition().getCoordinate()) {
                    currentLocation.setValue(updateCurrentLocation().getValue());
                    System.out.println("Position updated and changed");
                }
                else{
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
