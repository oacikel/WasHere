package com.example.washere.helpers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.Was;
import com.example.washere.repositories.WasRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

public class FirebaseFireStoreHelper {
    private Map<String, Object> wasObject = new HashMap<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ArrayList<Map> wasMap;


    //Take a Was Map And Send It To The Firestore
    public void addWasMapToFireStore(Was was) {
        wasObject.put("downloadURL", was.getDownloadUrl());
        wasObject.put("locationLatitude", was.getLocationLatitude());
        wasObject.put("locationLongitude", was.getLocationLongitude());
        wasObject.put("locationAltitude",was.getLocationAltitude());
        wasObject.put("markerDirectory", was.getMarkerDirectory());

        firebaseFirestore.collection("wasItems").add(wasObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i("Ocul", "Was Item successfully uploaded to Firestore with ID:" + documentReference.getId());
                System.out.println(wasObject.get("downloadURL"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Ocul", "Error uploading to Firesote: " + e.getMessage());
            }
        });
    }

    //Retrieve All The Was Maps From The Store
    public void getAllWasObjectsFromFireStore() {

        final FirebseStorageHelper firebseStorageHelper = new FirebseStorageHelper();
        wasMap = new ArrayList<>();
        firebaseFirestore.collection("wasItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Ocul", document.getId() + " => " + document.getData());
                        wasMap.add(document.getData());
                    }
                    ArrayList<Was> wasList = createWasArrayFromHashMap(wasMap);
                    firebseStorageHelper.addAudioFileToWasObject(wasList);
                    WasRepository.getInstance().getWasList().postValue(wasList);
                }
            }
        });

    }

    //Create a Was Object Array From Was Hash Map
    public ArrayList<Was> createWasArrayFromHashMap(ArrayList<Map> wasMap) {
        ArrayList<Was> wasList = new ArrayList<>();
        for (int i = 0; i < wasMap.size(); i++) {
            Was was = new Was((String) wasMap.get(i).get("downloadURL"),(double) (wasMap.get(i).get("locationLatitude")), ((double) wasMap.get(i).get("locationLongitude")), ((double) wasMap.get(i).get("locationAltitude")));
            wasList.add(was);
        }
        return wasList;
    }
}
