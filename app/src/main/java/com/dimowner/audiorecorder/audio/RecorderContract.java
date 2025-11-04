package com.dimowner.audiorecorder.audio;

import com.dimowner.audiorecorder.AppConstants;

public interface RecorderContract {
    interface Recorder {
        void prepare(String outputFile, int channelCount, int sampleRate, int bitrate);
        void startRecording();
        void pauseRecording();
        void resumeRecording();
        void stopRecording();
        void release();
        boolean isPaused();
        boolean isRecording();
        void setAudioSource(AudioSource source);
        AudioSource getAudioSource();
        float getMaxAmplitude();
        long getRecordedDuration();
    }

    interface RecorderCallback {
        void onStartRecord();
        void onPauseRecord();
        void onResumeRecord();
        void onRecordProgress(long mills, int amplitude);
        void onStopRecord(String outputFile);
        void onError(String message);
    }
}