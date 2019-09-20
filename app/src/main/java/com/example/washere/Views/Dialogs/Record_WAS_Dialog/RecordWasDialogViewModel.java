package com.example.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.ValueAnimator;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.helpers.AudioHelper;
import com.example.washere.helpers.WasUploadHelper;
import com.example.washere.models.Was;
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
        return WasRepository.getInstance().getWasRecordingState();
    }

    void updateWasRecordingState(eWasUploadState state) {
        WasRepository.getInstance().setWasRecordingState(state);
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
        wasRepository.setUploadLocation(WasRepository.getInstance().getCurrentLocation().getValue());
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
    void chgangeStateAfterAnimationEnd() {
        if (WasRepository.getInstance().getWasRecordingState().getValue() != eWasUploadState.FINISHED_RECORDING) {
            updateWasRecordingState(eWasUploadState.FINISHED_RECORDING);
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
        updateWasRecordingState(eWasUploadState.PLAYING);
    }

    //Revert
    void prepareRecorder() {
        audioHelper.stopMediaPlayer();
        audioHelper.prepareRecorder();
    }

    //Exit
    public void exit() {
        audioHelper.stopMediaPlayer();
        updateWasRecordingState(eWasUploadState.CANCELED_ACTION_OR_UPLOAD_COMPLETE);
    }

    //Creating a Was Object and Uploading it To Firebase
    void createWas() {
        wasUploadHelper.createWasWithNoUri();
        wasUploadHelper.uploadWasToFireBase(wasRepository.getUploadFile());
    }

    //Retrieving Download Link
    MutableLiveData<String> getDownloadUrl() {
        return wasRepository.getDownloadUrl();
    }

    //Add Was HashMap To FireStore
    void addWasHashMapToFireStore(String url) {
        Was wasToUpload = wasRepository.getUploadWasWithNoUri();
        wasToUpload.setDownloadUrl(url); //Embed the download link of the recording to the was item
        wasRepository.addWasHashMapToFireStore(wasToUpload); //Store the properties of the was object as a hashmap in the Firestore
    }
}
