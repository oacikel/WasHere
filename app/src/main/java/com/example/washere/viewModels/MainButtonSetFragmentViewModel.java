package com.example.washere.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.washere.R;
import com.example.washere.helpers.RecordAudioHelper;
import com.example.washere.models.Was;
import com.example.washere.repositories.WasRepository;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.PositioningManager;

public class MainButtonSetFragmentViewModel extends AndroidViewModel {
    private RecordAudioHelper recordAudioHelper;
    private PositioningManager positioningManager;
    private int count;
    private GeoCoordinate currentLocation;
    private WasRepository wasRepository;

    public MainButtonSetFragmentViewModel(@NonNull Application application) {
        super(application);
    }



    public void init() {
        wasRepository=WasRepository.getInstance();
        recordAudioHelper = new RecordAudioHelper(getApplication(),wasRepository);

    }

    public void startRecordingWasItem() {
        recordAudioHelper.startRecording();
        currentLocation = wasRepository.getCurrentLocation();
    }

    public void addWasItemAfterRecording(){
        Was was=createWas();
        wasRepository.addWasItem(was);
    }


    public Was createWas() {
        recordAudioHelper.stopRecording();
        Was was = new Was(count, 1, recordAudioHelper.getFilePath(), recordAudioHelper.getFileName(), currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), R.drawable.place_holder_icon);
        wasRepository.setCount(wasRepository.getCount()+1);
        return was;
    }
}
