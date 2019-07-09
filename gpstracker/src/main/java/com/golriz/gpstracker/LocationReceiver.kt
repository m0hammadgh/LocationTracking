package com.golriz.gpstracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable
import android.util.Log
import com.golriz.gpstracker.utils.SettingsLocationTracker


class LocationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (null != intent && intent.action == "my.action.mohammad") {
            val locationData =
                intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location?
            Log.d("Location: ", "Latitude: " + locationData?.latitude + "Longitude:" + locationData?.longitude)
//            calculateDistance(locationData!!.latitude, locationData.longitude, context)
            //RoomRepository(context).insertTask(locationData.latitude, locationData.longitude)

        }
    }




}