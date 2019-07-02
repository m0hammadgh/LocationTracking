package com.golriz.gpstracker.Core

import android.location.Location

class CalculateLocationDistance(var point1: Location, var point2: Location) {
    fun calculateDistance(): Float {
        val locationA = Location("point A")

        locationA.latitude = point1.latitude
        locationA.longitude = point1.longitude

        val locationB = Location("point B")

        locationB.latitude = point2.latitude
        locationB.longitude = point2.longitude

        return locationA.distanceTo(locationB)

    }

}