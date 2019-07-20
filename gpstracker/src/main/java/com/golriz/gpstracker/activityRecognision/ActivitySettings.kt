package com.golriz.gpstracker.activityRecognision

object ActivitySettings {

    const val BROADCAST_DETECTED_ACTIVITY = "activity_intent"

    internal const val DETECTION_INTERVAL_IN_MILLISECONDS = (3 * 1000).toLong()

    val CONFIDENCE = 70
    const val drivingInterval = 10000L
    const val walkingInterval = 20000L
    const val stillInterval = 30000L
    const val unknownInterval = 15000L

}
