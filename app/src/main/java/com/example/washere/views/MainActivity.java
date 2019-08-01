package com.example.washere.views;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.washere.R;
import com.example.washere.viewModels.MainActivityViewModel;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;

public class MainActivity extends AppCompatActivity {


    SupportMapFragment supportMapFragment;
    Map map;
    MainActivityViewModel mainActivityViewModel;

    private boolean isReturningToRoadView = false;
    private PointF m_mapTransformCenter;
    private MapMarker m_positionIndicatorFixed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        managePermissions(); //Check required permissions
        initiateMap(mainActivityViewModel.isSuccess());
    }

    private void managePermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            System.out.println("Map Fragment kontrol ediliyor");
            if (supportMapFragment != null) {
                System.out.println("Map fragment başlayacak Null değilmiş.");
                /* Initialize the SupportMapFragment, results will be given via the called back. */
                supportMapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {
                        if (error == Error.NONE) {
                            // retrieve a reference of the map from the map fragment
                            map = supportMapFragment.getMap();
                            map.setZoomLevel(19);
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
