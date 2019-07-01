package com.golriz.gpstracker.Core

import android.location.Location


interface CurrentLocationListener {

    /**
     * get current location
     */
    fun onCurrentLocation(location: Location)

    /**
     * Permission deined
     */
    fun onPermissionDiened()

}
