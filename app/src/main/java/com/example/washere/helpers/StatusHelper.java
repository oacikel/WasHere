package com.example.washere.helpers;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.here.android.mpa.common.PositioningManager;

public class StatusHelper {
    public static StatusHelper instance;
    private MutableLiveData<PositioningManager.LocationStatus> locationStatus = new MutableLiveData<>();

    public static StatusHelper getInstance() {
        if (instance == null) {
            instance = new StatusHelper();
        }
        return instance;
    }

}
