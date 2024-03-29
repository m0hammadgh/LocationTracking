package com.golriz.gpstracker.core

import android.content.Context
import android.location.Location
import android.util.Log
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.enums.LocationSharedPrefEnums.DistanceFromLastPoint
import com.golriz.gpstracker.enums.LocationSharedPrefEnums.IsInsertedToDb
import com.golriz.gpstracker.utils.LocationSettings
import com.golriz.gpstracker.utils.LocationSettings.TAG
import com.golriz.gpstracker.utils.LocationSharePrefUtil

class DistanceCalculation(
    val context: Context,
    var currentLocation: Location,
    private val prefUtil: LocationSharePrefUtil?
) {
    fun calculateDistance() {
        Log.d(TAG, "Distance : ${currentLocation.latitude}*${currentLocation.longitude} ")
        if (prefUtil?.getLocationItem(IsInsertedToDb, true) == false) {
            insertToDB(currentLocation.latitude, currentLocation.longitude)
            prefUtil.saveToSharedPref(IsInsertedToDb, true)
        } else {
            val distance = currentLocation.distanceTo(getLastInsertedLocation())
            checkDistance(distance)

        }
    }

    private fun checkDistance(distance: Float) {
        if (distance > this.prefUtil?.getLocationItem(DistanceFromLastPoint, 10) as Int) {
            insertToDB(currentLocation.latitude, currentLocation.longitude)
        }
    }

    private fun insertToDB(latitude: Double, longitude: Double) {
        RoomRepository(context).insertLocation(latitude, longitude, null)
    }

    private fun getLastInsertedLocation(): Location {
        val lastItem = RoomRepository(context).getLasSubmittedItem()
        val lastInsertedPoint = Location(LocationSettings.endLocation)
        lastInsertedPoint.longitude = lastItem.longtitude!!
        lastInsertedPoint.latitude = lastItem.latitude!!
        return lastInsertedPoint
    }

}