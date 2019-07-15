package com.golriz.gpstracker.utils

import android.content.Context
import com.golriz.gpstracker.model.SharePrefSettings

class StoreLocationManager(private val sharedPrefSetting: SharePrefSettings, val context: Context) {
    fun saveLocationSettings() {

        val prefManager = SharedPrefManager(context)
        prefManager.setNewLocationInterval(sharedPrefSetting.interval)
        prefManager.setIsUsingGps(sharedPrefSetting.isGpsMode)
        prefManager.setIsUsingWifi(sharedPrefSetting.isHighAccuracyMode)
        prefManager.setNewLocationDistance(sharedPrefSetting.distanceFromLastPoint)
        prefManager.setSyncItemCount(sharedPrefSetting.syncItemCount)
        prefManager.setSyncInterval(sharedPrefSetting.syncToServerInterval)
    }


    fun readLocationSettings(): SharePrefSettings {
        val prefManager = SharedPrefManager(context)


        sharedPrefSetting.syncToServerInterval = prefManager.getSyncInterval!!
        sharedPrefSetting.syncItemCount = prefManager.getSyncItemCount!!
        sharedPrefSetting.interval = prefManager.getNewLocationInterval!!
        sharedPrefSetting.isGpsMode = prefManager.getIsUsingGps!!
        sharedPrefSetting.isHighAccuracyMode = prefManager.getIsUsingWifi!!
        return sharedPrefSetting


    }

}