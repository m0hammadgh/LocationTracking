package com.golriz.gpstracker.GpsInfo

import android.content.Context
import android.location.LocationManager
import com.golriz.gpstracker.Enums.GpsModes

class GpsInfo(private val context: Context) {

    fun currentGpsInfo(): GpsModes {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (gps_enabled && network_enabled)
            return GpsModes.HighAccuracy
        else if (network_enabled && !gps_enabled)
            return GpsModes.BatterySaving
        else if (!network_enabled && gps_enabled)
            return GpsModes.GpsOnly
        else return GpsModes.Off


    }
}

