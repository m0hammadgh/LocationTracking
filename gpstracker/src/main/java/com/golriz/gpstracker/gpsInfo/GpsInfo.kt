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

        return if (isGpsEnabled && isWifiEnabled)
            GpsModes.HighAccuracy
        else if (isWifiEnabled && !isGpsEnabled)
            GpsModes.BatterySaving
        else if (!isWifiEnabled && isGpsEnabled)
            GpsModes.GpsOnly
        else GpsModes.Off


    }
}

