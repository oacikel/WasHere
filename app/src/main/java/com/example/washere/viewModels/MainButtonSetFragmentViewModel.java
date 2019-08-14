package com.example.washere.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.washere.R;
import com.example.washere.helpers.RecordAudioHelper;
import com.example.washere.models.Was;
import com.example.washere.repositories.WasRepository;
import com.example.washere.views.MainActivity;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.PositioningManager;

import java.util.List;

public class MainButtonSetFragmentViewModel extends AndroidViewModel {
    private RecordAudioHelper recordAudioHelper;
    private PositioningManager positioningManager;
    private int count;
    private GeoCoordinate currentLocation;
    private WasRepository wasRepository;
    private MutableLiveData<List<Was>> wasList = new MutableLiveData<>();
    private MainActivity activity;

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
        List<Was>currentWasList=wasList.getValue();
        currentWasList.add(was);
        wasList.postValue(currentWasList);
    }

    public MutableLiveData<List<Was>> getWasList() {
        wasList=WasRepository.getInstance().getWasList();
        return  wasList;
    }

    public Was createWas() {
        recordAudioHelper.stopRecording();
        Was was = new Was(WasRepository.getInstance().getCount()+1, 1, recordAudioHelper.getFilePath(), recordAudioHelper.getFileName(), currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), R.drawable.place_holder_icon);
        return was;
    }

    public void addMarkersOnMainActivity(){

        activity.placeMarkersOnMap();
    }
}