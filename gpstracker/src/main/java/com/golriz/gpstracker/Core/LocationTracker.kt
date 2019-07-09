package com.golriz.gpstracker.Core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import com.github.kayvannj.permission_utils.PermissionUtil
import com.golriz.gpstracker.DB.repository.RoomRepository
import com.golriz.gpstracker.Enums.GpsModes
import com.golriz.gpstracker.GpsInfo.GpsInfo
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import com.golriz.gpstracker.utils.SharedPrefManager
import java.io.Serializable


class LocationTracker(
    private val actionReceiver: String

) : Serializable {
    private var mBothPermissionRequest: PermissionUtil.PermissionRequestObject? = null
    private var interval: Long = 0
    private var isGpsMode: Boolean? = null
    private var isHighAccuracyMode: Boolean? = null
    private var syncToServerInterval: Long = 60000 //Default is 1 Minutes
    private var distanceFromLastPoint: Int = 5 // The distance  between last Location and the previous one  in Meter
    private var syncItemCount: Int = 10 // Number of records which will be synced to server in the desired interval
    private var storeToDataBase: Boolean = true
    private var currentLocationReceiver: BroadcastReceiver? = null


    fun currentLocation(currentLocationReceiver: BroadcastReceiver): LocationTracker {
        this.currentLocationReceiver = currentLocationReceiver
        return this
    }

    fun setNewPointInterval(interval: Long): LocationTracker {
        this.interval = interval
        return this
    }

    fun setOnlyGpsMode(gps: Boolean?): LocationTracker {
        this.isGpsMode = gps
        return this
    }

    fun setHighAccuracyMode(netWork: Boolean?): LocationTracker {
        this.isHighAccuracyMode = netWork
        return this
    }

    fun setSyncToServerInterval(time: Long): LocationTracker {
        this.syncToServerInterval = time
        return this
    }

    fun setMinDistanceBetweenLocations(distance: Int): LocationTracker {
        this.distanceFromLastPoint = distance
        return this
    }

    fun setCountOfSyncItems(count: Int): LocationTracker {
        this.syncItemCount = count
        return this
    }

    fun setStoreToDataBase(status: Boolean): LocationTracker {
        this.storeToDataBase = status
        return this
    }


    fun start(context: Context, appCompatActivity: AppCompatActivity): LocationTracker? {
        validatePermissions(appCompatActivity)
        RoomRepository(context).checkPrePopulation()
        if (!isServiceRunning(context)) {
            if (SharedPrefManager(context).getIsServiceRunning() == false) {
                startLocationService(context)
                if (this.currentLocationReceiver != null) {
                    val intentFilter = IntentFilter(SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST)
                    intentFilter.addAction(SettingsLocationTracker.ACTION_PERMISSION_DEINED)
                    context.registerReceiver(this.currentLocationReceiver, intentFilter)
                }
            }

        }
        SharedPrefManager(context).setIsServiceRunning(true)

        return this
    }

    private fun startLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        saveSettingsToSharedPreferences(context)
        context.startService(serviceIntent)

    }

    private fun isServiceRunning(context: Context): Boolean {

        if (LocationService.isRunning(context)) {

            if (currentLocationReceiver != null) {
                return true
            }

        }
        return false

    }


    fun stopLocationService(context: Context) {
        SharedPrefManager(context).setIsServiceRunning(false)

        if (currentLocationReceiver != null) {
            context.unregisterReceiver(currentLocationReceiver)
        }

        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }

    private fun validatePermissions(appCompatActivity: AppCompatActivity) {
        if (!com.golriz.gpspointer.Config.PermissionChecker(appCompatActivity).checkPermission(appCompatActivity)) {
            askPermissions(appCompatActivity)
        }
    }

    private fun askPermissions(appCompatActivity: AppCompatActivity) {
        com.golriz.gpspointer.Config.PermissionChecker(appCompatActivity)
            .requestPermission(appCompatActivity, PERMISSION_ACCESS_LOCATION_CODE)
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (null != mBothPermissionRequest) {
            mBothPermissionRequest!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveSettingsToSharedPreferences(context: Context) {
        val prefManager = SharedPrefManager(context)
        if (this.interval != 0L) {
            prefManager.setNewLocationInterval(this.interval)
        }

        prefManager.setLocationAction(this.actionReceiver)
        prefManager.setIsUsingGps(this.isGpsMode)
        prefManager.setIsUsingWifi(this.isHighAccuracyMode)
        prefManager.setNewLocationDistance(this.distanceFromLastPoint)
        prefManager.setSyncItemCount(this.syncItemCount)
        prefManager.setSyncInterval(this.syncToServerInterval)


    }


    fun gpsStatus(context: Context): GpsModes {
        val gpsInfo = GpsInfo(context)
        return gpsInfo.currentGpsInfo()

    }

}
