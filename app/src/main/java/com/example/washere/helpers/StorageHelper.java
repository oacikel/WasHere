package com.example.washere.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.Was;
import com.example.washere.models.eUploadingState;
import com.example.washere.repositories.WasRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StorageHelper {
    private static String LOG_TAG = "OCUL_StorageHelper";
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference audioReference;
    private Uri uri;
    private MutableLiveData<Boolean> isUploaded = new MutableLiveData<>();
    private String downloadUri;
    private String uploadTime;
    private String uploadDate;
    private WasRepository wasRepository= WasRepository.getInstance();
    private FirebaseFireStoreHelper firebaseFireStoreHelper=new FirebaseFireStoreHelper();
    private static String audioExtention = ("mp3");
    private static String audioChild = ("audio/");


    public StorageHelper() {

    }


    //Send data to the database
    public void uploadFilesToStorage(File file) {
        uri = Uri.fromFile(file);
        audioReference = storageReference.child(audioChild + file.getName());
        audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(LOG_TAG, "File uploaded to STORAGE");
                audioReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        wasRepository.setDownloadUrl(uri.toString());
                        wasRepository.postUpdateUploadingState(eUploadingState.STORAGE_UPLOAD_COMPLETE);
                        addWasHashMapToFireStore(downloadUri);
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                wasRepository.postUpdateUploadingState(eUploadingState.ERROR);
                Log.e(LOG_TAG, "Error Uploading file: " + e.getMessage());

            }
        });
    }

    //Retrieve data from the database
    public void addAudioFileToWasObject(final List<Was> wasList) {
        File localFile = null;
        Log.d(LOG_TAG, "WasList without audio file size is: " + wasList.size());
        try {
            localFile = File.createTempFile(audioChild, audioExtention);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error creating file: " + e.getMessage() + "\nAudio will not be downloaded from storage");
        }
        for (int i = 0; i < wasList.size(); i++) {
            System.out.println(wasList.get(i).getDownloadUrl());
            audioReference = storage.getReferenceFromUrl(wasList.get(i).getDownloadUrl());
            if (localFile != null) {
                final File finalLocalFile = localFile;
                final int finalI = i;
                audioReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i(LOG_TAG, "File downloaded successfully. Location:");
                        wasList.get(finalI).setAudioFile(finalLocalFile);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(LOG_TAG, "Error downloading file: " + exception.getMessage());
                    }
                });
            }
        }
    }

    void addWasHashMapToFireStore(String url) {
        Was wasToUpload = wasRepository.getUploadWasWithNoUri();
        wasToUpload.setDownloadUrl(url); //Embed the download link of the recording to the was item
        firebaseFireStoreHelper.addWasMapToFireStore(wasToUpload);
    }


}
