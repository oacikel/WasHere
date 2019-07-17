package com.example.washere;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.MapSettings;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.PlaceLink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WHMapFragment extends Fragment {
    /*public static List<DiscoveryResult> s_ResultList = new ArrayList<>();*/
    private AppCompatActivity activity;
    private PositioningManager positioningManager;
    private PositioningManager.OnPositionChangedListener positionListener;
    private PositioningManager.OnPositionChangedListener positionListenerSecond;
    private GeoCoordinate currentPosition = null;
    private MapEngine mapEngine = MapEngine.getInstance();
    private com.here.android.mpa.mapping.MapFragment mapFragmentView;
    private static View v;
    private SupportMapFragment mapFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ImageButton buttonCenterLocation;
    private Button buttonStartNavigation;

    //private ApplicationContext appContext = new ApplicationContext(getContext());

//    MapFragmentView mapFragmentView =new MapFragmentView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView Çağrıldı");
        v = inflater.inflate(R.layout.map_fragment, container, false);
        initMapFragment();


        return v;
    }

    private void setCenterToMyLocation() {
    }

    public void setPositionListener(PositioningManager.OnPositionChangedListener positionListener) {
        this.positionListener = positionListener;
    }

    public PositioningManager.OnPositionChangedListener getPositionListener() {
        return positionListener;
    }

    public WHMapFragment() {
    }

    public PositioningManager getPositioningManager() {
        return positioningManager;
    }

    public void setPositioningManager(PositioningManager positioningManager) {
        this.positioningManager = positioningManager;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    private Map map;
    private List<MapObject> m_mapObjectList = new ArrayList<>();


    public void initMapFragment() {

        /* Locate the mapFragment UI element */

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        boolean success = MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        System.out.println("OCUL: "+intentName+" & "+diskCacheRoot);
        if (!success) {
            System.out.println("OCUL: "+intentName+" & "+diskCacheRoot+" ");
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {

            mapFragmentView = new com.here.android.mpa.mapping.MapFragment();
            fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutForMapFragment, mapFragmentView).commit();

            //Here cinsinden ApplicationContext yarattık aşağıda:
            com.here.android.mpa.common.ApplicationContext newContext = new ApplicationContext(activity);
            //O yarttığımız contexti MapFragmentView.init methodu için kullandık (Mine de vardı ;) ):
            mapFragmentView.init(newContext, new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {

                    if (error == Error.NONE) {
                        //Manage Position Managers
                        positioningManager = PositioningManager.getInstance();
                        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);

                        //Manage Viewing the Map
                        map = mapFragmentView.getMap();
                        map.setZoomLevel(13.2);
                        //Set map center as devices Current Location and put an Indicator
                        GeoCoordinate myCurrentPosition = positioningManager.getPosition().getCoordinate();
                        map.setCenter(myCurrentPosition, Map.Animation.NONE);
                        map.getPositionIndicator().setVisible(true);
                        System.out.println("My current position is: " + myCurrentPosition.toString());



                    } else {
                        Toast.makeText(activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }

                    //TODO: Bu kısım pozisyon değiştiğinde haritayı cihaz merkezine zoomlamak için yapıldı. Daha sonra kullanılacak.
                    /*positionListener = new PositioningManager.OnPositionChangedListener() {
                        @Override
                        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
                            currentPosition = positioningManager.getPosition().getCoordinate();
                            map.setCenter(position.getCoordinate(), Map.Animation.NONE);
                            System.out.println("onPositionUpdated override oldu hayırlı olsun");
                        }

                        @Override
                        public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {
                            try {
                                positioningManager.addListener(new WeakReference<>(positionListener));

                                if (!positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)) {
                                    Log.e("HERE", "PositioningManager.start: Failed to start...");
                                } else {
                                    System.out.println("selam");
                                    currentPosition = positioningManager.getPosition().getCoordinate();
                                    map.setCenter(currentPosition, Map.Animation.NONE);
                                }
                            } catch (Exception e) {
                                Log.e("HERE", "Caught: " + e.getMessage());
                            }
                            map.getPositionIndicator().setVisible(true);
                        }
                    };
                    positioningManager.addListener(new WeakReference<>(positionListener));
*/
                }
            });
        }
    }


    public void addMarkerAtPlace(PlaceLink placeLink) {
        Image img = new Image();
        try {
            img.setImageResource(R.drawable.place_holder_icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapMarker mapMarker = new MapMarker();
        mapMarker.setIcon(img);
        mapMarker.setCoordinate(new GeoCoordinate(placeLink.getPosition()));
        map.addMapObject(mapMarker);
        m_mapObjectList.add(mapMarker);
    }

    @Override
    public void onAttach(Activity activitytoConvert) {
        super.onAttach(activity);

        try {
            activity = (AppCompatActivity) activitytoConvert;
        } catch (ClassCastException e) {
        }
    }

    public void cleanMap() {
        if (!m_mapObjectList.isEmpty()) {
            map.removeMapObjects(m_mapObjectList);
            m_mapObjectList.clear();
        }

    }

}
