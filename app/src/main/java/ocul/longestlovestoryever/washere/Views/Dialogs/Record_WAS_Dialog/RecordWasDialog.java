package ocul.longestlovestoryever.washere.Views.Dialogs.Record_WAS_Dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.models.eUploadingState;
import ocul.longestlovestoryever.washere.models.eRecordState;
import ocul.longestlovestoryever.washere.repositories.WasRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RecordWasDialog extends DialogFragment implements View.OnClickListener, Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
    private String LOG_TAG = ("OCUL - Record Dialog");
    private RecordWasDialogViewModel recordWasDialogViewModel;
    private MaterialButton materialButtonSend, materialButtonDiscardRecording, materialButtonRetry;
    private ProgressBar progressBarRemainingTime;
    private FloatingActionButton floatingActionButtonControlRecording;
    private TextInputEditText textInputEditTextTitle;
    private TextView editTextWasTitle, textViewUploadingOrUploaded;
    private ValueAnimator animator;
    private ConstraintLayout constraintLayoutRecord;
    private ConstraintLayout constraintLayoutUploading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_new_post, container, false);
        setCancelable(true);
        initViews(view);
        initOtherObjects();
        setOnClickListeners();
        recordWasDialogViewModel.updateUploadingState(eUploadingState.NO_ACTION);


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
                    constraintLayoutUploading.setVisibility(View.VISIBLE);
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
                    Log.e(LOG_TAG,"Error uploading");

                }else if(state==eUploadingState.UPLOAD_CANCELED){
                    Log.w(LOG_TAG,"Upload canceled");
                    recordWasDialogViewModel.exit();
                    dismiss();
                }
            }
        });
        //Observe Was Recording / Playing state
        recordWasDialogViewModel.getWasRecordingState().observe(this, new Observer<eRecordState>() {
            @Override
            public void onChanged(eRecordState state) {
                if (state != null) {
                    if (state == eRecordState.READY_TO_RECORD) {
                        constraintLayoutRecord.setVisibility(View.VISIBLE);
                        constraintLayoutUploading.setVisibility(View.INVISIBLE);
                        materialButtonSend.setVisibility(View.GONE);
                        materialButtonRetry.setVisibility(View.INVISIBLE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_record);
                        progressBarRemainingTime.setVisibility(View.INVISIBLE);

                    } else if (state == eRecordState.RECORDING) {
                        materialButtonSend.setVisibility(View.GONE);
                        materialButtonRetry.setVisibility(View.GONE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_stop);
                        //Start Recording
                        recordWasDialogViewModel.recordAudio();
                        recordWasDialogViewModel.setDateTimeLocation();

                        //Animate the progress bar
                        progressBarRemainingTime.setVisibility(View.VISIBLE);
                        animator.start();

                    } else if (state == eRecordState.FINISHED_RECORDING) {
                        Log.d(LOG_TAG, "Stop recording button pressed");
                        //Stop animating the Progress Bar
                        animator.cancel();
                        progressBarRemainingTime.setProgress(progressBarRemainingTime.getMax());
                        //Stop Recording
                        recordWasDialogViewModel.stopRecording();
                        //Prepare the recorder audio file to listen
                        recordWasDialogViewModel.prepareMediaPlayer();
                    } else if (state == eRecordState.READY_TO_PLAY) {
                        materialButtonRetry.setVisibility(View.VISIBLE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_play);
                        materialButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eRecordState.PLAYING) {
                        materialButtonRetry.setVisibility(View.VISIBLE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_pause);
                        recordWasDialogViewModel.playAudio();
                        materialButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eRecordState.PAUSED) {
                        materialButtonRetry.setVisibility(View.VISIBLE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_play);
                        materialButtonSend.setVisibility(View.VISIBLE);
                    } else if (state == eRecordState.FINISHED_PLAYING) {
                        materialButtonRetry.setVisibility(View.VISIBLE);
                        floatingActionButtonControlRecording.setImageResource(R.drawable.icon_play);
                        materialButtonSend.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == floatingActionButtonControlRecording) {
            eRecordState state = WasRepository.getInstance().getWasRecordingState().getValue();

            if (state == eRecordState.READY_TO_RECORD) {
                //Change state to RECORDING
                recordWasDialogViewModel.updateWasUploadState(eRecordState.RECORDING);

            } else if (state == eRecordState.RECORDING) {

                recordWasDialogViewModel.updateWasUploadState(eRecordState.FINISHED_RECORDING);


            } else if (state == eRecordState.FINISHED_RECORDING) {
            } else if (state == eRecordState.READY_TO_PLAY) {
                recordWasDialogViewModel.updateWasUploadState(eRecordState.PLAYING);
            } else if (state == eRecordState.PLAYING) {

            } else if (state == eRecordState.PAUSED) {

            } else if (state == eRecordState.FINISHED_PLAYING) {

            }

        } else if (view == materialButtonRetry) {
            recordWasDialogViewModel.prepareRecorder();
        } else if (view == materialButtonDiscardRecording) {
            recordWasDialogViewModel.updateUploadingState(eUploadingState.UPLOAD_CANCELED);

        } else if (view == materialButtonSend) {
            recordWasDialogViewModel.updateUploadingState(eUploadingState.UPLOADING_TO_STORAGE);
        }

    }

    private void initViews(View view) {
        floatingActionButtonControlRecording = view.findViewById(R.id.floatingActionButtonControlRecording);
        materialButtonSend = view.findViewById(R.id.materialButtonSend);
        materialButtonDiscardRecording = view.findViewById(R.id.materialButtonDiscardRecording);
        materialButtonRetry = view.findViewById(R.id.materialButtonRetry);
        progressBarRemainingTime = view.findViewById(R.id.progressBarRemainingTime);
        editTextWasTitle = view.findViewById(R.id.textInputEditTextTitle);
        constraintLayoutUploading = view.findViewById(R.id.constraintLayoutUploading);
        constraintLayoutRecord = view.findViewById(R.id.constraintLayoutRecord);
        textViewUploadingOrUploaded = view.findViewById(R.id.textViewUploadingOrUploaded);
    }

    private void setOnClickListeners() {
        materialButtonDiscardRecording.setOnClickListener(this);
        materialButtonSend.setOnClickListener(this);
        floatingActionButtonControlRecording.setOnClickListener(this);
        materialButtonRetry.setOnClickListener(this);
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
