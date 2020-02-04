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
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.Views.Dialogs.Record_WAS_Dialog.RecordWasDialog;
import ocul.longestlovestoryever.washere.adapters.WasCardAdapter;
import ocul.longestlovestoryever.washere.helpers.AudioHelper;
import ocul.longestlovestoryever.washere.helpers.DatabaseHelper;
import ocul.longestlovestoryever.washere.helpers.MapHelper;
import ocul.longestlovestoryever.washere.models.CONSTANTS;
import ocul.longestlovestoryever.washere.models.Was;
import ocul.longestlovestoryever.washere.models.eDownloadingState;
import ocul.longestlovestoryever.washere.models.eFollowState;
import ocul.longestlovestoryever.washere.models.eLocationStatus;
import ocul.longestlovestoryever.washere.models.ePermissionStatus;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.models.eViewMode;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapProxyObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MapActivityViewModel extends AndroidViewModel {
    private static String LOG_TAG = ("OCUL- MainActivityViewModel");
    private String intentName;
    private Context context;
    private MapHelper mapHelper = new MapHelper();
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
    private Image withinRadiusMarker, outsideDistanceMarker;

    public MapActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    //Location Status
    public MutableLiveData<eLocationStatus> getLocationStatus() {
        return wasRepository.getLocationStatus();
    }

    private void updateLocationStatus(eLocationStatus status) {
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

    public void updateFollowState(eFollowState state) {
        wasRepository.setUpdateFollowState(state);
    }

    public MutableLiveData<eFollowState> getFollowState() {
        return wasRepository.getFollowState();
    }

    void onMapEngineInitialized() {
        positioningManager = PositioningManager.getInstance();
        PositioningManager.OnPositionChangedListener positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                Log.w(LOG_TAG,"Position changed");
                wasRepository.setUpdateCurrentLocation(geoPosition.getCoordinate());
            }

            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
                Log.w(LOG_TAG,"Position fix changed");
                mapHelper.managePositionStatus(positioningManager, locationStatus);
                wasRepository.setUpdateCurrentLocation(positioningManager.getPosition().getCoordinate());
            }
        };
        positioningManager.addListener(new WeakReference<>(positionListener));
        startPositionManager();
        managePositionStatus();
    }

    public void startPositionManager(){
        mapHelper.startPositionManager(positioningManager);
    }

    public void initiateCurrentLocation(){
        wasRepository.setUpdateCurrentLocation(positioningManager.getPosition().getCoordinate());
    }

    public void managePositionStatus(){
        mapHelper.managePositionStatus(positioningManager, positioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR));
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
        if (wasRepository.getViewMode() == eViewMode.VIEW_ALL) {
            if (wasRepository.getAllClusterLayer() != null) {
                Log.i(LOG_TAG, "Setting up markers. Marker count in cluster is: " + wasRepository.getAllClusterLayer().getMarkers().size());
                wasRepository.getMap().addClusterLayer(wasRepository.getAllClusterLayer());
            }
        } else if (wasRepository.getViewMode() == eViewMode.VIEW_MINE) {
            if (wasRepository.getMyClusterLayer() != null) {
                Log.i(LOG_TAG, "Setting up  my markers. My Marker count in cluster is: " + wasRepository.getMyClusterLayer().getMarkers().size());
                wasRepository.getMap().addClusterLayer(wasRepository.getMyClusterLayer());
            }
        }
    }

    public void clearUserWasList() {
        if (wasRepository.getMyClusterLayer() != null) {
            wasRepository.getMap().removeClusterLayer(wasRepository.getMyClusterLayer());
        }
    }

    public void clearAllWasList() {
        if (wasRepository.getAllClusterLayer() != null) {
            wasRepository.getMap().removeClusterLayer(wasRepository.getAllClusterLayer());
        }
    }

    public void changeViewMode(eViewMode mode) {
        wasRepository.setViewMode(mode);
    }

    public MutableLiveData<ePermissionStatus> getPermissionStatus() {
        return wasRepository.getPermissionStatus();
    }

    public void updatePermissionStatus(ePermissionStatus status) {
        wasRepository.setUpdatePermissionStatus(status);
    }

    public void updateSelectedWasList(ViewObject object) {

        selectedWasList = new ArrayList<>();
        fullWasList = wasRepository.getAllWasList();
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
                    for (int a = 0; a < wasRepository.getAllWasList().size(); a++) {
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

    public void saveLastKnownLocation() {
        if (wasRepository.getCurrentLocation().getValue() != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(CONSTANTS.LAST_KNOWN_LAT, Double.doubleToRawLongBits(wasRepository.getCurrentLocation().getValue().getLatitude())).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(CONSTANTS.LAST_KNOWN_LNG, Double.doubleToRawLongBits(wasRepository.getCurrentLocation().getValue().getLongitude())).apply();
        }
    }

    public void setMapCenterToLastKnownLocation(Map map) {
        GeoCoordinate lastKnownLocation = null;
        double lastKnownLat = Double.longBitsToDouble(PreferenceManager.getDefaultSharedPreferences(context).getLong(CONSTANTS.LAST_KNOWN_LAT, 0));
        double lastKnownLng = Double.longBitsToDouble(PreferenceManager.getDefaultSharedPreferences(context).getLong(CONSTANTS.LAST_KNOWN_LNG, 0));
        if (lastKnownLat != 0 && lastKnownLng != 0) {
            lastKnownLocation = new GeoCoordinate(lastKnownLat, lastKnownLng);
        }
        if (lastKnownLocation != null) {
            wasRepository.getSupportMapFragment().getMap().setCenter(lastKnownLocation, Map.Animation.LINEAR);
        } else {
            map.setCenter(positioningManager.getLastKnownPosition().getCoordinate(), Map.Animation.LINEAR);
        }
    }

    public void setMapCenterToCurrentLocation() {
        if (wasRepository.getMap() != null && wasRepository.getCurrentLocation().getValue() != null) {
            wasRepository.getMap().setCenter(wasRepository.getCurrentLocation().getValue(), Map.Animation.LINEAR);
        }
    }

    public eViewMode getViewMode() {
        return wasRepository.getViewMode();
    }

    public void changeMapMarkersAccordingToDistance(GeoCoordinate currentLocation) {
        if (withinRadiusMarker == null) {
            withinRadiusMarker = new Image();
            try {
                withinRadiusMarker.setImageResource(R.drawable.place_holder_icon);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
        }
        if (outsideDistanceMarker == null) {
            outsideDistanceMarker = new Image();
            try {
                outsideDistanceMarker.setImageResource(R.drawable.outside_range_icon);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in setting image Resource: " + e.getMessage());
            }
        }

        for (MapMarker marker : wasRepository.getAllClusterLayer().getMarkers()) {
            if (marker.getCoordinate().distanceTo(currentLocation) <= CONSTANTS.VIEW_DISTANCE) {
                Log.i(LOG_TAG, "Marker is within viewing distance.");
                marker.setIcon(withinRadiusMarker);
            } else {
                marker.setIcon(outsideDistanceMarker);
                Log.i(LOG_TAG, "Marker is outside the viewing distance.");
            }
        }

    }

}
