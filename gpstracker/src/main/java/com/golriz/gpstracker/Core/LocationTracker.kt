package com.golriz.gpstracker.Core

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.kayvannj.permission_utils.Func2
import com.github.kayvannj.permission_utils.PermissionUtil
import com.golriz.gpstracker.DB.repository.RoomRepository
import com.golriz.gpstracker.Enums.GpsModes
import com.golriz.gpstracker.GpsInfo.GpsInfo
import com.golriz.gpstracker.utils.SharedPrefManager
import java.io.Serializable


class LocationTracker(
    private val actionReceiver: String

) : Serializable {
    private var mBothPermissionRequest: PermissionUtil.PermissionRequestObject? = null
    private var interval: Long = 0
    private var gps: Boolean? = null
    private var netWork: Boolean? = null
    private var syncInterval: Long = 60000 //Default is 1 Minutes
    private var distance: Int = 5 // The distance  between last Location and the previous one  in Meter
    private var syncCount: Int = 10 // Number of records which will be synced to server in the desired interval
    private var storeToDataBase: Boolean = true
    private var currentLocationReceiver: BroadcastReceiver? = null
    private var sync_action: String? = null


    /*******    *****/


    fun currentLocation(currentLocationReceiver: BroadcastReceiver): LocationTracker {
        this.currentLocationReceiver = currentLocationReceiver
        return this
    }

    fun setInterval(interval: Long): LocationTracker {
        this.interval = interval
        return this
    }

    fun setGps(gps: Boolean?): LocationTracker {
        this.gps = gps
        return this
    }

    fun setNetWork(netWork: Boolean?): LocationTracker {
        this.netWork = netWork
        return this
    }

    fun setSyncInterval(time: Long): LocationTracker {
        this.syncInterval = time
        return this
    }

    fun setDistance(distance: Int): LocationTracker {
        this.distance = distance
        return this
    }

    fun setSyncCount(count: Int): LocationTracker {
        this.syncCount = count
        return this
    }

    fun setStoreToDataBase(status: Boolean): LocationTracker {
        this.storeToDataBase = status
        return this
    }

    fun setSyncAction(action: String): LocationTracker {
        this.sync_action = action
        return this
    }


    fun start(context: Context, appCompatActivity: AppCompatActivity): LocationTracker {
        validatePermissions(context, appCompatActivity)
        RoomRepository(context).checkPrePopulation()


        if (this.currentLocationReceiver != null) {
            val intentFilter = IntentFilter(SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST)
            intentFilter.addAction(SettingsLocationTracker.ACTION_PERMISSION_DEINED)
            context.registerReceiver(this.currentLocationReceiver, intentFilter)
        }

        return this
    }

    fun start(context: Context): LocationTracker {
        startLocationService(context)

        if (this.currentLocationReceiver != null) {
            val intentFilter = IntentFilter(SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST)
            intentFilter.addAction(SettingsLocationTracker.ACTION_PERMISSION_DEINED)
            context.registerReceiver(this.currentLocationReceiver, intentFilter)
        }
        return this
    }

    private fun startLocationService(context: Context) {
        val serviceIntent = Intent(context, LocationService::class.java)
        saveSettingsInLocalStorage(context)
        context.startService(serviceIntent)

    }

    fun isServiceRunning(context: Context): Boolean {

        if (LocationService.isRunning(context)) {

            if (currentLocationReceiver != null) {
                return true
            }

        }
        return false

    }


    fun stopLocationService(context: Context) {
        if (LocationService.isRunning(context)) {

            if (currentLocationReceiver != null) {
                context.unregisterReceiver(currentLocationReceiver)
            }

            val serviceIntent = Intent(context, LocationService::class.java)
            context.stopService(serviceIntent)
        }
    }

    fun validatePermissions(context: Context, appCompatActivity: AppCompatActivity): Boolean {
        return if (AppUtils.hasM() && !(ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED)
        ) {
            askPermissions(context, appCompatActivity)
            false
        } else {
            startLocationService(context)
            true
        }
    }

    fun askPermissions(context: Context, appCompatActivity: AppCompatActivity) {
        mBothPermissionRequest = PermissionUtil.with(appCompatActivity)
            .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).onResult(
                object : Func2() {
                    override fun call(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                        if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                            startLocationService(context)
                        } else {
                            Toast.makeText(context, "Permission Deined", Toast.LENGTH_LONG).show()
                        }
                    }

                }).ask(SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE)
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (null != mBothPermissionRequest) {
            mBothPermissionRequest!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveSettingsInLocalStorage(context: Context) {
        val prefManager = SharedPrefManager(context)
        if (this.interval != 0L) {
            prefManager.setNewLocationInterval(this.interval)
        }


        prefManager.setLocationAction(this.actionReceiver)
        prefManager.setIsUsingGps(this.gps)
        prefManager.setIsUsingWifi(this.netWork)
        prefManager.setNewLocationDistance(this.distance)
        prefManager.setSyncItemCount(this.syncCount)
        prefManager.setSyncInterval(this.syncInterval)


    }


    fun gpsStatus(context: Context): GpsModes {
        val gpsInfo = GpsInfo(context)
        return gpsInfo.currentGpsInfo()

    }

}
