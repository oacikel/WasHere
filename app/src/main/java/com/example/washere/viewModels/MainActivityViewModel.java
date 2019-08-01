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

import java.io.File;
import java.io.IOException;

public class MainActivityViewModel extends AndroidViewModel {

    private String diskCacheRoot;
    private String intentName;
    private ApplicationInfo applicationInfo;
    private Bundle bundle;
    private Context context;
    private boolean success;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();

        diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(context.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);

    }


    //Map Management Part Start

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

    public boolean isSuccess() {
        return success;
    }
}
