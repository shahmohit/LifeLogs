package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by mohit on 9/14/14.
 */

public class AudioDialogFragment extends DialogFragment {

    public String FRAGMENT_NAME = "AudioDialogFragment";
    private static String mFileName = null;
    int SamplingRate = 44100;
    int numBits = 16;
    int numChannels = 2;
    long startTime;
    long stopTime;
    String recMM;
    String recSS;

    ImageButton recordBtn;
    ImageButton stopRecordBtn;
    ImageButton retryBtn;
    ImageButton playBtn;
    ImageButton stopBtn;
    ImageButton pauseBtn;

    Button posBtn;

    TextView time;
    EditText aTags;

    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;

    Visualizer mVisualizer;
    VisualizerView mVisualizerView;

    Handler handler;
    Runnable update_runnable;
    boolean isRecording = false;
    boolean isPlaying = false;
    boolean isPaused = false;

    AlertDialog dialog;

    static AudioDialogFragment newInstance(String fPath) {
        AudioDialogFragment f = new AudioDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("fPath", fPath);
        f.setArguments(args);

        return f;
    }

    public interface AudioDialogListener {
        public void onAudioPositiveClick(DialogFragment dialog);

        public void onAudioNegativeClick(DialogFragment dialog);
    }

    AudioDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AudioDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = getArguments().getString("fPath");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.audio_tags, null);
        v.setBackgroundResource(Color.TRANSPARENT);
        recordBtn = (ImageButton) v.findViewById(R.id.record);
        recordBtn.setOnClickListener(record);
        stopRecordBtn = (ImageButton) v.findViewById(R.id.stoprecord);
        stopRecordBtn.setOnClickListener(stopRecord);
        retryBtn = (ImageButton) v.findViewById(R.id.retry);
        retryBtn.setOnClickListener(retry);
        playBtn = (ImageButton) v.findViewById(R.id.play);
        playBtn.setOnClickListener(play);
        stopBtn = (ImageButton) v.findViewById(R.id.stop);
        stopBtn.setOnClickListener(stop);
        pauseBtn = (ImageButton) v.findViewById(R.id.pause);
        pauseBtn.setOnClickListener(pause);

        stopRecordBtn.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.INVISIBLE);

        time = (TextView) v.findViewById(R.id.recordtime);
        aTags = (EditText) v.findViewById(R.id.audio_tags);
        mVisualizerView = (VisualizerView) v.findViewById(R.id.canvas);

        builder.setPositiveButton("Save", done);
        builder.setNegativeButton("Cancel", cancel);

        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            posBtn = dialog.getButton(Dialog.BUTTON_POSITIVE);
            posBtn.setEnabled(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    View.OnClickListener record = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isRecording == false) {
                startRecording();
                startTime = System.currentTimeMillis();
                updateTime();
            }
        }
    };

    View.OnClickListener stopRecord = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isRecording == true) {
                stopRecording();
            }
        }
    };

    private void startRecording() {
        isRecording = true;
        recordBtn.setVisibility(View.INVISIBLE);
        stopRecordBtn.setVisibility(View.VISIBLE);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioSamplingRate(SamplingRate);
        mRecorder.setAudioEncodingBitRate(numBits);
        mRecorder.setAudioChannels(numChannels);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(FRAGMENT_NAME, "prepare() failed");
        }

        mRecorder.start();

    }

    private void stopRecording() {
        isRecording = false;
        mRecorder.stop();

        stopTime = System.currentTimeMillis();
        long duration = stopTime - startTime;
        Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
        Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        recMM = String.format("%0" + 2 + "d", mm);
        recSS = String.format("%0" + 2 + "d", ss);
        time.setText("00:00/" + recMM + ":" + recSS);
        Log.d(FRAGMENT_NAME, Long.toString(stopTime - startTime));

        mRecorder.release();
        mRecorder = null;

        stopRecordBtn.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
        mVisualizerView.setVisibility(View.VISIBLE);
        if (dialog != null) {
            posBtn.setEnabled(true);
        }
    }

    View.OnClickListener retry = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File file = new File(mFileName);
            boolean deleted = file.delete();
            Log.d(FRAGMENT_NAME, "File deleted");
            retryBtn.setVisibility(View.INVISIBLE);
            recordBtn.setVisibility(View.VISIBLE);
            if ((isPlaying == true) || (isPaused == true)) {
                stopPlaying();
            }
            playBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
            mVisualizerView.setVisibility(View.INVISIBLE);
            time.setText("00:00");
        }
    };

    View.OnClickListener play = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if ((isPlaying == false) && (isPaused == false)) {
                startPlaying();
                updatePlayTime();
            } else if ((isPlaying == false) && (isPaused == true)) {
                resumePlaying();
            }
        }
    };

    View.OnClickListener stop = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if ((isPlaying == true) || (isPaused == true)) {
                stopPlaying();
            }
        }
    };

    View.OnClickListener pause = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if ((isPlaying == true) && (isPaused == false)) {
                pausePlaying();
            }
        }
    };

    private void startPlaying() {
        isPlaying = true;
        playBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlaying = false;
                pauseBtn.setVisibility(View.INVISIBLE);
                playBtn.setVisibility(View.VISIBLE);
                mVisualizer.setEnabled(false);
            }
        });
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            setupVisuals();
            mVisualizer.setEnabled(true);
            mPlayer.start();
        } catch (IOException e) {
            Log.e(FRAGMENT_NAME, "prepare() failed");
        }

    }


    private void pausePlaying() {
        isPlaying = false;
        isPaused = true;
        pauseBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        mPlayer.pause();
    }

    private void resumePlaying() {
        isPlaying = true;
        isPaused = false;
        playBtn.setVisibility(View.INVISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        mPlayer.start();
    }

    private void stopPlaying() {
        isPlaying = false;
        isPaused = false;
        Log.d(FRAGMENT_NAME, "Inside stop playing");
        time.setText("00:00/" + recMM + ":" + recSS);
        pauseBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        mVisualizer.setEnabled(false);
        mPlayer.release();
        mPlayer = null;
    }

    DialogInterface.OnClickListener done = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (mFileName != null) {
                //String tags = aTags.getText().toString();
                //Log.d(FRAGMENT_NAME,tags);
            } else {
                Log.d(FRAGMENT_NAME, "No recording found");
            }
            mListener.onAudioPositiveClick(AudioDialogFragment.this);
            dialogInterface.dismiss();
        }
    };

    DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (isRecording == true) {
                stopRecording();
            }
            if (mFileName != null) {
                File file = new File(mFileName);
                boolean deleted = file.delete();
                Log.d(FRAGMENT_NAME, "File deleted");
            }
            if ((isPlaying == true) || (isPaused == true)) {
                stopPlaying();
            }
            mListener.onAudioNegativeClick(AudioDialogFragment.this);
            dialogInterface.cancel();
        }
    };

    private void updateTime() {
        if (isRecording == true) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    long duration = System.currentTimeMillis() - startTime;
                    Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
                    Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
                    String mmStr = String.format("%0" + 2 + "d", mm);
                    String ssStr = String.format("%0" + 2 + "d", ss);
                    Log.d(FRAGMENT_NAME, "Inside runnable");
                    time.setText(mmStr + ":" + ssStr);
                    updateTime();
                }
            }, 100);
        } else {
            stopTime = System.currentTimeMillis();
            long duration = stopTime - startTime;
            Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
            Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
            recMM = String.format("%0" + 2 + "d", mm);
            recSS = String.format("%0" + 2 + "d", ss);
            time.setText("00:00/" + recMM + ":" + recSS);
        }
    }

    private void updatePlayTime() {
        if ((isPlaying) || (isPaused)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mPlayer != null) {
                        long duration = mPlayer.getCurrentPosition();
                        Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
                        Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
                        String mmStr = String.format("%0" + 2 + "d", mm);
                        String ssStr = String.format("%0" + 2 + "d", ss);
                        Log.d(FRAGMENT_NAME, "Inside runnable");
                        time.setText(mmStr + ":" + ssStr + "/" + recMM + ":" + recSS);
                        updatePlayTime();
                    } else {
                        Log.d(FRAGMENT_NAME, "Inside first else condition");
                        time.setText("00:00/" + recMM + ":" + recSS);
                    }
                }
            }, 100);
        } else {
            time.setText("00:00/" + recMM + ":" + recSS);
            Log.d(FRAGMENT_NAME, "Inside else condition");
        }
    }

    private void setupVisuals() {
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public String getFilePath() {
        return mFileName;
    }

    public String getTags() {
        String tags = aTags.getText().toString();
        if (tags == null) {
            tags = "";
        }
        return tags;
    }
}