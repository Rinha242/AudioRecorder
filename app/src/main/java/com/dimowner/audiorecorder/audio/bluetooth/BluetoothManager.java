package com.dimowner.audiorecorder.audio.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

/**
 * Manages Bluetooth audio device connections and settings
 */
public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    
    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final AudioManager audioManager;

    public BluetoothManager(Context context) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Check if a Bluetooth headset is connected and available for audio input
     */
    public boolean isBluetoothHeadsetConnected() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Device doesn't support Bluetooth");
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is not enabled");
            return false;
        }

        if (!audioManager.isBluetoothScoAvailableOffCall()) {
            Log.d(TAG, "Bluetooth SCO is not available");
            return false;
        }

        int state = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        return state == BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * Start Bluetooth SCO (Synchronous Connection-Oriented) audio connection
     */
    public void startBluetoothSco() {
        if (isBluetoothHeadsetConnected()) {
            Log.d(TAG, "Starting Bluetooth SCO audio connection");
            audioManager.startBluetoothSco();
            audioManager.setBluetoothScoOn(true);
        }
    }

    /**
     * Stop Bluetooth SCO audio connection
     */
    public void stopBluetoothSco() {
        Log.d(TAG, "Stopping Bluetooth SCO audio connection");
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
    }

    /**
     * Check if Bluetooth SCO is currently active
     */
    public boolean isBluetoothScoOn() {
        return audioManager.isBluetoothScoOn();
    }
}