package com.example.washere.views;

import android.Manifest;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.washere.R;
import com.example.washere.helpers.PermissionHelper;
import com.example.washere.viewModels.MainActivityViewModel;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.washere.helpers.PermissionHelper.getRequestCodeAskPermissions;

public class MainActivity extends AppCompatActivity {


    SupportMapFragment supportMapFragment;
    Map map;
    MainActivityViewModel mainActivityViewModel;
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init();

        //Permission management start
        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkPermissions();
        //Permission management end

        //Map initiation Start
        initiateMap(mainActivityViewModel.isSuccess());
        mainActivityViewModel.getCurrentLocation().observe(this, new Observer<GeoCoordinate>() {
            @Override
            public void onChanged(@Nullable GeoCoordinate geoCoordinate) {
                map.setCenter(geoCoordinate, Map.Animation.LINEAR);
                supportMapFragment.getPositionIndicator().setVisible(true);
            }
        });
        //Map initiation End

        //Add Markers of Was Elements Start

        //Add Markers of Was Elements End
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
                            mainActivityViewModel.onMapEngineInitialized();
                            map.setZoomLevel(15);
                            map.addMapObjects(mainActivityViewModel.getWasMapMarkers());
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
}
