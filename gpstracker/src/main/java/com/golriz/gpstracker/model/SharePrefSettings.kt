package com.golriz.gpstracker.model

class SharePrefSettings {
    var interval: Long = 1000
    var isGpsMode: Boolean = true
    var isHighAccuracyMode: Boolean = true
    var syncToServerInterval: Long = 60000
    var distanceFromLastPoint: Int = 5
    var syncItemCount: Int = 10
    var notificationTitle = "دستیار گلریز"
    var notificationText = "دستیار گلریز"
    var confidence = 70
    var activityRecogniseInterval = 20000L
    var isLocationDependsOnActivity = false

}
