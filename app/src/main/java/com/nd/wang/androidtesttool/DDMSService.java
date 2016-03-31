package com.nd.wang.androidtesttool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class DDMSService extends Service {
    final String TAG = "DDMSService";
    long intervalTime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "DDMSService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Timer timer;
    TimerTask timerTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        intervalTime = bundle.getLong("time");
        Log.i(TAG, "run OnStartCommand, intervalTime = " + intervalTime);
        init();
        return START_NOT_STICKY;
    }
    private void killLogcatProcess() {
        String[] processIdArray = ScriptsCmd.getInstance().getProcess("system/bin/logcat");
        ScriptsCmd.getInstance().killProcess(processIdArray);
    }

    public void init() {
        stopTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                killLogcatProcess();

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
        killLogcatProcess();
        timerTask.cancel();
        Log.i(TAG, "Destroy DDMS Service");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved DDMS Service");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.putExtra("time", intervalTime);
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG, "reStart DDMS Service");
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
