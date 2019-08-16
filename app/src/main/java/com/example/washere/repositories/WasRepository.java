package com.example.washere.repositories;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.appcompat.app.AppCompatActivity;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.here.android.mpa.common.GeoCoordinate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WasRepository {

    private static WasRepository instance;
    private ArrayList<Was> dataSet = new ArrayList<>();
    private int count = 0;
    private GeoCoordinate currentLocation;
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private StorageReference audioReference;

    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public GeoCoordinate getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoCoordinate currentLocation) {
        this.currentLocation = currentLocation;
    }

    public MutableLiveData<List<Was>> getWasList() {
        //setWasItems();
        MutableLiveData<List<Was>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addWasItem(Was was) {
        dataSet.add(was);
        count++;
    }

    //Send data to the database
    public void sendFilesToDatabase(File file){
        Uri uri =Uri.fromFile(file);
        audioReference=storageReference.child("audio/"+file.getName());
        audioReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Dosya yüklendi!");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Dosya yüklenemedi: "+e.getMessage());
            }
        });
    }




}
