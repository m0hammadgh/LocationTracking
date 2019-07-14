package com.golriz.gpstracker.core

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.kayvannj.permission_utils.PermissionUtil
import com.golriz.gpstracker.FakeTracker.FakeApplicationManager
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.enums.GpsModes
import com.golriz.gpstracker.gpsInfo.GpsInfo
import com.golriz.gpstracker.gpsInfo.GpsSetting
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import com.golriz.gpstracker.utils.SharePrefSaver
import com.golriz.gpstracker.utils.SharedPrefManager
import java.io.Serializable


class LocationTracker(
    private val subscriber: AppCompatActivity

) : Serializable {
    private var mBothPermissionRequest: PermissionUtil.PermissionRequestObject? = null
    private var sharedPrefSetting = SharePrefSettings()

    fun setNewPointInterval(interval: Long): LocationTracker {
        sharedPrefSetting.interval = interval
        return this
    }

    fun setOnlyGpsMode(gps: Boolean?): LocationTracker {
        sharedPrefSetting.isGpsMode = gps
        return this
    }

    fun setHighAccuracyMode(netWork: Boolean?): LocationTracker {
        sharedPrefSetting.isHighAccuracyMode = netWork
        return this
    }

    fun setSyncToServerInterval(time: Long): LocationTracker {
        sharedPrefSetting.syncToServerInterval = time
        return this
    }

    fun setMinDistanceBetweenLocations(distance: Int): LocationTracker {
        sharedPrefSetting.distanceFromLastPoint = distance
        return this
    }

    fun setCountOfSyncItems(count: Int): LocationTracker {
        sharedPrefSetting.syncItemCount = count
        return this
    }


    fun start(context: Context, appCompatActivity: AppCompatActivity): LocationTracker? {
        if (!PermissionChecker().checkPermission(appCompatActivity))
            validatePermissions(appCompatActivity)
        else if (FakeApplicationManager(context).init()) {
            Log.d("Failed ... ", "Error :  To use this service YOU MUST Uninstall all Fake Gps Applications ")
            return null
        } else {


            if (!isServiceRunning(context)) {
                startLocationService(context)
            }
        }
        return this
    }

    private fun startLocationService(context: Context) {
        SharedPrefManager(context).setIsServiceRunning(true)
        val serviceIntent = Intent(context, LocationService::class.java)
        saveSettingsToSharedPreferences(context)
        context.startService(serviceIntent)

        try {
            GlobalBus.bus?.register(subscriber)

        } catch (e: Exception) {
        }

    }

    private fun isServiceRunning(context: Context): Boolean {

        return SharedPrefManager(context).getIsServiceRunning()!!

    }


    fun stopLocationService(context: Context) {
        SharedPrefManager(context).setIsServiceRunning(false)
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }

    private fun validatePermissions(appCompatActivity: AppCompatActivity) {
        if (!PermissionChecker().checkPermission(appCompatActivity)) {
            askPermissions(appCompatActivity)
        }
    }

    private fun askPermissions(appCompatActivity: AppCompatActivity) {
        PermissionChecker()
            .requestPermission(appCompatActivity, PERMISSION_ACCESS_LOCATION_CODE)
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (null != mBothPermissionRequest) {
            mBothPermissionRequest!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveSettingsToSharedPreferences(context: Context) {
        SharePrefSaver(sharedPrefSetting, context).saveToSharePref()
    }


    fun gpsStatus(context: Context): GpsModes {
        val gpsInfo = GpsInfo(context)
        return gpsInfo.currentGpsInfo()

    }

    private fun getConnectedSattelites(): Int? {
        return GpsSetting.instance?.gpsData?.satellitesSize
    }

}
