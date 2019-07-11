package com.golriz.gpstracker.gpsInfo

import java.text.DecimalFormat

/**
 * Class for storing constants used throughout the app
 *
 * @author Michael Fotiadis
 */
object GpsInfoConstants {

    val df = DecimalFormat("#.00")
    val LINE_SEPARATOR = System.getProperty("line.separator")
    val ACTION_AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE"

    enum class Broadcasts(val string: String) {
        BROADCAST_NETWORK_STATE_CHANGED("Brodacast_1"), // Network State Changed
        BROADCAST_GPS_CHANGED("Broadcast_2"),
        BROADCAST_NMEA_CHANGED("Broadcast_3"), // NMEA Changed
        BROADCAST_NETWORK_CHANGED("Broadcast_4"),
        BROADCAST_PASSIVE_CHANGED("Broadcast_5"),
        BROADCAST_GPS_STATE_CHANGED("Broadcast_6")

    }

    enum class Payloads(val string: String) {
        PAYLOAD_1("Payload_1"),
        PAYLOAD_2("Payload_2"),
        PAYLOAD_3("Payload_3"),
        PAYLOAD_4("Payload_4"),
        PAYLOAD_5("Payload_5")
    }

    enum class Results(val string: String) {
        RESULT_1("Result_1"),
        RESULT_2("Result_2"),
        RESULT_3("Result_3")
    }

    enum class Requests(val code: Int) {
        REQUEST_CODE_1(1),
        REQUEST_CODE_2(2),
        REQUEST_CODE_3(3)
    }

    enum class FragmentCode(val code: Int) {
        FRAGMENT_CODE_0(6000),
        FRAGMENT_CODE_GPS(6001),
        FRAGMENT_CODE_NETWORK(6002),
        FRAGMENT_CODE_PASSIVE(6003),
        FRAGMENT_CODE_SATELLITES(6004),
        FRAGMENT_CODE_NMEA(6005)
    }

}
