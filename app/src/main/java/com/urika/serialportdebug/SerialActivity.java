package com.urika.serialportdebug;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SerialActivity extends AppCompatActivity implements SerialPortUtil.OnDataReceiveListener {
    private static final String TAG = "SerialPort";
    SerialPortUtil sp;
    EditText etInput;
    EditText etOutput;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                etOutput.append(msg.obj.toString());
            }
        }
    };
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        etInput = (EditText) findViewById(R.id.etInput);
        etOutput = (EditText) findViewById(R.id.etOutput);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etInput.getText().toString().isEmpty())
                    return;
                Log.d(TAG, "sending " + etInput.getText().toString());
                sp.sendCmds(etInput.getText().toString());
            }
        });

        //sp = new SerialPortUtil();        sp.onCreate();
        sp = SerialPortUtil.getInstance();
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
    protected void onDestroy() {
        super.onDestroy();
        //sp.closeSerialPort();
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
}
