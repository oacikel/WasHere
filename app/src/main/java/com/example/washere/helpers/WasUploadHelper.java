package com.example.washere.helpers;

import android.util.Log;

import com.example.washere.models.Was;
import com.example.washere.repositories.UserRepository;
import com.example.washere.repositories.WasRepository;
import com.here.android.mpa.common.GeoCoordinate;

import java.io.File;

public class WasUploadHelper {

    private String LOG_TAG = ("OCUL - WasUploadHelper");
    private WasRepository wasRepository=WasRepository.getInstance();
    private StorageHelper storageHelper =new StorageHelper();
    private UserRepository userRepository=UserRepository.getInstance();

    public void createWasWithNoUri() {
        Was uploadWasWithNoUri;

        if (wasRepository.getUploadLocation() != null && wasRepository.getUploadTime() != null && wasRepository.getUploadDate() != null && wasRepository.getUploadTitle() != null && wasRepository.getUploadFile() != null) {
            GeoCoordinate uploadLocation = wasRepository.getUploadLocation();
            String uploaderName = userRepository.getCurrentUser().getUserName();
            String uploadTime = wasRepository.getUploadTime();
            String uploadDate = wasRepository.getUploadDate();
            String uploadTitle = wasRepository.getUploadTitle();
            File uploadFile = wasRepository.getUploadFile();
            uploadWasWithNoUri = new Was(uploadLocation.getLatitude(), uploadLocation.getLongitude(), uploadLocation.getAltitude(), uploadTitle, uploaderName, uploadTime, uploadDate, uploadFile);
            wasRepository.setUploadWasWithNoUri(uploadWasWithNoUri);
            Log.i(LOG_TAG, "A Was Object to upload is added to Repository.");
        } else {
            Log.e(LOG_TAG, "One or more of Was Constructor values is null");
        }
    }
    public void uploadFileToStorage(File file) {
        if (file != null) {
            storageHelper.uploadFilesToStorage(file);
        } else {
            Log.e(LOG_TAG, "File is null");
        }

    }

    public void uploadWasToDatabase(Was was){

    }
}

