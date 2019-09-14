package com.example.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.washere.R;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

public class RecordWasDialog extends DialogFragment implements View.OnClickListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private String TAG =("OCUL - Record Dialog");
    RecordWasDialogViewModel recordWasDialogViewModel;
    ImageButton imageButtonControlRecording, imageButtonSend, imageButtonDiscardRecording,imageButtonRetry;
    ProgressBar progressBarRemainingTime;
    TextView editTextWasTitle;
    ValueAnimator animator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_record_was, container, false);
        setCancelable(true);
        initViews(view);
        initOtherObjects();
        setOnClickListeners();


        //Observers
        //Observe Was Recording / Playing state
        recordWasDialogViewModel.getWasRecordingState().observe(this, new Observer<eWasUploadState>() {
            @Override
            public void onChanged(eWasUploadState state) {
                if (state != null) {
                    if (state == eWasUploadState.READY_TO_RECORD) {
                        imageButtonRetry.setVisibility(View.INVISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_record);
                        progressBarRemainingTime.setVisibility(View.INVISIBLE);

                    } else if (state == eWasUploadState.RECORDING) {
                        imageButtonRetry.setVisibility(View.INVISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_stop);
                        //Start Recording
                        recordWasDialogViewModel.recordAudio();
                        recordWasDialogViewModel.setUploadLocation();

                        //Animate the progress bar
                        progressBarRemainingTime.setVisibility(View.VISIBLE);
                        animator.start();

                    } else if (state == eWasUploadState.FINISHED_RECORDING) {
                        //Prepare the recorder audio file to listen
                        recordWasDialogViewModel.prepareMediaPlayer();
                    } else if (state == eWasUploadState.READY_TO_PLAY) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                    } else if (state == eWasUploadState.PLAYING) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_pause);
                    } else if (state == eWasUploadState.PAUSED) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                    } else if (state == eWasUploadState.FINISHED_PLAYING) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == imageButtonControlRecording) {
            eWasUploadState state = WasRepository.getInstance().getWasRecordingState().getValue();

            if (state == eWasUploadState.READY_TO_RECORD) {
                //Change state to RECORDING
                recordWasDialogViewModel.updateWasRecordingState(eWasUploadState.RECORDING);

            } else if (state == eWasUploadState.RECORDING) {
                Log.d(TAG,"Stop recording button pressed");
                //Stop animating the Progress Bar
                animator.cancel();
                progressBarRemainingTime.setProgress(progressBarRemainingTime.getMax());
                //Stop Recording
                recordWasDialogViewModel.stopRecording();
                recordWasDialogViewModel.updateWasRecordingState(eWasUploadState.FINISHED_RECORDING);


            } else if (state == eWasUploadState.FINISHED_RECORDING) {

            } else if (state == eWasUploadState.READY_TO_PLAY) {
                recordWasDialogViewModel.playAudio();
            } else if (state == eWasUploadState.PLAYING) {

            } else if (state == eWasUploadState.PAUSED) {

            } else if (state == eWasUploadState.FINISHED_PLAYING) {

            }

        }
        else if(view==imageButtonRetry){
            recordWasDialogViewModel.prepareRecorder();
        }
        else if (view==imageButtonDiscardRecording){

        }

    }

    public void initViews(View view) {
        imageButtonControlRecording = view.findViewById(R.id.imageButtonControlRecording);
        imageButtonSend = view.findViewById(R.id.imageButtonSend);
        imageButtonDiscardRecording = view.findViewById(R.id.imageButtonDiscardRecording);
        imageButtonRetry=view.findViewById(R.id.imageButtonRetry);
        progressBarRemainingTime = view.findViewById(R.id.progressBarRemainingTime);
        editTextWasTitle = view.findViewById(R.id.editTextWasTitle);
    }

    public void setOnClickListeners() {
        imageButtonDiscardRecording.setOnClickListener(this);
        imageButtonSend.setOnClickListener(this);
        imageButtonControlRecording.setOnClickListener(this);
        imageButtonRetry.setOnClickListener(this);
    }

    public void initOtherObjects() {
        recordWasDialogViewModel = ViewModelProviders.of(this).get(RecordWasDialogViewModel.class);
        animator = recordWasDialogViewModel.getAnimator();
        animator.addUpdateListener(this);
        animator.addListener(this);
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Log.d(TAG,"Animation ended");
        //recordWasDialogViewModel.updateWasRecordingState(eWasUploadState.FINISHED_RECORDING);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        progressBarRemainingTime.setProgress((int) animation.getAnimatedValue());
    }
}
