package com.golriz.gpstracker.core

class MyNetworkStatus {

    private val txtOn = "ON"
    private val txtOff = "OFF"
    var isGPSEnabled = false
    var isCellNetworkEnabled = false

    val isGPSEnabledAsString: String
        get() = if (isGPSEnabled) {
            txtOn
        } else {
            txtOff
        }

    val isCellNetworkEnabledAsString: String
        get() = if (isCellNetworkEnabled) {
            txtOn
        } else {
            txtOff
        }
}

