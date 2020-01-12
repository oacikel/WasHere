package ocul.longestlovestoryever.washere.Views.Activities.Map_Activity;

/*
This class will serve to manipulate audio files.
    Methods:
        RecordAudio - records the audio input in realtime from device.
        Play Audio - plays a selected audio file from user.
        ...
*/


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.Views.Dialogs.Record_WAS_Dialog.RecordWasDialog;
import ocul.longestlovestoryever.washere.adapters.WasCardAdapter;
import ocul.longestlovestoryever.washere.helpers.AudioHelper;
import ocul.longestlovestoryever.washere.helpers.DatabaseHelper;
import ocul.longestlovestoryever.washere.models.Was;
import ocul.longestlovestoryever.washere.models.eDownloadingState;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.repositories.WasRepository;
import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapProxyObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MapActivityViewModel extends AndroidViewModel {
    private static String LOG_TAG = ("OCUL- MainActivityViewModel");
    private String intentName;
    private Context context;
    private MutableLiveData<GeoCoordinate> currentLocation = WasRepository.getInstance().getCurrentLocation();
    private PositioningManager positioningManager;
    private AudioHelper audioHelper = new AudioHelper();
    private WasRepository wasRepository = WasRepository.getInstance();
    private ClusterLayer clusterLayer;
    private RecordWasDialog recordWasDialog;
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    private MapProxyObject mapProxyObject;
    private MapObject mapObject;
    private ClusterViewObject clusterViewObject;
    private ArrayList<MapMarker> selectedMarkerList;
    private MapMarker selectedMarker;
    private ArrayList<Was> selectedWasList;
    private ArrayList<Was> fullWasList;

    public MapActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    //Location Status
    public MutableLiveData<Integer> getLocationStatus() {
        return wasRepository.getLocationStatus();
    }

    private void updateLocationStatus(int status) {
        wasRepository.getLocationStatus().setValue(status);
    }

    //Was Recording / Playing State
    MutableLiveData<eRecordState> getWasRecordingState() {
        return wasRepository.getWasRecordingState();
    }


    //Was Recording Playing
    void initWasUploadDialog(FragmentTransaction fragmentTransaction, Fragment previousFragment) {
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }
        fragmentTransaction.addToBackStack(null);
        recordWasDialog = new RecordWasDialog();
        recordWasDialog.show(fragmentTransaction, "WAS_DIALOG");
    }


    boolean isDialogOn() {
        boolean isDialogOn = false;
        if (recordWasDialog != null) {
            isDialogOn = recordWasDialog.isVisible();
        }
        return isDialogOn;
    }

    //Map Management Part Start
    public boolean isSuccess() {

        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(context.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        return com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
    }


    void onMapEngineInitialized() {
        positioningManager = PositioningManager.getInstance();
        PositioningManager.OnPositionChangedListener positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                currentLocation.setValue(geoPosition.getCoordinate());
            }

            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
                if (locationStatus == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    Log.w(LOG_TAG, "Location Status is OUT OF SERVICE");
                    updateLocationStatus(2);
                } else if (locationStatus == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    Log.w(LOG_TAG, "Location Status is TEMPORARILY UNAVAILABLE");
                    updateLocationStatus(2);
                } else if (locationStatus == PositioningManager.LocationStatus.AVAILABLE) {
                    Log.w(LOG_TAG, "Location Status is AVAILABLE");
                    updateLocationStatus(1);
                }
                currentLocation.setValue(PositioningManager.getInstance().getPosition().getCoordinate());
            }
        };
        positioningManager.addListener(new WeakReference<>(positionListener));
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);

        while (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
            //Log.w("OCUL - While Loop", "Location Status is TEMPORARILY UNAVAILABLE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        }

        if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
            Log.w("OCUL - Positioning", "Location Status is OUT OF SERVICE");
            positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
            updateLocationStatus(2);
        } else if (positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR) == PositioningManager.LocationStatus.AVAILABLE) {
            Log.w("OCUL - Positioning", "Location Status is AVAILABLE.");
            if (positioningManager.getPosition().getCoordinate().getLatitude() != -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() != -1.7976931348623157E308) {
                currentLocation.setValue(PositioningManager.getInstance().getPosition().getCoordinate());
                updateLocationStatus(1);
            } else {
                while (positioningManager.getPosition().getCoordinate().getLatitude() == -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() == -1.7976931348623157E308) {
                    Log.w("OCUL - Positioning", "Wrong location pulled");
                    positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                    updateLocationStatus(2);
                }


            }
        }
    }

    MutableLiveData<GeoCoordinate> getCurrentLocation() {
        return wasRepository.getCurrentLocation();
    }


    public void setWasObjectsAndMarkers() {
        databaseHelper.updateWasObjects();
    }


    public MutableLiveData<eDownloadingState> getDownloadingState() {
        return wasRepository.getDownloadingState();
    }


    public void placeMarkersOnMap() {
        //wasRepository.getMap().removeClusterLayer(wasRepository.getClusterLayer());
        if (wasRepository.getClusterLayer()!=null){
            Log.i(LOG_TAG,"Setting up markers. Marker count in cluster is: "+wasRepository.getClusterLayer().getMarkers().size());
            wasRepository.getMap().addClusterLayer(wasRepository.getClusterLayer());
        }

    }

    public void updateSelectedWasList(ViewObject object) {

        selectedWasList = new ArrayList<>();
        fullWasList = wasRepository.getWasList();
        if (wasRepository.getSelectedWasList() == null) {
            wasRepository.setSelectedWasList(new ArrayList<Was>());
        }
        if (object.getBaseType() == ViewObject.Type.PROXY_OBJECT) {

            mapProxyObject = (MapProxyObject) object;
            Log.i(LOG_TAG, "A Cluster Marker is selected");
            if (mapProxyObject.getType() == MapProxyObject.Type.CLUSTER_MARKER) {

                clusterViewObject = (ClusterViewObject) mapProxyObject;
                selectedMarkerList = new ArrayList<>(clusterViewObject.getMarkers());
                for (int i = 0; i < selectedMarkerList.size(); i++) {
                    for (int a = 0; a < wasRepository.getWasList().size(); a++) {
                        if (selectedMarkerList.get(i).getTitle().equals(fullWasList.get(a).getUniqueId())) {
                            selectedWasList.add(fullWasList.get(a));
                        }
                    }
                }
            }

        } else if (object.getBaseType() == ViewObject.Type.USER_OBJECT) {
            mapObject = (MapObject) object;

            if (mapObject.getType() == MapObject.Type.MARKER) {
                Log.i(LOG_TAG, "A single Map Marker is selected");
                selectedMarker = (MapMarker) mapObject;
                for (int i = 0; i < fullWasList.size(); i++) {
                    if (selectedMarker.getTitle().equals(fullWasList.get(i).getUniqueId())) {
                        selectedWasList.add(fullWasList.get(i));
                    }
                }
            }
        }
        wasRepository.setSelectedWasList(selectedWasList);

    }

    public void fillWasCardAdapter(WasCardAdapter adapter) {
        if (wasRepository.getSelectedWasList() != null) {
            adapter.setWasList(wasRepository.getSelectedWasList());
        } else {
            Log.e(LOG_TAG, "Selected was list is null");
        }
    }

    //Audio Management Part Start

    void playAudio(Was was) {
        Log.d(LOG_TAG, "Preparing player");
        audioHelper.preparePlayerForSelectedWas(was);
        audioHelper.startPlayingWasObject();
    }

    public void playSelectedAudio(int position) {
        if (wasRepository.getSelectedWasList().get(position) != null) {
            playAudio(wasRepository.getSelectedWasList().get(position));
        }
    }
    //Audio Management Part End
}
