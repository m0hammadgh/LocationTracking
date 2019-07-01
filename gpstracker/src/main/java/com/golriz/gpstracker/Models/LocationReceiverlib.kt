package com.golriz.gpstracker.Models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable
import android.util.Log
import com.golriz.gpstracker.Core.SettingsLocationTracker

/**
 * @author josevieira
 */
class LocationReceiverlib : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (null != intent && intent.action == "my.action.mohammad") {
            val locationData =
                    intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location?
            Log.d(
                    "Location from lib: ",
                    "Latitude****: " + locationData?.latitude + "Longitude:" + locationData?.longitude
            )
        }
    }

}