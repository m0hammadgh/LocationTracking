package com.golriz.gpstracker.gpsInfo

import android.content.Context
import android.location.LocationManager
import com.golriz.gpstracker.enums.GpsModes

class GpsInfo(private val context: Context) {

    fun currentGpsInfo(): GpsModes {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGpsEnabled = false
        var isWifiEnabled = false

        try {
            isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            isWifiEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (isGpsEnabled && isWifiEnabled)
            return GpsModes.HighAccuracy
        else if (isWifiEnabled && !isGpsEnabled)
            return GpsModes.BatterySaving
        else if (!isWifiEnabled && isGpsEnabled)
            return GpsModes.GpsOnly
        else return GpsModes.Off


    }
}

