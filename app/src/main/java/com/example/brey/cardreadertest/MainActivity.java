package com.example.brey.cardreadertest;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.magtek.mobile.android.mtlib.MTConnectionState;
import com.magtek.mobile.android.mtlib.MTConnectionType;
import com.magtek.mobile.android.mtlib.MTSCRA;
import com.magtek.mobile.android.mtlib.MTSCRAEvent;

public class MainActivity extends AppCompatActivity {

    private MTSCRA cardReader;
    private TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hello = (TextView) findViewById(R.id.hello);

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI);
        cardReader = new MTSCRA(this, new Handler() {
            @Override public void handleMessage(Message msg) {
                hello.setText("Message");
                switch (msg.what) {
                    case MTSCRAEvent.OnDataReceived:
                        hello.setText("Data received");
                        if (cardReader.getTrack1().length() == 0 || cardReader.getTrack2().length() == 0 || cardReader.getKSN().length() == 0 || cardReader.getMaskedTracks().length() == 0) return; // Ignores partial swipes.
                        hello.setText(cardReader.getMaskedTracks());
                        break;

                    case MTSCRAEvent.OnDeviceConnectionStateChanged:
                        hello.setText("Change");
                        switch ((MTConnectionState) msg.obj) {
                            case Disconnected: audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialVolume, AudioManager.FLAG_SHOW_UI); break;
                            case Connected: audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI); break;
                        }
                }
            }
        });
        cardReader.setConnectionType(MTConnectionType.Audio);
        cardReader.openDevice();
        hello.setText("Opened");
    }
}
