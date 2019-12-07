package com.example.washere.helpers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.washere.models.Was;
import com.example.washere.models.eUploadingState;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static String LOG_TAG ="OCUL";
    private Map<String, Object> wasObject = new HashMap<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private WasRepository wasRepository=WasRepository.getInstance();

    public void updateWasObjects(){

        collectionReference=firebaseFirestore.collection("wasItems");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                List<Was> wasList;
                if (e!=null){
                    Log.e("OCUL - Firestore","Error: "+e.getMessage());
                }
                else{
                    wasList=new ArrayList<>();
                    for (QueryDocumentSnapshot document:value){
                        wasList.add(createWasFromHashmap(document.getData()));
                    }
                    WasRepository.getInstance().getWasList().setValue(wasList);
                }
            }
        });
    }

    //Take a Was Map And Send It To The Firestore
    public void addWasToDatabase(Was was) {
        if (was.getDownloadUrl()!=null){
            wasObject.put("downloadURL", was.getDownloadUrl());
            wasObject.put("locationLatitude", was.getLocationLatitude());
            wasObject.put("locationLongitude", was.getLocationLongitude());
            wasObject.put("locationAltitude", was.getLocationAltitude());
            wasObject.put("markerDirectory", was.getMarkerDirectory());
            wasObject.put("uploaderName", was.getUploaderName());
            wasObject.put("uploadDate", was.getUploadDate());
            wasObject.put("uploadTime", was.getUploadTime());
            wasObject.put("uploadTitle",was.getUploadTitle());

            collectionReference=firebaseFirestore.collection("wasItems");
            collectionReference.add(wasObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(LOG_TAG, "Was Item successfully uploaded to Database." + documentReference.getId());
                    wasRepository.getWasRecordingState().postValue(eWasUploadState.UPLOAD_SUCCESSFUL);
                    wasRepository.postUpdateUploadingState(eUploadingState.DATABASE_UPLOAD_COMPLETE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, "Error uploading to database: " + e.getMessage());
                    wasRepository.getWasRecordingState().postValue(eWasUploadState.UPLOAD_ERROR);
                    wasRepository.postUpdateUploadingState(eUploadingState.ERROR);
                }
            });
        } else{
            Log.e(LOG_TAG,"Download URL is not set for Was");
            wasRepository.postUpdateUploadingState(eUploadingState.ERROR);
        }
    }
    //Create a Was Object Array From Was Hash Map
    public ArrayList<Was> createWasArrayFromHashMap(ArrayList<Map> wasMapList) {
        ArrayList<Was> wasList = new ArrayList<>();
        for (int i = 0; i < wasMapList.size(); i++) {
           Was was=createWasFromHashmap(wasMapList.get(i));
            wasList.add(was);
        }
        return wasList;
    }

    public Was createWasFromHashmap(Map wasMap){
        Was was = new Was(
                (double) wasMap.get("locationLatitude"),
                (double) wasMap.get("locationLongitude"),
                (double) wasMap.get("locationAltitude"),
                (String) wasMap.get("uploadTitle"),
                (String) wasMap.get("downloadURL"),
                (String) wasMap.get("uploaderName"),
                (String) wasMap.get("uploadTime"),
                (String) wasMap.get("uploadDate"));
        return was;
    }
}
