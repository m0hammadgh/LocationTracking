package com.golriz.gpstracker.gpsInfo

import android.location.GpsSatellite
import android.location.Location
import android.util.Log
import org.apache.commons.collections4.queue.CircularFifoQueue

class LocationData {
    var location: Location? = null
        set(location) {
            if (location != null) {
                field = location
            } else {
                Log.d("", "Null Location")
            }
        }
    private var nmeaBuffer: CircularFifoQueue<String>? = null
    private var mSatellites: List<GpsSatellite>? = null

    val satellitesSize: Int
        get() = if (mSatellites != null) {
            mSatellites!!.size
        } else {
            0
        }

    internal constructor() {
        val mNmeaBufferSize = 50
        nmeaBuffer = CircularFifoQueue(mNmeaBufferSize)
    }

    constructor(location: Location) {
        this.location = location
    }

    internal fun appendToNmea(nmea: String) {
        if (nmea != "") {
            nmeaBuffer?.add(nmea)
        }
    }


    internal fun setGPSEvent(mGPSEvent: String) {
        val mGPSEvent1 = mGPSEvent
    }


    internal fun setSatellites(satellites: List<GpsSatellite>) {
        this.mSatellites = satellites
    }


    internal fun setSatellitesInFix(satellitesInFix: Int) {
        val mSatellitesInFix = satellitesInFix
    }

    companion object {

        // Unit Suffixes
        private val NOT_AVAILABLE = "N/A"
        private val METERS_SECONDS = " m/sec"
        private val MILLISECONDS = " ms"
        private val DEGREES = " deg"
        private val METRES = " m"
        private val CONFIDENCE = " (68% confidence)"
    }
}