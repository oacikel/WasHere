package com.example.washere.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.support.v7.app.AppCompatActivity;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.MapMarker;

import java.util.ArrayList;
import java.util.List;

public class WasRepository {

    private static WasRepository instance;
    private ArrayList<Was> dataSet = new ArrayList<>();
    private int count = 0;
    private GeoCoordinate currentLocation;

    public static WasRepository getInstance() {
        if (instance == null) {
            instance = new WasRepository();
        }
        return instance;
    }

    public GeoCoordinate getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoCoordinate currentLocation) {
        this.currentLocation = currentLocation;
    }

    public MutableLiveData<List<Was>> getWasList() {
        //setWasItems();
        MutableLiveData<List<Was>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addWasItem(Was was) {
        dataSet.add(was);
    }

    //Pretend to retrieve the data from a database
    private void setWasItems() {
        dataSet.add(
                new Was(0, R.raw.tekerleme01, "", "", 40.975513, 29.233847, 0.0, R.drawable.place_holder_icon) //Sancaktepe 01
        );
        count++;
        dataSet.add(
                new Was(1, R.raw.tekerleme02, "", "", 40.976403, 29.229676, 0.0, R.drawable.place_holder_icon) //Sancaktepe 02
        );
        count++;
    }

}
