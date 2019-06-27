package com.golriz.gpstracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationTracker {
    private int newLocationDistance = 50;
    private int newLocationTimeInterval = 20;
    private int syncToserverTimeInterval = 100;
    private int numberOfRecordsToSync = 10;
    private Context context;


    public LocationTracker(Context context) {
        this.context = context;
    }

    public LocationTracker(int newLocationDistance, int newLocationTimeInterval, int syncToserverTimeInterval, int numberOfRecordsToSync, Context context) {
        this.newLocationDistance = newLocationDistance;
        this.newLocationTimeInterval = newLocationTimeInterval;
        this.syncToserverTimeInterval = syncToserverTimeInterval;
        this.numberOfRecordsToSync = numberOfRecordsToSync;
        this.context = context;
    }

    public void setNewLocationDistance(int newLocationDistance) {
        this.newLocationDistance = newLocationDistance;
    }

    public void setNewLocationTimeInterval(int newLocationTimeInterval) {
        this.newLocationTimeInterval = newLocationTimeInterval;
    }

    public void setSyncToserverTimeInterval(int syncToserverTimeInterval) {
        this.syncToserverTimeInterval = syncToserverTimeInterval;
    }

    public void setNumberOfRecordsToSync(int numberOfRecordsToSync) {
        this.numberOfRecordsToSync = numberOfRecordsToSync;
    }

    public void startTracking() {
        LocationTrackerService mSensorService = new LocationTrackerService(context, newLocationDistance, newLocationTimeInterval, syncToserverTimeInterval, numberOfRecordsToSync);
        Intent mServiceIntent = new Intent(context, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            context.startService(mServiceIntent);
        }
    }

    public void stopTracking() {
        LocationTrackerService mSensorService = new LocationTrackerService(context);
        Intent mServiceIntent = new Intent(context, mSensorService.getClass());
        context.stopService(mServiceIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i("isMyServiceRunning?", true + "");
                    return true;
                }
            }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }
}
