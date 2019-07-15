package com.golriz.gpstracker.core

import android.content.Context
import android.location.Location
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SharedPrefManager

class CalculateDistance(
        val context: Context,
        var currentLocation: Location,
        private val prefManager: SharedPrefManager?
) {
    fun calculateDistance() {
        if (prefManager?.getIsInsertedItem == false) {
            insertToDB(currentLocation.latitude, currentLocation.longitude)
            prefManager.setIsInsertedDb(true)
        } else {
            val distance = currentLocation.distanceTo(getLastInsertedLocation())
            checkDistance(distance)

        }
    }

    private fun checkDistance(distance: Float) {
        if (distance > this.prefManager?.getNewLocationDistance!!) {
            insertToDB(currentLocation.latitude, currentLocation.longitude)
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