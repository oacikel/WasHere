package com.example.washere.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.washere.R;
import com.example.washere.helpers.PermissionHelper;
import com.example.washere.models.Was;
import com.example.washere.viewModels.MainActivityViewModel;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.util.List;

import static com.example.washere.helpers.PermissionHelper.getRequestCodeAskPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    SupportMapFragment supportMapFragment;
    Map map;
    MainActivityViewModel mainActivityViewModel;
    PermissionHelper permissionHelper;
    Button buttonUpdateMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setOnClickListeners();
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        //mainActivityViewModel.init();

        //Permission management start
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkPermissions();
        //Permission management end

        //Map initiation Start
        initiateMap(mainActivityViewModel.isSuccess());
        //Map initiation End

        //Observe Changes in Current Location Start
        mainActivityViewModel.getCurrentLocation().observe(this, new Observer<GeoCoordinate>() {
            @Override
            public void onChanged(@Nullable GeoCoordinate geoCoordinate) {
                map.setCenter(geoCoordinate, Map.Animation.BOW);
                supportMapFragment.getPositionIndicator().setVisible(true);
            }
        });
        //Observe Changes in Current Location End

        //Observe Changes in Was Items Start
        mainActivityViewModel.getWasList().observe(this, new Observer<List<Was>>() {
            @Override
            public void onChanged(@Nullable List<Was> was) {
                //TODO: Her keresinde yaratılan markerList aslında başka bir liste olduğu için üst üste marker binmiş gibi gözüküyor. Update marker diye bir method çağırılmalı.
                map.removeMapObjects(mainActivityViewModel.getMarkerList());
                System.out.println("Marker count to be cleared is: "+mainActivityViewModel.getMarkerList().size());
                mainActivityViewModel.updateMarkerList();
                System.out.println("Marker count to be put is: "+mainActivityViewModel.getMarkerList().size());
                map.addMapObjects(mainActivityViewModel.getMarkerList());
            }
        });
        //Observe Changes in Was Items End

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
        if (v == buttonUpdateMarkers) {
            mainActivityViewModel.addAnotherWasItem();
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
                            // retrieve a reference of the map from the map fragment
                            map = supportMapFragment.getMap();
                            mainActivityViewModel.init();
                            mainActivityViewModel.onMapEngineInitialized();
                            map.setZoomLevel(16);
                            //map.addMapObjects(mainActivityViewModel.updateMarkerList());
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
                                    for (ViewObject viewObject : list) {
                                        System.out.println(list.toString());
                                        if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                            MapObject mapObject = (MapObject) viewObject;

                                            if (mapObject.getType() == MapObject.Type.MARKER) {

                                                MapMarker window_marker = ((MapMarker) mapObject);

                                                System.out.println("Laitude is................." + window_marker.getCoordinate().getLatitude());
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
        buttonUpdateMarkers = findViewById(R.id.buttonUpdateMarkers);
    }

    public void setOnClickListeners() {
        buttonUpdateMarkers.setOnClickListener(this);
    }

}
