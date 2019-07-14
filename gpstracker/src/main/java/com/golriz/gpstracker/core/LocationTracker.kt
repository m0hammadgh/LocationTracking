package com.golriz.gpstracker.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import com.github.kayvannj.permission_utils.PermissionUtil
import com.golriz.gpstracker.FakeTracker.FakeApplicationManager
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.enums.GpsModes
import com.golriz.gpstracker.gpsInfo.AppLog
import com.golriz.gpstracker.gpsInfo.GpsInfo
import com.golriz.gpstracker.gpsInfo.GpsSetting
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import com.golriz.gpstracker.utils.SharedPrefManager
import java.io.Serializable


class LocationTracker(
    private val actionReceiver: String, private val subscriber: AppCompatActivity

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
        stopLocationService(context)
        if (!PermissionChecker().checkPermission(appCompatActivity))
            validatePermissions(appCompatActivity)
        else if (FakeApplicationManager(context).init()) {
            AppLog.d("Error :  To use this service Uninstall all Fake Gps Applications First ")
            return null
        } else {

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
        }


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
        try {
            GlobalBus.bus?.register(subscriber)

        } catch (e: Exception) {
        }


    }


    fun gpsStatus(context: Context): GpsModes {
        val gpsInfo = GpsInfo(context)
        return gpsInfo.currentGpsInfo()

    }

    private fun getConnectedSattelites(): Int? {
        return GpsSetting.instance?.gpsData?.satellitesSize
    }

}
