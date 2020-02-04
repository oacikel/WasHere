package ocul.longestlovestoryever.washere.helpers;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.models.Was;
import ocul.longestlovestoryever.washere.models.eDownloadingState;
import ocul.longestlovestoryever.washere.models.ePrivacyStatus;
import ocul.longestlovestoryever.washere.models.eUploadingState;
import ocul.longestlovestoryever.washere.models.eViewMode;
import ocul.longestlovestoryever.washere.repositories.UserRepository;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.here.android.mpa.cluster.BasicClusterStyle;
import com.here.android.mpa.cluster.ClusterDensityRange;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterStyle;
import com.here.android.mpa.cluster.ClusterTheme;
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
    private UserRepository userRepository = UserRepository.getInstance();

    public void updateWasObjects() {
        CollectionReference wasItemReference = firebaseFirestore.collection("wasItems");
        if (wasRepository.getAllWasList() == null) {
            Log.i(LOG_TAG, "Global Was List is null. Creating new one");
            wasRepository.setAllWasList(new ArrayList<Was>());
        }
        if (wasRepository.getAllClusterLayer() == null) {
            wasRepository.setAllClusterLayer(new ClusterLayer());
            Log.i(LOG_TAG, "Global Cluster layer is null. Creating new one");
        }

        //Set My WasList
        if (wasRepository.getMyWasList() == null) {
            Log.i(LOG_TAG, "Private Was List is null. Creating new one");
            wasRepository.setMyWasList(new ArrayList<Was>());
        }
        if (wasRepository.getMyClusterLayer() == null) {
            wasRepository.setMyClusterLayer(new ClusterLayer());
            Log.i(LOG_TAG, "Private Cluster layer is null. Creating new one");
        }

        wasItemReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(LOG_TAG, "Error receiving updates from database: " + e.getMessage());
                } else {
                    for (DocumentChange change : value.getDocumentChanges()) {
                        Was changedWas = createWasFromDocumentChange(change);
                        //Add Was if uploader is me
                        if (changedWas.getUploaderName().equals(userRepository.getCurrentUser().getUserName())) {
                            Log.d(LOG_TAG, "This was is created by current user");
                            switch (change.getType()) {
                                case ADDED:
                                    Log.i(LOG_TAG, "Users was is received with ID: " + change.getDocument().getId());
                                    wasRepository.getAllWasList().add(changedWas);
                                    wasRepository.getAllClusterLayer().addMarker(changedWas.getMapMarker());
                                    wasRepository.getMyWasList().add(changedWas);
                                    wasRepository.getMyClusterLayer().addMarker(changedWas.getMapMarker());
                                    break;
                                case MODIFIED:
                                    Log.i(LOG_TAG, "Was item modified");
                                    break;
                                case REMOVED:
                                    Log.i(LOG_TAG, "Was item removed");
                                    break;
                            }
                        }
                        //If uploader isn't me take only the non private ones
                        else if (ePrivacyStatus.PRIVATE.name().equals(changedWas.getPrivacyStatus())) {
                            Log.d(LOG_TAG, "This was is created by " + changedWas.getUploaderName() + " but is private");
                        } else {
                            Log.d(LOG_TAG, "This was is created by " + changedWas.getUploaderName() + " and is not private");
                            switch (change.getType()) {
                                case ADDED:
                                    Log.i(LOG_TAG, "New was item received with id: " + change.getDocument().getId());

                                    wasRepository.getAllWasList().add(changedWas);
                                    wasRepository.getAllClusterLayer().addMarker(changedWas.getMapMarker());

                                    break;
                                case MODIFIED:
                                    Log.i(LOG_TAG, "Was item modified");

                                    break;
                                case REMOVED:
                                    Log.i(LOG_TAG, "Was item removed");
                                    break;
                            }
                        }
                    }
                }
                wasRepository.getMap().removeClusterLayer(wasRepository.getAllClusterLayer());
                wasRepository.getMap().removeClusterLayer(wasRepository.getMyClusterLayer());
                wasRepository.setUpdateDownloadingState(eDownloadingState.WAS_ITEM_ADDED);
            }
        });
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
            wasObject.put("privacySetting", was.getPrivacyStatus());

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
        was.setPrivacyStatus((String) wasMap.get("privacySetting"));
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
