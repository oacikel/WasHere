package com.example.washere.helpers;

import android.content.Context;
import android.media.MediaPlayer;

public class PlayAudioHelper {

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context context, int resID) {
        stop();

        mMediaPlayer = MediaPlayer.create(context, resID);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }

}