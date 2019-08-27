package com.example.washere.helpers;

import android.app.Application;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.washere.R;
import com.example.washere.models.WasAudioFile;
import com.example.washere.repositories.WasRepository;

import java.io.File;
import java.io.IOException;

public class RecordAudioHelper {

    private static final String LOG_TAG = "RecordingService";

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

    public void startRecording() {
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

        } catch (IOException e) {
            Log.e(LOG_TAG, "Mediarecorder couldn't be prepared: " + e.getMessage());
        }
            mediaRecorder.start();
    }

    public void setFileNameAndPath() {
        do {
            fileName = application.getString(R.string.default_file_name)
                    + "_" + (wasRepository.getUserName()+"_"+wasRepository.getTimeStamp()) + ".mp3";
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

    }

    public void setUploadDateAndTime(){
        file.setUploadDate(wasRepository.getDate());
        file.setUploadTime(wasRepository.getTime());
        file.setUploaderName(wasRepository.getUserName());
    }

    public void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

}
