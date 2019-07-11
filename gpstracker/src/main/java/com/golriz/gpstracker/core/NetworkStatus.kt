package com.golriz.gpstracker.core

class MyNetworkStatus {

    private val text_on = "ON"
    private val text_off = "OFF"
    var isGPSEnabled: Boolean = false
    var isCellNetworkEnabled: Boolean = false

    val isGPSEnabledAsString: String
        get() = if (isGPSEnabled) {
            text_on
        } else {
            text_off
        }

    val isCellNetworkEnabledAsString: String
        get() = if (isCellNetworkEnabled) {
            text_on
        } else {
            text_off
        }
}
