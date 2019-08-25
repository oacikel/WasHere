package com.example.washere.views;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.example.washere.viewModels.MainButtonSetFragmentViewModel;

import java.sql.SQLOutput;
import java.util.List;

public class MainButtonSetFragment extends Fragment implements View.OnTouchListener {

    private Button buttonRecordAudio;
    MainButtonSetFragmentViewModel mainButtonSetFragmentViewModel;
    MainActivity activity;

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
        activity = (MainActivity) getActivity();

        mainButtonSetFragmentViewModel = ViewModelProviders.of(this).get(MainButtonSetFragmentViewModel.class);
        mainButtonSetFragmentViewModel.init();
        mainButtonSetFragmentViewModel.getDownloadUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String url) {
                System.out.println("Ocul: url değişti");
                mainButtonSetFragmentViewModel.addWasHashMapToFireStore(url);
                System.out.println(url);
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
                mainButtonSetFragmentViewModel.addWasItemAfterRecording();

            }
        }
        return true;
    }
}
