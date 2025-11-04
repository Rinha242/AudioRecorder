package com.dimowner.audiorecorder.audio;

/**
 * Enum representing different audio input sources available for recording.
 */
public enum AudioSource {
    /**
     * Default microphone source
     */
    MIC,
    
    /**
     * Internal audio source for recording system audio
     */
    INTERNAL,
    
    /**
     * Bluetooth microphone source
     */
    BLUETOOTH;

    /**
     * Convert string value to enum
     * @param value String value to convert
     * @return AudioSource enum value, defaults to MIC if not found
     */
    public static AudioSource fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (Exception e) {
            return MIC;
        }
    }
}