package com.golriz.gpspointer.Config

import android.location.Location

/**
 * @author Mohammad
 */
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
