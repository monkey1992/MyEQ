package com.chunk.myeq;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;
    private LinearLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        setContentView(mLayout);

        mMediaPlayer = new MediaPlayer();
        try {
        	mMediaPlayer.setDataSource("/storage/sdcard0/Music/mysmbt.mp3");
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setEqualize();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void setEqualize() {
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        short bands = mEqualizer.getNumberOfBands();

        final short minEqualizer = mEqualizer.getBandLevelRange()[0];
        final short maxEqualizer = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;
//            VerticalSeekBar seekBar = (VerticalSeekBar) findViewById(seekBars[index]);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);

            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            minDbTextView.setText((minEqualizer / 100) + "dB");

            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEqualizer / 100) + "dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            VerticalSeekBar seekBar = new VerticalSeekBar(this);
            seekBar.setMax(maxEqualizer - minEqualizer);
            seekBar.setProgress(mEqualizer.getBandLevel(band));
            seekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(VerticalSeekBar VerticalSeekBar,
                        int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(band,
                            (short) (progress + minEqualizer));
                }

                @Override
                public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {
                }

                @Override
                public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
                }
            });
            
            TextView freqTextView = new TextView(this);
            freqTextView.setLayoutParams(new ViewGroup.LayoutParams(
            		ViewGroup.LayoutParams.WRAP_CONTENT,
            		ViewGroup.LayoutParams.WRAP_CONTENT));

            freqTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            freqTextView
                    .setText((mEqualizer.getCenterFreq(band) / 1000) + "HZ");

            row.addView(maxDbTextView);
            row.addView(seekBar);
            row.addView(minDbTextView);
            row.addView(freqTextView);
            mLayout.addView(row);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (isFinishing() && mMediaPlayer != null) {
            mMediaPlayer.release();
            mEqualizer.release();
            mMediaPlayer = null;
        }
    }
}
