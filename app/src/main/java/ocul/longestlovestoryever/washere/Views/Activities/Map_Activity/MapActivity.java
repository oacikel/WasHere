package ocul.longestlovestoryever.washere.Views.Activities.Map_Activity;

import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.Views.Fragments.Main_Button_Set_Fragment.MainButtonSetFragment;
import ocul.longestlovestoryever.washere.adapters.WasCardAdapter;
import ocul.longestlovestoryever.washere.helpers.PermissionHelper;
import ocul.longestlovestoryever.washere.models.eDownloadingState;
import ocul.longestlovestoryever.washere.models.eFollowState;
import ocul.longestlovestoryever.washere.models.ePermissionStatus;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, WasCardAdapter.OnMarkerListener {

    private static String LOG_TAG = ("OCUL- Map Activity");
    SupportMapFragment supportMapFragment;
    Map map;
    MapActivityViewModel mapActivityViewModel;
    PermissionHelper permissionHelper;
    MainButtonSetFragment mainButtonSetFragment;
    RecyclerView recyclerViewWasCard;
    WasCardAdapter wasCardAdapter;
    FragmentTransaction fragmentTransaction;
    Fragment previousFragment;
    FloatingActionButton floatingActionButtonFollowMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Main Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnClickListeners();
        mapActivityViewModel = ViewModelProviders.of(this).get(MapActivityViewModel.class);
        initiateMainButtonsFragment();
        //Permission management start
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkPermissions();
        //Permission management end

        //Map initiation
        //initiateMap(mapActivityViewModel.isSuccess());

        //Observers
        //Observe Changes in Current Location
        mapActivityViewModel.getCurrentLocation().observe(this, new Observer<GeoCoordinate>() {
            @Override
            public void onChanged(@Nullable GeoCoordinate geoCoordinate) {
                if (geoCoordinate != null && mapActivityViewModel.getFollowState().getValue() == eFollowState.FOLLOW_USER) {
                    map = WasRepository.getInstance().getMap();
                    map.setCenter(geoCoordinate, Map.Animation.LINEAR);
                    Log.w(LOG_TAG, "Changed position");
                }
            }
        });

        //Observe Changes in Main Activity
        mapActivityViewModel.getWasRecordingState().observe(this, new Observer<eRecordState>() {
            @Override
            public void onChanged(eRecordState state) {
                if (state != null) {
                    Log.i(LOG_TAG, "Upload State is: " + state.toString());
                    if (state == eRecordState.READY_TO_RECORD) {
                        if (!mapActivityViewModel.isDialogOn()) {
                            Log.i(LOG_TAG, "Starting dialog fragment");
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            previousFragment = getSupportFragmentManager().findFragmentByTag("WAS_DIALOG");
                            mapActivityViewModel.initWasUploadDialog(fragmentTransaction, previousFragment);
                        } else {
                            Log.i(LOG_TAG, "Record Dialog already visible");
                        }
                    }
                }
            }
        });

        //Observe Changes In Permission Status
        mapActivityViewModel.getPermissionStatus().observe(this, new Observer<ePermissionStatus>() {
            @Override
            public void onChanged(ePermissionStatus ePermissionStatus) {
                if (ePermissionStatus == ocul.longestlovestoryever.washere.models.ePermissionStatus.PERMISSONS_GRANTED) {
                    initiateMap(mapActivityViewModel.isSuccess());
                }
            }
        });

        //Observe Changes in Was Items
        mapActivityViewModel.getDownloadingState().observe(this, new Observer<eDownloadingState>() {
            @Override
            public void onChanged(eDownloadingState eDownloadingState) {
                if (eDownloadingState == eDownloadingState.WAS_ITEM_ADDED) {
                    Log.i(LOG_TAG, "Adding markers and/or clusters on map");
                    mapActivityViewModel.placeMarkersOnMap();

                } else if (eDownloadingState == eDownloadingState.WAS_ITEM_CHANGED) {

                } else if (eDownloadingState == eDownloadingState.WAS_ITEM_REMOVED) {

                }
            }
        });

        //Observe Follow State
        mapActivityViewModel.getFollowState().observe(this, new Observer<eFollowState>() {
            @Override
            public void onChanged(eFollowState eFollowState) {
                if (eFollowState == ocul.longestlovestoryever.washere.models.eFollowState.FREE_ROAM) {
                    floatingActionButtonFollowMode.setImageResource(R.drawable.icon_free_roam);
                } else if (eFollowState == ocul.longestlovestoryever.washere.models.eFollowState.FOLLOW_USER) {
                    floatingActionButtonFollowMode.setImageResource(R.drawable.icon_follow_user);
                    mapActivityViewModel.setMapCenterToCurrentLocation();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(LOG_TAG, "Activity has entered on stop method");
        //mainActivityViewModel.onStopEvent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.getRequestCodeAskPermissions()) {
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    Log.w(LOG_TAG, "Permissions are not granted");
                    mapActivityViewModel.updatePermissionStatus(ePermissionStatus.PERMISSONS_GRANTED);
                    // exit the app if one permission is not granted
                    return;
                } else {
                    Log.w(LOG_TAG, "Permissions are granted");
                    mapActivityViewModel.updatePermissionStatus(ePermissionStatus.PERMISSONS_GRANTED);
                }
            }


        }
    }

    @Override
    public void onClick(View v) {
        if (v == floatingActionButtonFollowMode) {

            mapActivityViewModel.updateFollowState(eFollowState.FOLLOW_USER);


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
                            Log.i(LOG_TAG, "Map Engine initialized");
                            if (WasRepository.getInstance().getMap() == null) {
                                WasRepository.getInstance().setMap(supportMapFragment.getMap());
                                WasRepository.getInstance().setSupportMapFragment(supportMapFragment);
                                Log.i(LOG_TAG, "Map is null");
                            }
                            map = WasRepository.getInstance().getMap();
                            mapActivityViewModel.setWasObjectsAndMarkers();
                            supportMapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
                                @Override
                                public void onPanStart() {
                                    mapActivityViewModel.updateFollowState(eFollowState.FREE_ROAM);
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
                                    for (ViewObject viewObject : list) {
                                        mapActivityViewModel.updateSelectedWasList(viewObject);
                                        mapActivityViewModel.fillWasCardAdapter(wasCardAdapter);
                                        recyclerViewWasCard.setVisibility(View.VISIBLE);
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onTapEvent(PointF pointF) {
                                    recyclerViewWasCard.setVisibility(View.GONE);
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
                            mapActivityViewModel.onMapEngineInitialized();
                            supportMapFragment.getPositionIndicator().setVisible(true);
                            WasRepository.getInstance().setMap(map);
                            mapActivityViewModel.placeMarkersOnMap();
                            map.setZoomLevel(16);
                            mapActivityViewModel.setMapCenterToLastKnownLocation(map);
                            map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(), Map.Animation.LINEAR);
                            mapActivityViewModel.updateFollowState(eFollowState.FOLLOW_USER);
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG, "Cannot initialize Map with error " + error);
                        }
                    }
                });
            }
        }


    }

    public void initViews() {
        recyclerViewWasCard = findViewById(R.id.recyclerViewWasCard);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewWasCard.setLayoutManager(layoutManager);
        recyclerViewWasCard.setHasFixedSize(true);
        wasCardAdapter = new WasCardAdapter(this);
        recyclerViewWasCard.setAdapter(wasCardAdapter);
        floatingActionButtonFollowMode = findViewById(R.id.floatingActionButtonFollowMode);
    }

    public void setOnClickListeners() {
        floatingActionButtonFollowMode.setOnClickListener(this);
    }

    public void initiateMainButtonsFragment() {
        mainButtonSetFragment = new MainButtonSetFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLatourButtonSet, mainButtonSetFragment).commit();
    }

    @Override
    public void onWasCardClick(int position) {
        Log.d(LOG_TAG, "Clicked on was card number" + position);
        mapActivityViewModel.playSelectedAudio(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapActivityViewModel.saveLastKnownLocation();
    }
}
