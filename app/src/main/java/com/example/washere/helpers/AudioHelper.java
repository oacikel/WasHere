package com.example.washere.helpers;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.washere.models.Was;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

import java.io.File;
import java.io.IOException;

public class AudioHelper {

    private static final String LOG_TAG = "OCUL-AudioHelper";

    private String fileName = null;
    private String filePath = null;
    private static String DEFAULT_FILE_NAME=("was_recording");
    private WasRepository wasRepository;
    private Application application;
    private File uploadFile;

    public AudioHelper() {
        wasRepository=WasRepository.getInstance();
    }

    //Audio Record Part
    public void prepareRecorder() {
        setFileNameAndPath();
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        try {
            mediaRecorder.prepare();
            WasRepository.getInstance().setMediaRecorder(mediaRecorder);
            WasRepository.getInstance().getWasRecordingState().setValue(eWasUploadState.READY_TO_RECORD);
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

    private void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    private void setFileNameAndPath() {

        do {
            fileName =DEFAULT_FILE_NAME
                    + "_" + (wasRepository.getUserName() + "_" + wasRepository.getTimeStamp()) + ".mp3";
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
        wasRepository.setUploadFile(new File(filePath));


    }

    public void startRecording() {
        WasRepository.getInstance().getMediaRecorder().start();
    }

    public void stopRecording() {
        MediaRecorder mediaRecorder = wasRepository.getMediaRecorder();
        //wasRepository.setUploadFile(getUploadFile());
        mediaRecorder.stop();
        mediaRecorder.release();
        wasRepository.setMediaRecorder(null);

    }

    public void preparePlayerForPreview() {
        MediaPlayer mediaPlayer;
        WasRepository wasRepository = WasRepository.getInstance();
        mediaPlayer = new MediaPlayer();
        WasRepository.getInstance().setMediaPlayer(mediaPlayer);
        try {

            mediaPlayer.setDataSource(wasRepository.getUploadFilePath());

            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    WasRepository.getInstance().getWasRecordingState().setValue(eWasUploadState.READY_TO_PLAY);
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to prepare the Media Player for Upload: " + e.getMessage());
        }
    }

    public void preparePlayerForSelectedWas(Was was) {
        MediaPlayer mediaPlayer;
        WasRepository wasRepository = WasRepository.getInstance();
        if (wasRepository.getMediaPlayer() == null) {
            mediaPlayer = new MediaPlayer();
            wasRepository.setMediaPlayer(mediaPlayer);
        }

        mediaPlayer = wasRepository.getMediaPlayer();
        mediaPlayer.reset();
        try {

            mediaPlayer.setDataSource(was.getDownloadUrl());
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to prepare the Media Player for Was Object: " + e.getMessage());
        }
    }

    public void startPlayingForPreview() {
        final MediaPlayer mediaPlayer;
        if (WasRepository.getInstance().getMediaPlayer() != null) {
            mediaPlayer = WasRepository.getInstance().getMediaPlayer();
            mediaPlayer.stop();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMediaPlayer();
                    preparePlayerForPreview();
                }
            });
            mediaPlayer.start();


        } else {
            Log.e(LOG_TAG, "Media Player does not exist in repository");
        }
    }

    public void startPlayingWasObject() {
        final MediaPlayer mediaPlayer;
        if (WasRepository.getInstance().getMediaPlayer() != null) {
            mediaPlayer = WasRepository.getInstance().getMediaPlayer();
            mediaPlayer.start();


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
