package com.golriz.gpstracker.gpsInfo

import java.text.DecimalFormat

object GpsInfoConstants {

    val df = DecimalFormat("#.00")
    val LINE_SEPARATOR = System.getProperty("line.separator")
    const val ACTION_AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE"

    enum class Broadcasts(val string: String) {
        BROADCAST_NETWORK_STATE_CHANGED("Brodacast_1"), // Network State Changed
        BROADCAST_GPS_CHANGED("Broadcast_2"),
        BROADCAST_NMEA_CHANGED("Broadcast_3"), // NMEA Changed
        BROADCAST_NETWORK_CHANGED("Broadcast_4"),
        BROADCAST_PASSIVE_CHANGED("Broadcast_5"),
        BROADCAST_GPS_STATE_CHANGED("Broadcast_6")

    }


}
