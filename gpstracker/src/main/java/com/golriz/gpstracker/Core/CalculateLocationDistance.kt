package com.golriz.gpstracker.Core

import android.location.Location

class CalculateLocationDistance(var startPoint: Location, var endPoint: Location) {
    fun calculateDistance(): Float {
        val locationA = Location("Start Point")

        locationA.latitude = startPoint.latitude
        locationA.longitude = startPoint.longitude

        val locationB = Location("End Point")

        locationB.latitude = endPoint.latitude
        locationB.longitude = endPoint.longitude

        return locationA.distanceTo(locationB)

    }

}