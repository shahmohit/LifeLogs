package com.android.lifelogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class AudioActivity extends Activity {

    public String ACTIVITY_NAME = "AudioActivity";
    private static String mFileName = null;
    int SamplingRate = 44100;
    int numBits = 16;
    int numChannels = 2;
    long startTime;
    long stopTime;
    String recMM;
    String recSS;
    String mCurrentAudioPath = null;
    Button recordBtn;
    ImageButton playBtn;
    ImageButton stopBtn;
    ImageButton doneBtn;
    ImageButton retryBtn;
    ImageButton cancelBtn;
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

    TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_audio);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        recordBtn = (Button) findViewById(R.id.recordbtn);
        playBtn = (ImageButton) findViewById(R.id.playbtn);
        playBtn.setVisibility(View.INVISIBLE);
        stopBtn = (ImageButton) findViewById(R.id.stopbtn);
        stopBtn.setVisibility(View.INVISIBLE);
        doneBtn = (ImageButton) findViewById(R.id.audio_done);
        doneBtn.setVisibility(View.INVISIBLE);
        cancelBtn = (ImageButton) findViewById(R.id.audio_cancel);
        retryBtn = (ImageButton) findViewById(R.id.audio_replay);
        retryBtn.setVisibility(View.INVISIBLE);
        time = (TextView) findViewById(R.id.recordtime);
        aTags = (EditText) findViewById(R.id.audio_tags);
        mVisualizerView = (VisualizerView) findViewById(R.id.canvas);
    }

    private void setupVisuals() {
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public void record (View view) {
        if (isRecording==false) {
            startRecording();
            startTime = System.currentTimeMillis();
            updateTime();
        }
        else {
            stopRecording();
        }

    }

    private void updateTime() {
        if (isRecording == true) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    long duration = System.currentTimeMillis() - startTime;
                    Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
                    Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
                    String mmStr = String.format("%0"+2+"d",mm);
                    String ssStr = String.format("%0"+2+"d",ss);
                    Log.d(ACTIVITY_NAME, "Inside runnable");
                    time.setText(mmStr + ":" + ssStr);
                    updateTime();
                }
            }, 100);
        }
        else {
            stopTime = System.currentTimeMillis();
            long duration = stopTime - startTime;
            Long mm = TimeUnit.MILLISECONDS.toMinutes(duration);
            Long ss = TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
            recMM = String.format("%0"+2+"d",mm);
            recSS = String.format("%0"+2+"d",ss);
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
                        Log.d(ACTIVITY_NAME, "Inside runnable");
                        time.setText(mmStr + ":" + ssStr + "/" + recMM + ":" + recSS);
                        updatePlayTime();
                    }
                    else {
                        Log.d(ACTIVITY_NAME,"Inside first else condition");
                        time.setText("00:00/" + recMM + ":" + recSS);
                    }
                }
            }, 100);
        }
        else {
            time.setText("00:00/" + recMM + ":" + recSS);
            Log.d(ACTIVITY_NAME, "Inside else condition");
        }
    }

    private void startRecording() {
        isRecording = true;
        recordBtn.setText("Stop Recording");
        File audioFile = null;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioSamplingRate(SamplingRate);
        mRecorder.setAudioEncodingBitRate(numBits);
        mRecorder.setAudioChannels(numChannels);
        try {
            audioFile = createAudioFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        if (audioFile != null) {
            mFileName = audioFile.getAbsolutePath();
        }
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(ACTIVITY_NAME, "prepare() failed");
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
        Log.d(ACTIVITY_NAME,Long.toString(stopTime-startTime));
        mRecorder.release();
        mRecorder = null;
        recordBtn.setText("Start Recording");
        recordBtn.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.VISIBLE);
        mVisualizerView.setVisibility(View.VISIBLE);
    }

    public void play (View view) {
        if ((isPlaying==false) && (isPaused == false)) {
            startPlaying();
            updatePlayTime();
        }
        else if ((isPlaying==false) && (isPaused==true)){
            resumePlaying();
        }
        else if ((isPlaying == true) && (isPaused == false)) {
            pausePlaying();
        }
    }

    public void stop (View view) {
        if ((isPlaying == true) || (isPaused == true)) {
            stopPlaying();
        }
    }

    private void startPlaying() {
        isPlaying = true;
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlaying = false;
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
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
            Log.e(ACTIVITY_NAME, "prepare() failed");
        }

    }


    private void pausePlaying() {
        isPlaying = false;
        isPaused = true;
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        mPlayer.pause();
    }

    private void resumePlaying() {
        isPlaying = true;
        isPaused = false;
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
        mPlayer.start();
    }
    private void stopPlaying() {
        isPlaying = false;
        isPaused = false;
        Log.d(ACTIVITY_NAME,"Inside stop playing");
        time.setText("00:00/" + recMM + ":" + recSS);
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        mVisualizer.setEnabled(false);
        mPlayer.release();
        mPlayer = null;
    }
    public void cancel (View view) {
        if (isRecording == true) {
            stopRecording();
        }
        if (mFileName != null) {
            File file = new File(mFileName);
            boolean deleted = file.delete();
            Log.d(ACTIVITY_NAME, "File deleted");
        }
        if ((isPlaying == true) || (isPaused == true)) {
            stopPlaying();
        }
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED,intent);
        finish();
    }

    public void retry (View view) {
        File file = new File(mFileName);
        boolean deleted = file.delete();
        Log.d(ACTIVITY_NAME,"File deleted");
        recordBtn.setVisibility(View.VISIBLE);
        if ((isPlaying == true) || (isPaused == true)) {
            stopPlaying();
        }
        playBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        doneBtn.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.INVISIBLE);
        mVisualizerView.setVisibility(View.INVISIBLE);
        time.setText("00:00");
    }

    public void done (View view) {
        if (mFileName != null) {
            String tags = aTags.getText().toString();
            Toast.makeText(AudioActivity.this,tags,Toast.LENGTH_LONG);
            Intent intent = new Intent();
            intent.putExtra("current",mFileName);
            intent.putExtra("tags",tags);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        else {
            Log.d(ACTIVITY_NAME,"No recording found");
        }
    }

    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "AAC_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File audio = File.createTempFile(
                audioFileName,  /* prefix */
                ".m4a",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentAudioPath = "file:" + audio.getAbsolutePath();
        return audio;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class VisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    };

    public VisualizerView(Context context, AttributeSet attrs, int i) {
        super(context,attrs,i);
        init();
    };

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(10f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(getResources().getColor(R.color.wave));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }
}