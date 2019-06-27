package com.golriz.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class LocationTrackerService extends Service {
    public int counter = 0;
    Context context;
    public static boolean stopTime = false;
    private int newLocationDistance = 0;
    private int newLocationTimeInterval = 0;
    private int syncToserverTimeInterval = 0;
    private int numberOfRecordsToSync = 0;

    public LocationTrackerService(Context applicationContext, int newLocationDistance, int newLocationTimeInterval, int syncToserverTimeInterval, int numberOfRecordsToSync) {
        super();
        context = applicationContext;
        this.newLocationDistance = newLocationDistance;
        this.newLocationTimeInterval = newLocationTimeInterval;
        this.syncToserverTimeInterval = syncToserverTimeInterval;
        this.numberOfRecordsToSync = numberOfRecordsToSync;

    }

    public LocationTrackerService(Context context) {
        this.context = context;
    }

    public LocationTrackerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");

        Intent broadcastIntent = new Intent("service.locationTracker.stopped");
        sendBroadcast(broadcastIntent);
        stoptimertask();

    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        Log.d(TAG, "startTimer: ");
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        Log.d(TAG, "stoptimertask: ");

        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}