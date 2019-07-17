package com.golriz.gpstracker.utils

import com.golriz.gpstracker.enums.ActivityType
import com.google.android.gms.location.DetectedActivity

class ActivityRecognisionUtils {
    fun getActivityString(detectedActivityType: Int): String {
        return when (detectedActivityType) {
            DetectedActivity.IN_VEHICLE -> ActivityType.InVehicle.name
            DetectedActivity.ON_BICYCLE -> ActivityType.OnBicycle.name
            DetectedActivity.ON_FOOT -> ActivityType.OnFoot.name
            DetectedActivity.RUNNING -> ActivityType.Running.name
            DetectedActivity.STILL -> ActivityType.Still.name
            DetectedActivity.TILTING -> ActivityType.Tilting.name
            DetectedActivity.UNKNOWN -> ActivityType.Unknown.name
            DetectedActivity.WALKING -> ActivityType.Walking.name
            else -> ActivityType.None.name
        }
    }
}