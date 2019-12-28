package com.example.washere.helpers;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.washere.models.Was;
import com.example.washere.models.eRecordState;
import com.example.washere.repositories.UserRepository;
import com.example.washere.repositories.WasRepository;

import java.io.File;
import java.io.IOException;

public class AudioHelper {

    private static final String LOG_TAG = "OCUL-AudioHelper";

    private String fileName = null;
    private String filePath = null;
    private static String DEFAULT_FILE_NAME = ("was_recording");
    private WasRepository wasRepository;
    private File uploadFile;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private UserRepository userRepository = UserRepository.getInstance();


    public AudioHelper() {
        wasRepository = WasRepository.getInstance();
    }

    //Audio Record Part
    public void prepareRecorder() {
        setFileNameAndPath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        try {
            mediaRecorder.prepare();
            wasRepository.setMediaRecorder(mediaRecorder);
            wasRepository.getWasRecordingState().setValue(eRecordState.READY_TO_RECORD);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Mediarecorder couldn't be prepared: " + e.getMessage());
        }
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private File getUploadFile() {
        return uploadFile;
    }


    private void setFileNameAndPath() {

        do {

            fileName = DEFAULT_FILE_NAME
                    + "_" + (userRepository.getCurrentUser().getUserName() + "_" + wasRepository.getTimeStamp() + ".mp4");


            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filePath += "/WasHere/" + fileName;
            uploadFile = new File(filePath);
        } while (uploadFile.exists() && !uploadFile.isDirectory());
        try {
            // Make sure the directory exists.
            uploadFile.getParentFile().mkdirs();
        } catch (Exception e) {
            System.out.println("OCUL - Error in file creation: " + e.getMessage());
        }
        wasRepository.setUploadFilePath(filePath);
        wasRepository.setUploadFile(uploadFile);


    }

    public void startRecording() {
        if (wasRepository.getMediaRecorder() == null) {
            prepareRecorder();
        }
        wasRepository.getMediaRecorder().start();
    }

    public void stopRecording() {
        Log.d(LOG_TAG, "Stopping recording");
        mediaRecorder = wasRepository.getMediaRecorder();
        Log.d(LOG_TAG, "The uploaded file name is: " + wasRepository.getUploadFile().getName() +
                "\n The uploaded file exists (True/False): " + wasRepository.getUploadFile().exists() +
                "\n The uploaded file length is: " + wasRepository.getUploadFile().length());
        mediaRecorder.stop();
        mediaRecorder.release();
        wasRepository.setMediaRecorder(null);
    }

    public void preparePlayerForPreview() {
        mediaPlayer = new MediaPlayer();
        WasRepository.getInstance().setMediaPlayer(mediaPlayer);
        try {

            mediaPlayer.setDataSource(wasRepository.getUploadFilePath());

            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    wasRepository.getWasRecordingState().setValue(eRecordState.READY_TO_PLAY);
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to prepare the Media Player for Upload: " + e.getMessage());
        }
    }

    public void preparePlayerForSelectedWas(Was was) {
        if (wasRepository.getMediaPlayer() == null) {
            mediaPlayer = new MediaPlayer();
            wasRepository.setMediaPlayer(mediaPlayer);
        }

        mediaPlayer = wasRepository.getMediaPlayer();
        mediaPlayer.reset();
        try {

            mediaPlayer.setDataSource(was.getDownloadUrl());
            mediaPlayer.prepare();
            Log.i(LOG_TAG, "Prepared media player successfully");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to prepare the Media Player for Was Object: " + e.getMessage());
        }
    }

    public void startPlayingForPreview() {

        if (WasRepository.getInstance().getMediaPlayer() != null) {
            mediaPlayer = WasRepository.getInstance().getMediaPlayer();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopMediaPlayer();
                        preparePlayerForPreview();
                    }
                });
            }
            mediaPlayer.start();


        } else {
            Log.e(LOG_TAG, "Media Player does not exist in repository");
        }
    }

    public void startPlayingWasObject() {
        if (wasRepository.getMediaPlayer() != null) {
            mediaPlayer = wasRepository.getMediaPlayer();
            mediaPlayer.start();
            Log.i(LOG_TAG, "Playing was item:");
        } else {
            Log.e(LOG_TAG, "Media Player does not exist in repository");
        }
    }

    public void stopMediaPlayer() {
        if (WasRepository.getInstance().getMediaPlayer() != null) {
            WasRepository.getInstance().getMediaPlayer().release();
            WasRepository.getInstance().setMediaPlayer(null);
        }
    }


}
