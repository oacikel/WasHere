package com.example.washere.helpers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.example.washere.models.eDownloadingState;
import com.example.washere.models.eUploadingState;
import com.example.washere.repositories.WasRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.MapMarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private static String LOG_TAG = "OCUL - DatabaseHelper";
    private Map<String, Object> wasObject = new HashMap<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private WasRepository wasRepository = WasRepository.getInstance();

    public void updateWasObjects() {
        //wasRepository.clearWasItems();
            CollectionReference wasItemReference=firebaseFirestore.collection("wasItems");
        if (wasRepository.getWasList() == null) {
            Log.i(LOG_TAG, "Was List is null. Creating new one");
            wasRepository.setWasList(new ArrayList<Was>());
        }
        if (wasRepository.getClusterLayer() == null) {
            wasRepository.setClusterLayer(new ClusterLayer());
            Log.i(LOG_TAG, "Cluster layer is null. Creating new one");
        }
            wasItemReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                    {
                        if (e != null) {
                            Log.e(LOG_TAG, "Error receiving updates from database: " + e.getMessage());
                        } else {

                            for (DocumentChange change : value.getDocumentChanges()) {
                                Was changedWas = createWasFromDocumentChange(change);
                                switch (change.getType()) {
                                    case ADDED:
                                        Log.i(LOG_TAG, "New was item received with id: " + change.getDocument().getId());

                                        wasRepository.getWasList().add(changedWas);
                                        wasRepository.getClusterLayer().addMarker(changedWas.getMapMarker());

                                        break;
                                    case MODIFIED:
                                        Log.i(LOG_TAG, "Was item modified");

                                        break;
                                    case REMOVED:
                                        Log.i(LOG_TAG, "Was item removed");


                                        break;
                                }
                            }
                            wasRepository.getMap().removeClusterLayer(wasRepository.getClusterLayer());
                            wasRepository.setUpdateDownloadingState(eDownloadingState.WAS_ITEM_ADDED);

                        }
                    }
                }
            });

            /*
        if (wasRepository.getWasItemsReference() == null) {
            wasRepository.setWasItemsReference(firebaseFirestore.collection("wasItems"));
        }
        if (wasRepository.getWasChangeListener() == null) {
            wasRepository.setWasChangeListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                    {
                        if (e != null) {
                            Log.e(LOG_TAG, "Error receiving updates from database: " + e.getMessage());
                        } else {

                            for (DocumentChange change : value.getDocumentChanges()) {
                                Was changedWas = createWasFromDocumentChange(change);
                                if (wasRepository.getWasList() == null) {
                                    Log.i(LOG_TAG, "Was List is null. Creating new one");
                                    wasRepository.setWasList(new ArrayList<Was>());
                                }
                                if (wasRepository.getClusterLayer() == null) {
                                    wasRepository.setClusterLayer(new ClusterLayer());
                                    Log.i(LOG_TAG, "Cluster layer is null. Creating new one");
                                }
                                switch (change.getType()) {
                                    case ADDED:
                                        Log.i(LOG_TAG, "New was item received with id: " + change.getDocument().getId());

                                        wasRepository.getWasList().add(changedWas);
                                        wasRepository.getClusterLayer().addMarker(changedWas.getMapMarker());

                                        break;
                                    case MODIFIED:
                                        Log.i(LOG_TAG, "Was item modified");

                                        break;
                                    case REMOVED:
                                        Log.i(LOG_TAG, "Was item removed");


                                        break;
                                }
                            }
                            wasRepository.getMap().removeClusterLayer(wasRepository.getClusterLayer());
                            wasRepository.setUpdateDownloadingState(eDownloadingState.WAS_ITEM_ADDED);

                        }
                    }
                }
            });
            wasRepository.getWasItemsReference().addSnapshotListener(wasRepository.getWasChangeListener());
        }
        wasRepository.setWasListenerInvokedOnce(false);
        */
    }

    //Take a Was Map And Send It To The Firestore
    public void addWasToDatabase(Was was) {
        if (was.getDownloadUrl() != null) {

            wasObject.put("downloadURL", was.getDownloadUrl());
            wasObject.put("locationLatitude", was.getLocationLatitude());
            wasObject.put("locationLongitude", was.getLocationLongitude());
            wasObject.put("locationAltitude", was.getLocationAltitude());
            wasObject.put("markerDirectory", was.getMarkerDirectory());
            wasObject.put("uploaderName", was.getUploaderName());
            wasObject.put("uploadDate", was.getUploadDate());
            wasObject.put("uploadTime", was.getUploadTime());
            wasObject.put("uploadTitle", was.getUploadTitle());

            collectionReference = firebaseFirestore.collection("wasItems");
            collectionReference.add(wasObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(LOG_TAG, "Was Item successfully uploaded to Database." + documentReference.getId());
                    wasRepository.postUpdateUploadingState(eUploadingState.DATABASE_UPLOAD_COMPLETE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(LOG_TAG, "Error uploading to database: " + e.getMessage());
                    wasRepository.postUpdateUploadingState(eUploadingState.ERROR);
                }
            });
        } else {
            Log.e(LOG_TAG, "Download URL is not set for Was");
            wasRepository.postUpdateUploadingState(eUploadingState.ERROR);
        }
    }
    //Create a Was Object Array From Was Hash Map

    public Was createWasFromDocumentChange(DocumentChange change) {
        Map wasMap = change.getDocument().getData();
        Was was = new Was(
                (double) wasMap.get("locationLatitude"),
                (double) wasMap.get("locationLongitude"),
                (double) wasMap.get("locationAltitude"),
                (String) wasMap.get("uploadTitle"),
                (String) wasMap.get("downloadURL"),
                (String) wasMap.get("uploaderName"),
                (String) wasMap.get("uploadTime"),
                (String) wasMap.get("uploadDate"));
        was.setUniqueId(change.getDocument().getId());
        MapMarker marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate((double) wasMap.get("locationLatitude"), (double) wasMap.get("locationLongitude")));
        marker.setTitle(was.getUniqueId());

        //Set Icon For Marker
        Image image = new Image();
        try {
            image.setImageResource(R.drawable.place_holder_icon);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in setting image Resource: " + e.getMessage());
        }
        marker.setIcon(image);
        was.setMapMarker(marker);

        return was;
    }


}
