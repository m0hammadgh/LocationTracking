package com.golriz.gpstracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable
import android.util.Log
import com.golriz.gpstracker.Core.SettingsLocationTracker
import com.golriz.gpstracker.DB.repository.RoomRepository

class LocationReceiverlib : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (null != intent && intent.action == "my.action.mohammad") {
            val locationData =
                    intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location?
            RoomRepository(context).insertTask(locationData?.latitude, locationData?.longitude)

            Log.d(
                    "Location from lib: ",
                    "Latitude****: " + locationData?.latitude + "Longitude:" + locationData?.longitude
            )
        }
    }

}