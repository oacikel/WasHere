package com.example.washere.Views.Fragments.Main_Button_Set_Fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.helpers.AudioHelper;
import com.example.washere.repositories.WasRepository;

public class MainButtonSetFragmentViewModel extends AndroidViewModel {
    private AudioHelper audioHelper;


    public MainButtonSetFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {

        audioHelper = new AudioHelper();
    }

    //Location Status
    public MutableLiveData<Integer> getLocationStatus() {
        return WasRepository.getInstance().getLocationStatus();
    }

    //Recording
    void prepareRecorder() {
        audioHelper.prepareRecorder();
    }

}