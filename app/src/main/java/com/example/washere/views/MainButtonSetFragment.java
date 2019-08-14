package com.example.washere.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.example.washere.viewModels.MainButtonSetFragmentViewModel;

import java.util.List;

public class MainButtonSetFragment extends Fragment implements View.OnTouchListener {

    private Button buttonRecordAudio;
    MainButtonSetFragmentViewModel mainButtonSetFragmentViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_main_button_set, container, false);
        initViews(view);
        setButtonListeners();
        final MainActivity activity=(MainActivity) getActivity();

        mainButtonSetFragmentViewModel = ViewModelProviders.of(this).get(MainButtonSetFragmentViewModel.class);
        mainButtonSetFragmentViewModel.init();

        mainButtonSetFragmentViewModel.getWasList().observe(getViewLifecycleOwner(), new Observer<List<Was>>() {
            @Override
            public void onChanged(@Nullable List<Was> was) {
                //TODO: Repository'deki waslist update olduğunda. Main activity'nin de "getWasList" metodu  çağırılmalı. Ancak çağırılmıyor. Çözüm bulmak gerekiyor.
                System.out.println("OCUL: New was item added from fragment side, changes should occur in main activity.");
                activity.placeMarkersOnMap();

            }
        });



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initViews(View view) {
        buttonRecordAudio = view.findViewById(R.id.buttonRecordAudio);
    }

    private void setButtonListeners() {
        buttonRecordAudio.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == buttonRecordAudio) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainButtonSetFragmentViewModel.startRecordingWasItem();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                System.out.println("Button released");
                mainButtonSetFragmentViewModel.addWasItemAfterRecording();
                int order = mainButtonSetFragmentViewModel.getWasList().getValue().size() - 1;

            }
        }
        return true;
    }
}
