package com.nd.wang.androidtesttool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {
    final String TAG = "TestTools";
    Button startButton;
    Button stopButton;
    Button rebootButton;
    Button cleanButton;
    EditText timeText;
    long intervalTime = 1800000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        timeText = (EditText) findViewById(R.id.timeText);

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
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DDMSService.class);
                stopService(intent);
                Toast.makeText(getApplicationContext(), "stop DDMS Service", Toast.LENGTH_LONG).show();
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
    }
}
