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

    public static WasRepository getInstance(){
        if(instance == null){
            instance = new WasRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Was>> getWasList(){
        setWasItems();
        MutableLiveData<List<Was>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }


    //Pretend to retrieve the data from a database
    private void setWasItems(){
        dataSet.add(
                new Was(R.raw.tekerleme01,"/storage/emulated/0/VoiceRecorder/my_sounds/tekerleme01.3gp","tekerleme01.3gp",40.975513, 29.233847, 0.0, R.drawable.place_holder_icon) //Sancaktepe 01
        );

        dataSet.add(
                new Was(R.raw.tekerleme02,"","",40.976403, 29.229676, 0.0, R.drawable.place_holder_icon) //Sancaktepe 02
        );
    }

}
