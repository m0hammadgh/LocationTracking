package com.golriz.gpstracker.utils

import android.content.Context
import com.golriz.gpstracker.enums.LocationSharedPrefEnums.*
import com.golriz.gpstracker.model.SharePrefSettings

class LocationDbUtil(private val sharedPrefSetting: SharePrefSettings, val context: Context) {
    fun saveLocationSettings() {

        val prefManager = LocationSharePrefUtil(context)
        prefManager.saveToSharedPref(Interval, sharedPrefSetting.interval)
        prefManager.saveToSharedPref(IsGpsMode, sharedPrefSetting.isGpsMode)
        prefManager.saveToSharedPref(IsHighAccuracyMode, sharedPrefSetting.isHighAccuracyMode)
        prefManager.saveToSharedPref(DistanceFromLastPoint, sharedPrefSetting.distanceFromLastPoint)
        prefManager.saveToSharedPref(SyncItemCount, sharedPrefSetting.syncItemCount)
        prefManager.saveToSharedPref(SyncToServerInterval, sharedPrefSetting.syncToServerInterval)
        prefManager.saveToSharedPref(Confidence, sharedPrefSetting.confidence)
        prefManager.saveToSharedPref(ActivityInterval, sharedPrefSetting.activityRecogniseInterval)
        prefManager.saveToSharedPref(IsUsingActivityRecognition, sharedPrefSetting.isLocationDependsOnActivity)
    }


    fun readLocationSettings(): SharePrefSettings {
        val prefManager = LocationSharePrefUtil(context)
        sharedPrefSetting.syncToServerInterval = prefManager.getLocationItem(SyncToServerInterval, 1000L) as Long
        sharedPrefSetting.syncItemCount = prefManager.getLocationItem(SyncItemCount, 20) as Int
        sharedPrefSetting.interval = prefManager.getLocationItem(Interval, 1000L) as Long
        sharedPrefSetting.distanceFromLastPoint = prefManager.getLocationItem(DistanceFromLastPoint, 50) as Int
        sharedPrefSetting.isGpsMode = prefManager.getLocationItem(IsGpsMode, true) as Boolean
        sharedPrefSetting.isHighAccuracyMode = prefManager.getLocationItem(IsHighAccuracyMode, true) as Boolean
        sharedPrefSetting.isLocationDependsOnActivity =
            prefManager.getLocationItem(IsUsingActivityRecognition, false) as Boolean
        sharedPrefSetting.activityRecogniseInterval = prefManager.getLocationItem(ActivityInterval, 20000L) as Long
        sharedPrefSetting.confidence = prefManager.getLocationItem(Confidence, 70) as Int

        return sharedPrefSetting


    }

}