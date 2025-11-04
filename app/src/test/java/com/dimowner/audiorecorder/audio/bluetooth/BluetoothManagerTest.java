package com.dimowner.audiorecorder.audio.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BluetoothManagerTest {
    @Mock
    private Context context;
    @Mock
    private BluetoothAdapter bluetoothAdapter;
    @Mock
    private AudioManager audioManager;

    private BluetoothManager bluetoothManager;

    @Before
    public void setup() {
        when(context.getSystemService(Context.AUDIO_SERVICE)).thenReturn(audioManager);
        bluetoothManager = new BluetoothManager(context);
    }

    @Test
    public void whenBluetoothNotSupported_thenReturnsFalse() {
        assertFalse(bluetoothManager.isBluetoothHeadsetConnected());
    }

    @Test
    public void whenBluetoothHeadsetConnected_thenReturnsTrue() {
        when(audioManager.isBluetoothScoAvailableOffCall()).thenReturn(true);
        when(bluetoothAdapter.isEnabled()).thenReturn(true);
        when(bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET))
            .thenReturn(BluetoothProfile.STATE_CONNECTED);

        assertTrue(bluetoothManager.isBluetoothHeadsetConnected());
    }

    @Test
    public void whenStartBluetoothSco_thenAudioManagerStartsCalled() {
        when(audioManager.isBluetoothScoAvailableOffCall()).thenReturn(true);
        when(bluetoothAdapter.isEnabled()).thenReturn(true);
        when(bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET))
            .thenReturn(BluetoothProfile.STATE_CONNECTED);

        bluetoothManager.startBluetoothSco();

        verify(audioManager).startBluetoothSco();
        verify(audioManager).setBluetoothScoOn(true);
    }

    @Test
    public void whenStopBluetoothSco_thenAudioManagerStopCalled() {
        bluetoothManager.stopBluetoothSco();

        verify(audioManager).stopBluetoothSco();
        verify(audioManager).setBluetoothScoOn(false);
    }
}