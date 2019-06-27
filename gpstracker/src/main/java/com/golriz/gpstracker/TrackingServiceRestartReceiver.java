package com.golriz.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrackingServiceRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (LocationTrackerService.stopTime) {
                LocationTrackerService.stopTime = false;
                return;
            }
            if (intent.getAction().equals("service.locationTracker.stopped")) {

                
                context.startService(new Intent(context, LocationTrackerService.class));
            }

        }
    }
}