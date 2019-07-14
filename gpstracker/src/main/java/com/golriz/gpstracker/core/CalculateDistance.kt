package com.golriz.gpstracker.core

import android.content.Context
import android.location.Location
import android.util.Log
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.utils.SettingsLocationTracker

class CalculateDistance(
    val context: Context,
    var currentLocation: Location,
    private val newLocationDistance: Int?
) {
    fun calculateDistance() {
        val distance = currentLocation.distanceTo(getLastInsertedLocation())
        checkDistance(distance)
    }

    private fun checkDistance(distance: Float) {
        if (distance > this.newLocationDistance!!) {
            Log.d("distance", "distance is bigger")
            insertToDB(currentLocation.latitude, currentLocation.longitude)
        } else {
            Log.d("distance", "distance is not bigger")
        }
    }

    private fun insertToDB(latitude: Double, longitude: Double) {
        RoomRepository(context).insertTask(latitude, longitude, null)
    }

    private fun getLastInsertedLocation(): Location {
        val lastItem = RoomRepository(context).getLasSubmittedItem()
        val lastInsertedPoint = Location(SettingsLocationTracker.endLocation)
        lastInsertedPoint.longitude = lastItem.longtitude!!
        lastInsertedPoint.latitude = lastItem.latitude!!
        return lastInsertedPoint
    }

}