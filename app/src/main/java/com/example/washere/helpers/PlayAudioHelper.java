package com.example.washere.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.example.washere.models.Was;

import java.io.File;
import java.io.IOException;

public class PlayAudioHelper {
    private static final String LOG_TAG = "PlaybackFragment";
    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    public void startPlaying(Context context,Was was) {
        mMediaPlayer = new MediaPlayer();

        try {
            //mMediaPlayer.setDataSource(was.getAudioFile().getPath());
            mMediaPlayer.setDataSource(was.getDownloadUrl());
            System.out.println("Ocul, downloadURL is : "+was.getDownloadUrl());
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed"+e.getMessage());
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

    }

}