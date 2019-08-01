package com.example.washere.viewModels;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.washere.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.io.File;
import java.lang.ref.WeakReference;

public class MapViewModel {
    private SupportMapFragment m_mapFragment;
    private Map m_map;

    private MapMarker m_positionIndicatorFixed = null;
    private PointF m_mapTransformCenter;
    private boolean m_returningToRoadViewMode = false;
    private double m_lastZoomLevelInRoadViewMode = 0.0;
    private AppCompatActivity m_activity;

    public MapViewModel(AppCompatActivity activity) {
        m_activity = activity;
        initMapFragment();
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.hereMapFragment);
    }

    private void initMapFragment() {
        m_mapFragment = getMapFragment();
        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (m_mapFragment != null) {

                /* Initialize the SupportMapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        if (error == OnEngineInitListener.Error.NONE) {
                            // retrieve a reference of the map from the map fragment
                            m_map = m_mapFragment.getMap();
                            m_map.setZoomLevel(19);
                            m_map.addTransformListener(onTransformListener);

                        } else {
                            Toast.makeText(m_activity,
                                    "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }


        m_mapFragment.addOnMapRenderListener(new OnMapRenderListener() {
            @Override
            public void onPreDraw() {
                if (m_positionIndicatorFixed != null) {
                    if (NavigationManager.getInstance()
                            .getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
                        if (!m_returningToRoadViewMode) {
                            // when road view is active, we set the position indicator to align
                            // with the current map transform center to synchronize map and map
                            // marker movements.
                            m_positionIndicatorFixed.setCoordinate(m_map.pixelToGeo(m_mapTransformCenter));
                        }
                    }
                }
            }

            @Override
            public void onPostDraw(boolean b, long l) {

            }

            @Override
            public void onSizeChanged(int i, int i1) {

            }

            @Override
            public void onGraphicsDetached() {

            }

            @Override
            public void onRenderBufferCreated() {

            }
        });

    }

    private void resumeRoadView() {
        // move map back to it's current position.
        m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(), Map
                        .Animation.BOW, m_lastZoomLevelInRoadViewMode, Map.MOVE_PRESERVE_ORIENTATION,
                80);
        // do not start RoadView and its listener until the map movement is complete.
        m_returningToRoadViewMode = true;
    }

    // application design suggestion: pause roadview when user gesture is detected.

    final private NavigationManager.RoadView.Listener roadViewListener = new NavigationManager.RoadView.Listener() {
        @Override
        public void onPositionChanged(GeoCoordinate geoCoordinate) {
            // an active RoadView provides coordinates that is the map transform center of it's
            // movements.
            m_mapTransformCenter = m_map.projectToPixel
                    (geoCoordinate).getResult();
        }
    };

    final private Map.OnTransformListener onTransformListener = new Map.OnTransformListener() {
        @Override
        public void onMapTransformStart() {
        }

        @Override
        public void onMapTransformEnd(MapState mapsState) {
            // do not start RoadView and its listener until moving map to current position has
            // completed
            if (m_returningToRoadViewMode) {
                NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode
                        .ROADVIEW);
                NavigationManager.getInstance().getRoadView().addListener(new
                        WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));
                m_returningToRoadViewMode = false;
            }
        }

    };

    public void onDestroy() {
        m_map.removeMapObject(m_positionIndicatorFixed);
        NavigationManager.getInstance().stop();
        PositioningManager.getInstance().stop();
    }

    public void onBackPressed() {
        if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager
                .MapUpdateMode.NONE)) {
            resumeRoadView();
        } else {
            m_activity.finish();
        }
    }
}