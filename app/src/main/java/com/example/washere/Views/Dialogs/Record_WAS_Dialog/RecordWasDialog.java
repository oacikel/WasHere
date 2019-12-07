package com.example.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.washere.R;
import com.example.washere.models.eUploadingState;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

public class RecordWasDialog extends DialogFragment implements View.OnClickListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private String LOG_TAG = ("OCUL - Record Dialog");
    private RecordWasDialogViewModel recordWasDialogViewModel;
    private ImageButton imageButtonControlRecording, imageButtonSend, imageButtonDiscardRecording, imageButtonRetry;
    private ProgressBar progressBarRemainingTime;
    private TextView editTextWasTitle, textViewUploadingOrUploaded;
    private ValueAnimator animator;
    private ConstraintLayout constraintLayoutRecord;
    private RelativeLayout relativeLayoutUploading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_record_was, container, false);
        setCancelable(true);
        initViews(view);
        initOtherObjects();
        setOnClickListeners();


        //Observers
        //Was Uploading State
        recordWasDialogViewModel.getUploadingState().observe(this, new Observer<eUploadingState>() {
            @Override
            public void onChanged(eUploadingState state) {
                if (state==eUploadingState.UPLOADING_TO_STORAGE){
                    Log.i(LOG_TAG,"Uploading to storage");
                    if (editTextWasTitle.getText().toString().isEmpty()) {
                        recordWasDialogViewModel.setUploadTitle(getString(R.string.empty_title));
                    } else {
                        recordWasDialogViewModel.setUploadTitle(editTextWasTitle.getText().toString());
                    }
                    textViewUploadingOrUploaded.setText(R.string.uploading);
                    constraintLayoutRecord.setVisibility(View.INVISIBLE);
                    relativeLayoutUploading.setVisibility(View.VISIBLE);
                    recordWasDialogViewModel.uploadWasToStorage();

                } else if(state==eUploadingState.STORAGE_UPLOAD_COMPLETE){
                    Log.i(LOG_TAG,"Upload to storeage complete");
                    recordWasDialogViewModel.updateUploadingState(eUploadingState.UPLOADING_TO_DATABASE);
                }else if(state==eUploadingState.UPLOADING_TO_DATABASE){
                    Log.i(LOG_TAG,"Uploading to database");
                    recordWasDialogViewModel.uploadWasToDatabase();

                }else if (state==eUploadingState.DATABASE_UPLOAD_COMPLETE){
                    Log.i(LOG_TAG,"Storage upload complete.");
                    showUploadSuccessful();
                    dismiss();
                }else if(state==eUploadingState.ERROR){
                    Log.i(LOG_TAG,"Error uploading");

                }
            }
        });
        //Observe Was Recording / Playing state
        recordWasDialogViewModel.getWasRecordingState().observe(this, new Observer<eWasUploadState>() {
            @Override
            public void onChanged(eWasUploadState state) {
                if (state != null) {
                    if (state == eWasUploadState.READY_TO_RECORD) {
                        constraintLayoutRecord.setVisibility(View.VISIBLE);
                        relativeLayoutUploading.setVisibility(View.INVISIBLE);
                        imageButtonSend.setVisibility(View.GONE);
                        imageButtonRetry.setVisibility(View.INVISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_record);
                        progressBarRemainingTime.setVisibility(View.INVISIBLE);

                    } else if (state == eWasUploadState.RECORDING) {
                        imageButtonSend.setVisibility(View.GONE);
                        imageButtonRetry.setVisibility(View.INVISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_stop);
                        //Start Recording
                        recordWasDialogViewModel.recordAudio();
                        recordWasDialogViewModel.setDateTimeLocation();

                        //Animate the progress bar
                        progressBarRemainingTime.setVisibility(View.VISIBLE);
                        animator.start();

                    } else if (state == eWasUploadState.FINISHED_RECORDING) {
                        Log.d(LOG_TAG, "Stop recording button pressed");
                        //Stop animating the Progress Bar
                        animator.cancel();
                        progressBarRemainingTime.setProgress(progressBarRemainingTime.getMax());
                        //Stop Recording
                        recordWasDialogViewModel.stopRecording();
                        //Prepare the recorder audio file to listen
                        recordWasDialogViewModel.prepareMediaPlayer();
                    } else if (state == eWasUploadState.READY_TO_PLAY) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                        imageButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eWasUploadState.PLAYING) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_pause);
                        recordWasDialogViewModel.playAudio();
                        imageButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eWasUploadState.PAUSED) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                        imageButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eWasUploadState.FINISHED_PLAYING) {
                        imageButtonRetry.setVisibility(View.VISIBLE);
                        imageButtonControlRecording.setImageResource(R.drawable.icon_play);
                        imageButtonSend.setVisibility(View.VISIBLE);

                    }  else if (state == eWasUploadState.UPLOAD_CANCELED) {
                        recordWasDialogViewModel.exit();
                        dismiss();
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
                recordWasDialogViewModel.updateWasUploadState(eWasUploadState.RECORDING);

            } else if (state == eWasUploadState.RECORDING) {

                recordWasDialogViewModel.updateWasUploadState(eWasUploadState.FINISHED_RECORDING);


            } else if (state == eWasUploadState.FINISHED_RECORDING) {
            } else if (state == eWasUploadState.READY_TO_PLAY) {
                recordWasDialogViewModel.updateWasUploadState(eWasUploadState.PLAYING);
            } else if (state == eWasUploadState.PLAYING) {

            } else if (state == eWasUploadState.PAUSED) {

            } else if (state == eWasUploadState.FINISHED_PLAYING) {

            }

        } else if (view == imageButtonRetry) {
            recordWasDialogViewModel.prepareRecorder();
        } else if (view == imageButtonDiscardRecording) {
            recordWasDialogViewModel.updateWasUploadState(eWasUploadState.UPLOAD_CANCELED);
        } else if (view == imageButtonSend) {
            recordWasDialogViewModel.updateUploadingState(eUploadingState.UPLOADING_TO_DATABASE);
        }

    }

    private void initViews(View view) {
        imageButtonControlRecording = view.findViewById(R.id.imageButtonControlRecording);
        imageButtonSend = view.findViewById(R.id.imageButtonSend);
        imageButtonDiscardRecording = view.findViewById(R.id.imageButtonDiscardRecording);
        imageButtonRetry = view.findViewById(R.id.imageButtonRetry);
        progressBarRemainingTime = view.findViewById(R.id.progressBarRemainingTime);
        editTextWasTitle = view.findViewById(R.id.editTextWasTitle);
        relativeLayoutUploading = view.findViewById(R.id.relativeLayoutUploading);
        constraintLayoutRecord = view.findViewById(R.id.constraintLayoutRecord);
        textViewUploadingOrUploaded = view.findViewById(R.id.textViewUploadingOrUploaded);
    }

    private void setOnClickListeners() {
        imageButtonDiscardRecording.setOnClickListener(this);
        imageButtonSend.setOnClickListener(this);
        imageButtonControlRecording.setOnClickListener(this);
        imageButtonRetry.setOnClickListener(this);
    }

    private void initOtherObjects() {
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
        Log.d(LOG_TAG, "Animation ended");
        recordWasDialogViewModel.changeStateAfterAnimationEnd();
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

    public void showUploadSuccessful(){
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                textViewUploadingOrUploaded.setText(R.string.upload_successful);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);

    }
}
