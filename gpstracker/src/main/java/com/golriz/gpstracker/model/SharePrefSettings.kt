package com.golriz.gpstracker.model

class SharePrefSettings {
    var interval: Long = 0
    var isGpsMode: Boolean? = null
    var isHighAccuracyMode: Boolean? = null
    var syncToServerInterval: Long = 60000
    var distanceFromLastPoint: Int = 5
    var syncItemCount: Int = 0

}
