package ocul.longestlovestoryever.washere.helpers;

import android.util.Log;

import ocul.longestlovestoryever.washere.models.Was;
import ocul.longestlovestoryever.washere.repositories.UserRepository;
import ocul.longestlovestoryever.washere.repositories.WasRepository;
import com.here.android.mpa.common.GeoCoordinate;

import java.io.File;

public class WasUploadHelper {

    private String LOG_TAG = ("OCUL - WasUploadHelper");
    private WasRepository wasRepository=WasRepository.getInstance();
    private StorageHelper storageHelper =new StorageHelper();
    private DatabaseHelper databaseHelper=new DatabaseHelper();
    private UserRepository userRepository=UserRepository.getInstance();

    public void createWasToUpload() {
        Was wasToUpload;
        // TODO: 2019-12-06 Was'ın parametrelerini ayrı ayrı tutmaktansa yeni parametre yaratıldıkça repositorydeki sabit bir was'a eklenmeli. (Tarih belli olduğunda was.setDate(---) gibi...
        if (wasRepository.getUploadLocation() != null && wasRepository.getUploadTime() != null && wasRepository.getUploadDate() != null && wasRepository.getUploadTitle() != null && wasRepository.getUploadFile() != null) {
            GeoCoordinate uploadLocation = wasRepository.getUploadLocation();
            String uploaderName = userRepository.getCurrentUser().getUserName();
            String uploadTime = wasRepository.getUploadTime();
            String uploadDate = wasRepository.getUploadDate();
            String uploadTitle = wasRepository.getUploadTitle();
            File uploadFile = wasRepository.getUploadFile();
            wasToUpload = new Was(uploadLocation.getLatitude(), uploadLocation.getLongitude(), uploadLocation.getAltitude(), uploadTitle, uploaderName, uploadTime, uploadDate, uploadFile);
            wasToUpload.setPrivacyStatus(wasRepository.getPrivacyStatus().name());
            wasRepository.setWasToUpload(wasToUpload);
            Log.i(LOG_TAG, "A Was Object to upload is added to Repository.");
        } else {
            Log.e(LOG_TAG, "One or more of Was Constructor values is null");
        }
    }
    public void uploadFileToStorage(Was was) {
        File file=was.getAudioFile();
        if (file != null) {
            storageHelper.uploadFilesToStorage(was);
        } else {
            Log.e(LOG_TAG, "File is null");
        }
    }


    public void uploadWasToDatabase(Was was){
        databaseHelper.addWasToDatabase(was);
    }
}

