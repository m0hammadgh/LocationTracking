package com.golriz.gpstracker.utils

import android.content.Context
import com.golriz.gpstracker.model.SharePrefSettings

class SharePrefSaver(val sharedPrefSetting: SharePrefSettings, val context: Context) {
    fun saveToSharePref() {
        val prefManager = SharedPrefManager(context)
        prefManager.setNewLocationInterval(sharedPrefSetting.interval)
        prefManager.setIsUsingGps(sharedPrefSetting.isGpsMode)
        prefManager.setIsUsingWifi(sharedPrefSetting.isHighAccuracyMode)
        prefManager.setNewLocationDistance(sharedPrefSetting.distanceFromLastPoint)
        prefManager.setSyncItemCount(sharedPrefSetting.syncItemCount)
        prefManager.setSyncInterval(sharedPrefSetting.syncToServerInterval)
    }

}