package com.example.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Application;
import android.media.MediaRecorder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.helpers.RecordAudioHelper;
import com.example.washere.models.Was;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

public class RecordWasDialogViewModel extends AndroidViewModel {
    ValueAnimator animator;
    RecordAudioHelper recordAudioHelper;
    WasRepository wasRepository;

    public RecordWasDialogViewModel(@NonNull Application application) {
        super(application);
        animator = ValueAnimator.ofInt(0, 1000);
        animator.setDuration(WasRepository.getInstance().getMAX_WAS_LENGTH());
        wasRepository = WasRepository.getInstance();
        recordAudioHelper = new RecordAudioHelper(application, wasRepository);
    }

    public MutableLiveData<eWasUploadState> getWasRecordingState() {
        return WasRepository.getInstance().getWasRecordingState();
    }

    public void updateWasRecordingState(eWasUploadState state) {
        WasRepository.getInstance().setWasRecordingState(state);
    }

    //Preparing and Uploading Was Items:
    //Ready To Record State:
    public ValueAnimator getAnimator() {
        return animator;
    }

    //Recording State:
    public void recordAudio() {
        recordAudioHelper.startRecording();
    }

    //Call the setUploadLocation method after recordAudio to determine the upload location
    public void setUploadLocation() {
        wasRepository.setUploadLocation(WasRepository.getInstance().getCurrentLocation().getValue());
    }

    //Recording Complete State:
    public void stopRecording() {
        recordAudioHelper.stopRecording();
    }

    //Prepare Media Player For Playback
    public void prepareMediaPlayer(){
        recordAudioHelper.preparePlayer();
    }

    //Ready To Play State:
    public void playAudio(){
        recordAudioHelper.startPlaying();
        updateWasRecordingState(eWasUploadState.PLAYING);
    }

    //Revert
    public void prepareRecorder(){
        recordAudioHelper.prepareRecorder();
    }

}
