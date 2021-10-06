package ocul.longestlovestoryever.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.ValueAnimator;
import android.app.Application;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.helpers.AudioHelper;
import ocul.longestlovestoryever.washere.helpers.WasUploadHelper;
import ocul.longestlovestoryever.washere.models.ePrivacyStatus;
import ocul.longestlovestoryever.washere.models.eUploadingState;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.models.eViewMode;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

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

    MutableLiveData<eRecordState> getWasRecordingState() {
        return wasRepository.getWasRecordingState();
    }

    void updateWasUploadState(eRecordState state) {
        if (wasRepository.getWasRecordingState().getValue()!=state){
           wasRepository.setWasRecordingState(state);
        }
    }

    void updateUploadingState(eUploadingState state) {
        if (wasRepository.getUploadingState().getValue()!=state){
            wasRepository.setUpdateUploadingState(state);
        }
    }


    public MutableLiveData<eUploadingState> getUploadingState(){
        return wasRepository.getUploadingState();
    }

    //Pre Record Countdown
    public void setCountDownText(final TextView textView){
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText(String.valueOf (millisUntilFinished / 1000));
            }

            public void onFinish() {
                textView.setText("GO!");
                updateWasUploadState(eRecordState.RECORDING);
            }

        }.start();
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
        if (WasRepository.getInstance().getWasRecordingState().getValue() != eRecordState.FINISHED_RECORDING) {
            updateWasUploadState(eRecordState.FINISHED_RECORDING);
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
    void uploadWasToStorage() {
        wasUploadHelper.createWasToUpload();
        wasUploadHelper.uploadFileToStorage(wasRepository.getWasToUpload());
    }

    void uploadWasToDatabase(){
        wasUploadHelper.uploadWasToDatabase(wasRepository.getWasToUpload());
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

    public void setPrivacyStatus(ePrivacyStatus aPrivate) {
        wasRepository.setPrivacyStatus(aPrivate);
    }
}
