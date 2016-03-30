package com.nd.wang.androidtesttool;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends AppCompatActivity {
    final String TAG = "TestTools";
    Button startButton;
    Button stopButton;
    Button rebootButton;
    Button cleanButton;
    EditText timeText;
    TextView textStatus;
    Timer timer;
    Handler handler;
    long intervalTime = 1800000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        timeText = (EditText) findViewById(R.id.timeText);
        textStatus = (TextView) findViewById(R.id.textStatus);
        handler = new MyHandler();

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellUtils.execCommand("mkdir sdcard/ddms", true);
                if (TextUtils.isEmpty(timeText.getText())){
                    Log.i(TAG, "editText isEmpty ..........");
                }else{
                    Log.i(TAG, "editText isNotEmpty.setIntervalTime.....");
                    intervalTime = Long.parseLong(timeText.getText().toString()) * 1000;
                }
                Intent intent = new Intent(getApplicationContext(), DDMSService.class);
                intent.putExtra("time", intervalTime);
                startService(intent);
                Toast.makeText(getApplicationContext(), "start DDMS Service", Toast.LENGTH_LONG).show();
                isServiceRunning();
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DDMSService.class);
                stopService(intent);
                Toast.makeText(getApplicationContext(), "stop DDMS Service", Toast.LENGTH_SHORT).show();
            }
        });

        rebootButton = (Button) findViewById(R.id.rebootButton);
        rebootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellUtils.execCommand("reboot", true);
                Log.i(TAG, "reboot Deivce");
            }
        });

        cleanButton = (Button) findViewById(R.id.cleanButton);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButton.callOnClick();
                ShellUtils.execCommand("rm -r sdcard/ddms", true);
                Log.i(TAG, "clean DDMS log path");
                Toast.makeText(getApplicationContext(), "clean log complete", Toast.LENGTH_LONG).show();
            }
        });
        new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        };
                        timer = new Timer(true);
                        timer.schedule(task, 1000, 1000);
                    }
                });
            }
        }.start();
        ShellUtils.execCommand("mkdir sdcard/ddms", true);
    }

    private final class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if (isServiceRunning()){
                        textStatus.setText("Service is Running");
                        textStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRunning));
                    }else {
                        textStatus.setText("Service is Stop");
                        textStatus.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorStop));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if ("com.nd.wang.androidtesttool.DDMSService".equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
