package com.nd.wang.androidtesttool;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wang on 16-3-28.
 */
public class DDMSService extends Service {
    final String TAG = "DDMSService";
    long intervalTime;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Timer timer;
    TimerTask timerTask;
    Context context;
    Handler handler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        handler = new MyHandler();
        Bundle bundle = intent.getExtras();
        intervalTime = bundle.getLong("time");
        Log.i(TAG, "run OnStartCommand, intervalTime = " + intervalTime);
        init();
        flags = START_REDELIVER_INTENT;
        return flags;
    }

    public void init() {
        stopTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                String[] processIdArray = ScriptsCmd.getInstance().getProcess("system/bin/logcat");
                ScriptsCmd.getInstance().killProcess(processIdArray);

                final String[] ddmsLogShell = new String[]{
                        "/system/bin/logcat -c",
                        "/system/bin/logcat -f sdcard/ddms/$(date +%Y%m%d-%H%M%S).txt -v time"
                };

                for (String str : ddmsLogShell) Log.i(TAG, "ddmsLogShell===" + str);
                new Thread(){
                    @Override
                    public void run() {
                        ShellUtils.CommandResult result = ShellUtils.execCommand(ddmsLogShell, true);
                        Log.i(TAG, "=================================结果==============================");
                        Log.i(TAG, "result.result===" + result.result);
                        Log.i(TAG, "result.successMsg===" + result.successMsg);
                        Log.i(TAG, "result.errorMsg===" + result.errorMsg);
                    }
                }.start();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 3000, intervalTime);
    }


    private class MyHandler extends Handler {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    }

    public void stopTask() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        Log.i(TAG, "Destroy DDMS Service");
    }
}
