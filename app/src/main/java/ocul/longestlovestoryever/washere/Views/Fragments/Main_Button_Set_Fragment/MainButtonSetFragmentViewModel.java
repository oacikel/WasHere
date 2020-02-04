package ocul.longestlovestoryever.washere.Views.Fragments.Main_Button_Set_Fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.helpers.AudioHelper;
import ocul.longestlovestoryever.washere.models.eLocationStatus;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

public class MainButtonSetFragmentViewModel extends AndroidViewModel {
    private AudioHelper audioHelper;
    private WasRepository wasRepository=WasRepository.getInstance();


    public MainButtonSetFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {

        audioHelper = new AudioHelper();
    }

    //Location Status
    public MutableLiveData<eLocationStatus> getLocationStatus() {
        return WasRepository.getInstance().getLocationStatus();
    }

    //Recording
    void prepareRecorder() {
        audioHelper.prepareRecorder();
    }

}