package com.dimowner.audiorecorder.audio;

import android.content.Context;
import android.media.MediaRecorder;

import com.dimowner.audiorecorder.audio.bluetooth.BluetoothManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioRecorderTest {
    @Mock
    private Context context;
    @Mock
    private RecorderContract.RecorderCallback callback;
    @Mock
    private BluetoothManager bluetoothManager;
    
    private AudioRecorder audioRecorder;
    
    @Before
    public void setup() {
        audioRecorder = new AudioRecorder(context, callback);
    }
    
    @Test
    public void testDefaultAudioSource() {
        assertEquals(AudioSource.MIC, audioRecorder.getAudioSource());
    }
    
    @Test
    public void testSetAudioSource() {
        audioRecorder.setAudioSource(AudioSource.BLUETOOTH);
        assertEquals(AudioSource.BLUETOOTH, audioRecorder.getAudioSource());
    }
    
    @Test
    public void testIsRecording() {
        assertFalse(audioRecorder.isRecording());
    }
    
    @Test
    public void testIsPaused() {
        assertFalse(audioRecorder.isPaused());
    }
    
    @Test
    public void testGetRecordedDuration() {
        assertEquals(0, audioRecorder.getRecordedDuration());
    }
}