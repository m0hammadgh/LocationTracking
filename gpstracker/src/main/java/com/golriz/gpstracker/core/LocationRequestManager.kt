package com.golriz.gpstracker.core

import com.golriz.gpstracker.model.SharePrefSettings
import com.google.android.gms.location.LocationRequest

class LocationRequestManager {
    fun createLocationRequest(sharePrefSettings: SharePrefSettings): LocationRequest {
        val locationRequest = LocationRequest()

        locationRequest.interval = sharePrefSettings.interval

        if (sharePrefSettings.isHighAccuracyMode) {
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else if (sharePrefSettings.isGpsMode) {
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        return locationRequest
    }
}