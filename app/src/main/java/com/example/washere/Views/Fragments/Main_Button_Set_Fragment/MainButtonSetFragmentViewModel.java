package com.example.washere.Views.Fragments.Main_Button_Set_Fragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.example.washere.R;
import com.example.washere.helpers.FirebseStorageHelper;
import com.example.washere.helpers.RecordAudioHelper;
import com.example.washere.models.Was;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;
import com.example.washere.Views.Activities.Main_Activity.MainActivity;
import com.here.android.mpa.common.GeoCoordinate;

import java.util.List;

public class MainButtonSetFragmentViewModel extends AndroidViewModel {
    private RecordAudioHelper recordAudioHelper;
    private GeoCoordinate currentLocation;
    private WasRepository wasRepository;
    private MutableLiveData<List<Was>> wasList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUpdated =new MutableLiveData<>();
    private MainActivity activity;
    private FirebseStorageHelper firebseStorageHelper;
    private Was was;
    private MutableLiveData<String>downloadUrl =new MutableLiveData<>();


    public MainButtonSetFragmentViewModel(@NonNull Application application) {
        super(application);
    }


    public void init() {
        wasRepository = WasRepository.getInstance();
        recordAudioHelper = new RecordAudioHelper(getApplication(), wasRepository);
    }

    //Location Status
    public MutableLiveData<Integer> getLocationStatus() {
        return WasRepository.getInstance().getLocationStatus();
    }
    public void updateLocationStatus(int status){
        WasRepository.getInstance().getLocationStatus().setValue(status);
    }

    //Was Recording /Playing Status
    public MutableLiveData<eWasUploadState> getWasRecordingState() {
        return WasRepository.getInstance().getWasRecordingState();
    }

    public void updateWasRecordingState(eWasUploadState state) {
        WasRepository.getInstance().setWasRecordingState(state);
    }


    //Recording
    public void prepareRecorder(){
        recordAudioHelper.prepareRecorder();
    }

    public void startRecordingWasItem() {
        recordAudioHelper.startRecording();
        currentLocation = WasRepository.getInstance().getCurrentLocation().getValue();
    }

    public void addWasItemAfterRecording() {
        was = createWas(); //Create a Was File At The Location Of The Recording
        was.setUploadDate(recordAudioHelper.getFile().getUploadDate());
        was.setUploadTime(recordAudioHelper.getFile().getUploadTime());
        was.setUploaderName(recordAudioHelper.getFile().getUploaderName());
        wasRepository.uploadFilesToFirebaseStorage(recordAudioHelper.getFile()); //Upload the audio file of the was to Firebase Storage
    }

    public void addWasHashMapToFireStore(String url){
        was.setDownloadUrl(url); //Embed the download link of the recording to the was item
        wasRepository.addWasHashMapToFireStore(was); //Store the properties of the was object as a hashmap in the Firestore
    }

    public MutableLiveData<Boolean> getIsUpdated() {
        return isUpdated;
    }

    private Was createWas() {
        recordAudioHelper.stopRecording();
        Was was = new Was(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), R.drawable.place_holder_icon);
        return was;
    }

    public MutableLiveData<String> getDownloadUrl(){
        return wasRepository.getFirebseStorageHelper().getDownloadUri();
    }
}