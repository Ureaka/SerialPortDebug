package com.urika.serialportdebug;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SerialPortUtil.OnDataReceiveListener {
    private static final String TAG = "xxx";
    private static int mChannelIndex = 0;
    private TextView tvChannel;
    private Spinner spChannel;
    private Button mBtnDial;
    private Button mBtnHalt;
    private EditText etShow;
    private SerialPortUtil sp;
    private AudioManager mAudioManager;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                etShow.append(msg.obj.toString());
            } else if (msg.what == 2) {
                tvChannel.setText("Dial");
                sp.sendCmds("atd10000000");
                setRecAudioRoute(false);
                mBtnDial.setEnabled(true);
            } else if (msg.what == 3) {
                tvChannel.setText("Halt");
                sp.sendCmds("ath");
                setRecAudioRoute(true);
                mBtnHalt.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvChannel = (TextView) findViewById(R.id.tvChannel);
        spChannel = (Spinner) findViewById(R.id.spinner);
        etShow = (EditText) findViewById(R.id.etShow);
        mBtnDial = (Button) findViewById(R.id.dial);
        mBtnHalt = (Button) findViewById(R.id.halt);

        sp = SerialPortUtil.getInstance();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        spChannel.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mChannelIndex = position + 1;
                (new channelTask()).execute();
                TextView tv = (TextView) view;
                if (tv != null) tv.setTextSize(24.0f);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBtnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnDial.setEnabled(false);
                Message message = handler.obtainMessage(2);
                handler.sendMessage(message);
            }
        });

        mBtnHalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnHalt.setEnabled(false);
                Message message = handler.obtainMessage(3);
                handler.sendMessage(message);
            }
        });
    }

    void setRecAudioRoute(Boolean isChecked) {
        Settings.System.putInt(this.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, isChecked ? 1 : 0);
        if (isChecked) {
            mAudioManager.setParameters("pmr_route=out_speaker");
            mAudioManager.loadSoundEffects();
        } else {
            mAudioManager.setParameters("pmr_route=mic_handset");
            mAudioManager.unloadSoundEffects();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp.setOnDataReceiveListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sp.setOnDataReceiveListener(null);
    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        String s = new String(buffer, 0, size);
        Log.d(TAG, "onDataReceive(" + size + "), data is:" + s);
        Message message = new Message();
        message.what = 1;
        message.obj = s;
        handler.sendMessage(message);
    }

    private class channelTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spChannel.setEnabled(false);
            tvChannel.setText("Swithing to channel " + mChannelIndex + "...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground");
            try {
                sp.sendCmds("at+ctom=1");
                Thread.sleep(1000);
                sp.sendCmds(String.format("at+cxdcs=%d,1", mChannelIndex));
                Thread.sleep(1000);
                sp.sendCmds("at+ctsdc=0,0,0,1,1,0,1,1");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            tvChannel.setText("Channel " + mChannelIndex + " set ok");
            spChannel.setEnabled(true);
        }

    }
}
