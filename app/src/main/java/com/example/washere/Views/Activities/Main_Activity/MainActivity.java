package com.example.washere.Views.Activities.Main_Activity;

import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washere.R;
import com.example.washere.Views.Fragments.Main_Button_Set_Fragment.MainButtonSetFragment;
import com.example.washere.adapters.WasCardAdapter;
import com.example.washere.helpers.PermissionHelper;
import com.example.washere.models.Was;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;
import com.here.android.mpa.cluster.ClusterViewObject;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapProxyObject;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.washere.helpers.PermissionHelper.getRequestCodeAskPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WasCardAdapter.OnMarkerListener {

    private static String LOG_TAG = ("OCUL- MainActivity");
    SupportMapFragment supportMapFragment;
    Map map;
    MainActivityViewModel mainActivityViewModel;
    PermissionHelper permissionHelper;
    MainButtonSetFragment mainButtonSetFragment;
    ImageButton buttonCloseList;
    RecyclerView recyclerViewWasCard;
    WasCardAdapter wasCardAdapter;
    ArrayList<Was> mapMarkersInCluster;
    FragmentTransaction fragmentTransaction;
    Fragment previousFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnClickListeners();
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        initiateMainButtonsFragment();

        //Permission management start
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkPermissions();
        //Permission management end

        //Map initiation
        initiateMap(mainActivityViewModel.isSuccess());

        //Observers
        //Observe Changes in Current Location
        mainActivityViewModel.getCurrentLocation().observe(this, new Observer<GeoCoordinate>() {
            @Override
            public void onChanged(@Nullable GeoCoordinate geoCoordinate) {
                if(geoCoordinate!=null){
                    map=WasRepository.getInstance().getMap();
                    map.setCenter(geoCoordinate, Map.Animation.LINEAR);
                    Log.i(LOG_TAG, "Changed Current position is: " + geoCoordinate.getLatitude() + "lat " +
                            geoCoordinate.getLongitude() + " lng.");
                }
                else{
                    Log.w(LOG_TAG, "Position is null");
                }

            }
        });

        //Observe Changes in Main Activity
        mainActivityViewModel.getWasRecordingState().observe(this, new Observer<eWasUploadState>() {
            @Override
            public void onChanged(eWasUploadState state) {
                if (state != null) {
                    Log.i(LOG_TAG, "Upload State is: " + state.toString());
                    if (state == eWasUploadState.READY_TO_RECORD) {
                        if (!mainActivityViewModel.isDialogOn()) {
                            Log.i(LOG_TAG, "Starting dialog fragment");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            previousFragment = getSupportFragmentManager().findFragmentByTag("WAS_DIALOG");
                            mainActivityViewModel.initWasUploadDialog(fragmentTransaction, previousFragment);
                        } else {
                            Log.i(LOG_TAG, "Record Dialog already visible");
                        }
                    } else if (state == eWasUploadState.UPLOAD_CANCELED) {
                        mainActivityViewModel.dismissWasUploadDialog();
                    }
                }
            }
        });

        //Observe Changes in Was Items
        mainActivityViewModel.getWasList().observe(this, new Observer<List<Was>>() {
            @Override
            public void onChanged(List<Was> was) {

                placeMarkersOnMap();
            }
        });

        //Observe Changes In Selected Cluster View Objects:

        mainActivityViewModel.getSelectedClusterViewWasList().observe(this, new Observer<ArrayList<Was>>() {
            @Override
            public void onChanged(ArrayList<Was> was) {
                mapMarkersInCluster = was;
                if (was != null) {
                    recyclerViewWasCard.setVisibility(View.VISIBLE);
                    buttonCloseList.setVisibility(View.VISIBLE);
                    wasCardAdapter.setWasList(was);
                } else {
                    recyclerViewWasCard.setVisibility(View.INVISIBLE);
                    buttonCloseList.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == getRequestCodeAskPermissions()) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    // exit the app if one permission is not granted
                    return;
                }
            }
            initiateMap(mainActivityViewModel.isSuccess());

        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonCloseList) {
            recyclerViewWasCard.setVisibility(View.INVISIBLE);
            buttonCloseList.setVisibility(View.INVISIBLE);
        }

    }

    public void initiateMap(boolean success) {

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.hereMapFragment);
        // Set path of isolated disk cache
        if (!success) {
            Log.e(this.getClass().toString(), "Setting the isolated disk cache was not successful, please check if the path is valid and\n" +
                    "ensure that it does not match the default location\n" +
                    "getExternalStorageDirectory()/.here-maps).\n" +
                    "Also, ensure the provided intent name does not match the default intent name.");
        } else {
            if (supportMapFragment != null) {
                /* Initialize the SupportMapFragment, results will be given via the called back. */
                supportMapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {
                        if (error == Error.NONE) {
                            WasRepository.getInstance().setMap(supportMapFragment.getMap());
                            map=WasRepository.getInstance().getMap();
                            mainActivityViewModel.init();
                            mainActivityViewModel.onMapEngineInitialized();
                            supportMapFragment.getPositionIndicator().setVisible(true);
                            map.setCenter(mainActivityViewModel.getCurrentLocation().getValue(), Map.Animation.NONE);
                            map.setZoomLevel(16);
                            supportMapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
                                @Override
                                public void onPanStart() {

                                }

                                @Override
                                public void onPanEnd() {

                                }

                                @Override
                                public void onMultiFingerManipulationStart() {

                                }

                                @Override
                                public void onMultiFingerManipulationEnd() {

                                }

                                @Override
                                public boolean onMapObjectsSelected(List<ViewObject> list) {
                                    //TODO Shitty solutions to shitty problems... Map sees multiple clusters objects stacked. Fix it correctly
                                    boolean isClusterObject = false;
                                    for (ViewObject viewObject : list) {
                                        // Log.i(LOG_TAG,"Clicked object base type is: "+viewObject.getBaseType().toString());
                                        if (viewObject.getBaseType() == ViewObject.Type.PROXY_OBJECT) {
                                            isClusterObject = true;
                                            Log.i(LOG_TAG, "PROXY OBJECT: Clicked object base type is: " + viewObject.getBaseType().toString());
                                            MapProxyObject proxyObject = (MapProxyObject) viewObject;

                                            if (proxyObject.getType() == MapProxyObject.Type.CLUSTER_MARKER) {
                                                ClusterViewObject clusterViewObject = (ClusterViewObject) proxyObject;
                                                mainActivityViewModel.getClusterViewObject().setValue(clusterViewObject);
                                                mainActivityViewModel.setWasObjectsInCluster();
                                            }

                                        } else if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT && !isClusterObject) {
                                            Log.i(LOG_TAG, "USER OBJECT: Clicked object base type is: " + viewObject.getBaseType().toString());
                                            MapObject mapObject = (MapObject) viewObject;
                                            if (mapObject.getType() == MapObject.Type.MARKER) {
                                                mainActivityViewModel.setSelectedMarker((MapMarker) mapObject);
                                                mainActivityViewModel.setWasObjectFromSelectedMapMarker();
                                                //mainActivityViewModel.playAudio(mainActivityViewModel.getWasList().getValue(), (MapMarker) mapObject);
                                                return false;
                                            }
                                        }
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onTapEvent(PointF pointF) {
                                    return false;
                                }


                                @Override
                                public boolean onDoubleTapEvent(PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onPinchLocked() {

                                }

                                @Override
                                public boolean onPinchZoomEvent(float v, PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onRotateLocked() {

                                }

                                @Override
                                public boolean onRotateEvent(float v) {
                                    return false;
                                }

                                @Override
                                public boolean onTiltEvent(float v) {
                                    return false;
                                }

                                @Override
                                public boolean onLongPressEvent(PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onLongPressRelease() {

                                }

                                @Override
                                public boolean onTwoFingerTapEvent(PointF pointF) {
                                    return false;
                                }
                            }, 0, false);

                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                            System.out.println("ERROR: Cannot initialize Map with error " + error);
                        }
                    }
                });
            }
        }
    }

    public void initViews() {
        recyclerViewWasCard = findViewById(R.id.recyclerViewWasCard);
        buttonCloseList = findViewById(R.id.buttonCloseList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewWasCard.setLayoutManager(layoutManager);
        recyclerViewWasCard.setHasFixedSize(true);
        wasCardAdapter = new WasCardAdapter(this);
        recyclerViewWasCard.setAdapter(wasCardAdapter);
    }

    public void setOnClickListeners() {
        buttonCloseList.setOnClickListener(this);
    }

    public void initiateMainButtonsFragment() {
        mainButtonSetFragment = new MainButtonSetFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLatourButtonSet, mainButtonSetFragment).commit();
    }

    public void placeMarkersOnMap() {
        //TODO: Bu kısım daha iyi handle edilmeli. Her refreshte markerlar silinip tekrardan eklenmemeli...
        Log.i(LOG_TAG, "Marker Clusters are being added to map");
        if (map != null) {
            if (mainActivityViewModel.getExistingClusterLayer() != null) {
                map.removeClusterLayer(mainActivityViewModel.getExistingClusterLayer());
            }
            mainActivityViewModel.updateMarkerList();
            map.addClusterLayer(mainActivityViewModel.getExistingClusterLayer());
        }
    }

    @Override
    public void onWasCardClick(int position) {
        Log.d(LOG_TAG,"Clicked on was card number"+ position);
        Was wasSelected = mapMarkersInCluster.get(position);
        mainActivityViewModel.playAudio(wasSelected);
    }
}
