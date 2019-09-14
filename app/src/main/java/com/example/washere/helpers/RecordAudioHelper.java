package com.example.washere.helpers;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.washere.R;
import com.example.washere.models.Was;
import com.example.washere.models.WasAudioFile;
import com.example.washere.models.eWasUploadState;
import com.example.washere.repositories.WasRepository;

import java.io.File;
import java.io.IOException;

public class RecordAudioHelper {

    private static final String LOG_TAG = "OCUL-RecordAudioHelper";

    private String fileName = null;
    private String filePath = null;
    private WasRepository wasRepository;
    private MediaRecorder mediaRecorder = null;
    private Application application;
    private WasAudioFile file;

    public RecordAudioHelper(Application application, WasRepository wasRepository) {
        this.application = application;
        this.wasRepository = wasRepository;
    }

    //Audio Record Part
    public void prepareRecorder() {
        setFileNameAndPath();
        setUploadDateAndTime();
        mediaRecorder = new MediaRecorder();
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

    public void startRecording() {
        WasRepository.getInstance().getMediaRecorder().start();
    }

    public WasAudioFile getFile() {
        return file;
    }

    public void setFile(WasAudioFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public void setFileNameAndPath() {
        do {
            fileName = application.getString(R.string.default_file_name)
                    + "_" + (wasRepository.getUserName() + "_" + wasRepository.getTimeStamp()) + ".mp3";
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filePath += "/WasHere/" + fileName;


            file = new WasAudioFile(filePath);
        } while (file.exists() && !file.isDirectory());
        try {
            // Make sure the directory exists.
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            System.out.println("Error in file creation: " + e.getMessage());
        }
        wasRepository.setUploadFilePath(filePath);
    }

    public void setUploadDateAndTime() {
        file.setUploadDate(wasRepository.getDate());
        file.setUploadTime(wasRepository.getTime());
        file.setUploaderName(wasRepository.getUserName());
    }

    public void stopRecording() {
        MediaRecorder mediaRecorder = wasRepository.getMediaRecorder();
        mediaRecorder.stop();
        mediaRecorder.release();
        wasRepository.setMediaRecorder(null);
    }

    public void preparePlayer() {
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
            Log.e(LOG_TAG, "Failed to prepare the Media Player: " + e.getMessage());
        }
    }

    public void startPlaying() {
        final MediaPlayer mediaPlayer;
        if (WasRepository.getInstance().getMediaPlayer() != null) {
            mediaPlayer = WasRepository.getInstance().getMediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMediaPlayer();
                    preparePlayer();
                }
            });
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
