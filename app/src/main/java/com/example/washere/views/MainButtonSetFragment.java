package com.example.washere.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.washere.R;
import com.example.washere.viewModels.MainButtonSetFragmentViewModel;

public class MainButtonSetFragment extends Fragment implements View.OnTouchListener {

    private Button buttonRecordAudio;
    MainButtonSetFragmentViewModel mainButtonSetFragmentViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainButtonSetFragmentViewModel=ViewModelProviders.of(this.getActivity()).get(MainButtonSetFragmentViewModel.class);
        mainButtonSetFragmentViewModel.init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view=inflater.inflate(R.layout.fragment_main_button_set,container,false);
        initViews(view);
        setButtonListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initViews(View view){
        buttonRecordAudio=view.findViewById(R.id.buttonRecordAudio);
    }

    private void setButtonListeners(){
        buttonRecordAudio.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v==buttonRecordAudio){
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                mainButtonSetFragmentViewModel.startRecordingWasItem();
            }
            else if(event.getAction()==MotionEvent.ACTION_UP){
                mainButtonSetFragmentViewModel.addWasItemAfterRecording();
            }
        }
        return true;
    }
}
