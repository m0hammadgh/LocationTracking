package com.golriz.gpstracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable
import android.util.Log
import com.golriz.gpstracker.Core.AppPreferences
import com.golriz.gpstracker.Core.CalculateLocationDistance
import com.golriz.gpstracker.Core.SettingsLocationTracker
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Last_Point_Distance
import com.golriz.gpstracker.DB.repository.RoomRepository

class LocationReceiverlib : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (null != intent && intent.action == "my.action.mohammad") {
            val locationData =
                intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location?
            Log.d("Location: ", "Latitude: " + locationData?.latitude + "Longitude:" + locationData?.longitude)
            calculateDistance(locationData!!.latitude, locationData.longitude, context)
            //RoomRepository(context).insertTask(locationData.latitude, locationData.longitude)


        }
    }

    fun calculateDistance(latitude: Double, longitude: Double, context: Context) {
        val lastItem = RoomRepository(context).getLasSubmittedItem()
        val currentPoint = Location("current User Point")
        currentPoint.latitude = latitude
        currentPoint.longitude = longitude
        val lastInsertedPoint = Location("Last Inserted Point")
        lastInsertedPoint.longitude = lastItem.longtitude!!
        lastInsertedPoint.latitude = lastItem.latitude!!
        val distance = CalculateLocationDistance(currentPoint, lastInsertedPoint).calculateDistance()
        val desiredDistance = AppPreferences(context).getInt(Pref_Last_Point_Distance, 0)
        if (distance > desiredDistance!!) {
            Log.d("distance", "distance is bigger")
            insertToDB(context, latitude, longitude)
        } else {
            Log.d("distance", "distance is not bigger")
        }


    }

    private fun insertToDB(context: Context, latitude: Double, longitude: Double) {
        RoomRepository(context).insertTask(latitude, longitude)
    }

}