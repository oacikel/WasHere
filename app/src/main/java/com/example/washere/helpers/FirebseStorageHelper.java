package com.example.washere.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.Was;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FirebseStorageHelper {
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseStorage storage=FirebaseStorage.getInstance();
    private StorageReference audioReference;
    private Uri uri;
    private MutableLiveData<Boolean> isUploaded = new MutableLiveData<>();
    private MutableLiveData<String> downloadUri = new MutableLiveData<>();
    private String uploadTime;
    private String uploadDate;

    private static String audioExtention = ("mp3");
    private static String audioChild = ("audio/");


    public FirebseStorageHelper() {

    }


    public MutableLiveData<Boolean> isUploaded() {
        return isUploaded;
    }

    public MutableLiveData<String> getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(MutableLiveData<String> downloadUri) {
        this.downloadUri = downloadUri;
    }

    //Send data to the database
    public void uploadFilesToStorage(File file) {
        uri = Uri.fromFile(file);
        audioReference = storageReference.child(audioChild + file.getName());
        audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("File uploaded!");
                audioReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloaURL=uri.toString();
                        System.out.println("Download id is: "+downloaURL);
                        downloadUri.postValue(downloaURL);
                    }
                });

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Ocul", "Error Uploading file: " + e.getMessage());

            }
        });
    }

    //Retrieve data from the database
    public void addAudioFileToWasObject(final List<Was> wasList) {
        File localFile = null;
        Log.d("OCUL - Storage Helper: ","WasList without audio file size is: "+wasList.size());
        try {
            localFile = File.createTempFile(audioChild, audioExtention);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("OCUL - Storage Helper: ", "Error creating file: " + e.getMessage() + "\nAudio will not be downloaded from storage");
        }
        for (int i =0; i<wasList.size();i++) {
            System.out.println(wasList.get(i).getDownloadUrl());
            audioReference = storage.getReferenceFromUrl(wasList.get(i).getDownloadUrl());
            if (localFile != null) {
                final File finalLocalFile = localFile;
                final int finalI = i;
                audioReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i("OCUL", "File downloaded successfully. Location:");
                        wasList.get(finalI).setAudioFile(finalLocalFile);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("OCUL", "Error downloading file: " + exception.getMessage());
                    }
                });
            }
        }
    }


}
