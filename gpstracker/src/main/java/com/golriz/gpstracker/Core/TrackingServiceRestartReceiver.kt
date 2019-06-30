package com.golriz.gpspointer.Config

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TrackingServiceRestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            if (LocationService.stopTime) {
                LocationService.stopTime = false
                return
            }
            if (intent.action == "service.locationTracker.stopped") {


                LocationTracker("my.action", null)
                    .setInterval(5000)
                    .setGps(true)
                    .setNetWork(true)
                    .start(context)
            }

        }
    }
}