package com.example.washere.helpers;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.WindowManager;

import com.example.washere.models.Was;

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


    public void startPlaying(Was was) {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(was.getFileLocation());
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

    }

}