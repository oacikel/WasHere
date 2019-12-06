package com.example.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.ValueAnimator;
import android.app.Application;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.R;
import com.example.washere.helpers.AudioHelper;
import com.example.washere.helpers.WasUploadHelper;
import com.example.washere.models.eUploadingState;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

public class RecordWasDialogViewModel extends AndroidViewModel {
    private ValueAnimator animator;
    private AudioHelper audioHelper;
    private WasRepository wasRepository;
    private WasUploadHelper wasUploadHelper;

    public RecordWasDialogViewModel(@NonNull Application application) {
        super(application);
        animator = ValueAnimator.ofInt(0, 1000);
        animator.setDuration(WasRepository.getInstance().getMAX_WAS_LENGTH());
        wasRepository = WasRepository.getInstance();
        audioHelper = new AudioHelper();
        wasUploadHelper = new WasUploadHelper();
    }

    MutableLiveData<eWasUploadState> getWasRecordingState() {
        return wasRepository.getWasRecordingState();
    }

    void updateWasUploadState(eWasUploadState state) {
        if (wasRepository.getWasRecordingState().getValue()!=state){
           wasRepository.setWasRecordingState(state);
        }
    }

    public MutableLiveData<eUploadingState> getUploadingState(){
        return wasRepository.getUploadingState();
    }

    //Preparing and Uploading Was Items:
    //Ready To Record State:
    ValueAnimator getAnimator() {
        return animator;
    }

    //Recording State:
    void recordAudio() {
        audioHelper.startRecording();
    }

    //Call the setUploadLocation method after recordAudio to determine the upload location
    private void setUploadLocation() {
        wasRepository.setUploadLocation(wasRepository.getCurrentLocation().getValue());
    }

    //Set Upload Date and Time after recordAudio to determine upload Date and Time
    private void setUploadDate() {
        wasRepository.setUploadDate(wasRepository.getDate());
    }

    private void setUploadTime() {
        wasRepository.setUploadTime(wasRepository.getTime());
    }

    //Manage Date Time And Location For Was Upload:
    void setDateTimeLocation() {
        setUploadLocation();
        setUploadDate();
        setUploadTime();
    }
    void setUploadTitle(String title){
        wasRepository.setUploadTitle(title);
    }
    //Finish recording after animation stop
    void changeStateAfterAnimationEnd() {
        if (WasRepository.getInstance().getWasRecordingState().getValue() != eWasUploadState.FINISHED_RECORDING) {
            updateWasUploadState(eWasUploadState.FINISHED_RECORDING);
        }
    }

    //Recording Complete State:
    void stopRecording() {
        audioHelper.stopRecording();
    }

    //Prepare Media Player For Playback
    void prepareMediaPlayer() {
        audioHelper.preparePlayerForPreview();
    }

    //Ready To Play State:
    void playAudio() {
        audioHelper.startPlayingForPreview();
    }

    //Revert
    void prepareRecorder() {
        audioHelper.stopMediaPlayer();
        audioHelper.prepareRecorder();
    }

    //Exit
    public void exit() {
        audioHelper.stopMediaPlayer();
    }

    //Creating a Was Object and Uploading it To Firebase
    void uploadWas() {
        wasUploadHelper.createWasWithNoUri();
        wasUploadHelper.uploadFileToStorage(wasRepository.getUploadFile());
    }

    public void showUploadSuccessful(final TextView textView){
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                textView.setText(R.string.upload_successful);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);

    }

}
