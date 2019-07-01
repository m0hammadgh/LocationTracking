package com.golriz.gpstracker.Core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable

class CurrentLocationReceiver : BroadcastReceiver {

    private val currentLocationListener: CurrentLocationListener


    constructor(currentLocationListener: CurrentLocationListener) {
        this.currentLocationListener = currentLocationListener
    }

    override fun onReceive(context: Context, intent: Intent?) {

        if (null != intent && intent.action == SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST) {
            val locationData =
                    intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location?
            currentLocationListener.onCurrentLocation(locationData!!)
        }

        if (null != intent && intent.action == SettingsLocationTracker.ACTION_PERMISSION_DEINED) {
            currentLocationListener.onPermissionDiened()
        }

    }

}
