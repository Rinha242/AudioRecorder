package com.dimowner.audiorecorder.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.dimowner.audiorecorder.ARApplication;
import com.dimowner.audiorecorder.audio.bluetooth.BluetoothManager;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorder implements RecorderContract.Recorder {

    private static final String TAG = "AudioRecorder";

    private MediaRecorder recorder = null;
    private File recordFile = null;
    private Timer timer;
    private final RecorderContract.RecorderCallback callback;
    private final Context context;
    private long updateTime = 0;
    private long durationMills = 0;
    private boolean isPaused = false;
    private AudioSource audioSource = AudioSource.MIC;
    private final BluetoothManager bluetoothManager;

    public AudioRecorder(Context context, RecorderContract.RecorderCallback callback) {
        this.context = context;
        this.callback = callback;
        this.bluetoothManager = new BluetoothManager(context);
    }

    @Override
    public void prepare(String outputFile, int channelCount, int sampleRate, int bitrate) {
        recordFile = new File(outputFile);
        if (recordFile.exists() && recordFile.isFile()) {
            recordFile.delete();
        }
        recorder = new MediaRecorder();
        
        // Set up audio source based on selected type
        setupAudioSource();

        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(channelCount);
        recorder.setAudioSamplingRate(sampleRate);
        recorder.setAudioEncodingBitRate(bitrate);
        recorder.setOutputFile(recordFile.getAbsolutePath());

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
            callback.onError(e.getMessage());
        }
    }

    private void setupAudioSource() {
        switch (audioSource) {
            case BLUETOOTH:
                if (bluetoothManager.isBluetoothHeadsetConnected()) {
                    bluetoothManager.startBluetoothSco();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                } else {
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                }
                break;
            case INTERNAL:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    recorder.setAudioSource(MediaRecorder.AudioSource.REMOTE_SUBMIX);
                } else {
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                }
                break;
            case MIC:
            default:
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                break;
        }
    }

    @Override
    public void startRecording() {
        if (recorder != null && !isPaused) {
            try {
                recorder.start();
                startTimer();
                callback.onStartRecord();
            } catch (IllegalStateException e) {
                Log.e(TAG, "startRecording() failed", e);
                callback.onError(e.getMessage());
            }
        }
    }

    @Override
    public void resumeRecording() {
        if (isPaused) {
            startRecording();
            isPaused = false;
            callback.onResumeRecord();
        }
    }

    @Override
    public void pauseRecording() {
        if (recorder != null) {
            stopRecorder();
            isPaused = true;
            callback.onPauseRecord();
        }
    }

    @Override
    public void stopRecording() {
        if (recorder != null) {
            stopRecorder();
            isPaused = false;
            if (bluetoothManager.isBluetoothScoOn()) {
                bluetoothManager.stopBluetoothSco();
            }
            callback.onStopRecord(recordFile.getAbsolutePath());
        }
    }

    @Override
    public void setAudioSource(AudioSource source) {
        this.audioSource = source;
    }

    @Override
    public AudioSource getAudioSource() {
        return audioSource;
    }

    private void stopRecorder() {
        try {
            recorder.stop();
        } catch (RuntimeException e) {
            Log.e(TAG, "stopRecorder() failed", e);
        }
        stopTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (recorder != null) {
                    durationMills += ARApplication.VISUALIZATION_INTERVAL;
                    callback.onRecordProgress(durationMills, getMaxAmplitude());
                }
            }
        }, 0, ARApplication.VISUALIZATION_INTERVAL);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void release() {
        stopTimer();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public boolean isRecording() {
        return recorder != null && !isPaused;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public float getMaxAmplitude() {
        if (recorder != null) {
            try {
                return recorder.getMaxAmplitude();
            } catch (IllegalStateException e) {
                Log.e(TAG, "getMaxAmplitude() failed", e);
            }
        }
        return 0;
    }

    @Override
    public long getRecordedDuration() {
        return durationMills;
    }
}