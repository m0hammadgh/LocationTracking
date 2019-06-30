package com.golriz.gpspointer.Config

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.kayvannj.permission_utils.Func2
import com.github.kayvannj.permission_utils.PermissionUtil

import java.io.Serializable

class LocationTracker(
    /**
     * name  action to send gps data
     * for  broadcast receiver
     */
    private val actionReceiver: String, mainActivity: Activity?
) : Serializable {

    /**
     * ask permissions
     */
    private var mBothPermissionRequest: PermissionUtil.PermissionRequestObject? = null

    /**
     * interval to send gps data
     */
    private var interval: Long = 0
    private var distanceFromLastLocation: Long = 0
    private var syncToServerInterval: Long = 0
    private var numberOfRecordsToSync = 10

    /**
     * use gps provider
     */
    private var gps: Boolean? = null

    /**
     * use network provider
     */
    private var netWork: Boolean? = null

    /**
     * broadcast to get current location
     */
    private var currentLocationReceiver: BroadcastReceiver? = null

    init {
        if (mainActivity != null) {
            val permissionChecker = PermissionChecker(mainActivity)
            permissionChecker.checkPermission()
        }

    }


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

    fun setsyncInterval(syncInterval: Long?): LocationTracker {
        this.syncToServerInterval = syncInterval!!
        return this
    }

    fun setNumberOfRecordToSync(syncCountRecords: Int): LocationTracker {
        this.numberOfRecordsToSync = syncCountRecords
        return this
    }

    fun setdistanceFromLastLocation(distanceFromLastLocation: Long?): LocationTracker {
        this.distanceFromLastLocation = distanceFromLastLocation!!
        return this
    }

    fun start(context: Context, appCompatActivity: AppCompatActivity): LocationTracker {
        validatePermissions(context, appCompatActivity)

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

    fun stopLocationService(context: Context) {
        if (LocationService.isRunning(context)) {

            if (currentLocationReceiver != null) {
                context.unregisterReceiver(currentLocationReceiver)
            }

            val serviceIntent = Intent(context, LocationService::class.java)
            context.stopService(serviceIntent)
        }
    }

    fun validatePermissions(context: Context, appCompatActivity: AppCompatActivity) {
        if (AppUtils.hasM() && !(ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            askPermissions(context, appCompatActivity)
        } else {
            startLocationService(context)
        }
    }

    fun askPermissions(context: Context, appCompatActivity: AppCompatActivity) {
        mBothPermissionRequest = PermissionUtil.with(appCompatActivity)
            .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).onResult(
            object : Func2() {
                override fun call(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

    fun saveSettingsInLocalStorage(context: Context) {
        val appPreferences = AppPreferences(context)
        if (this.interval != 0L) {
            appPreferences.putLong("LOCATION_INTERVAL", this.interval)
        }
        appPreferences.putString("ACTION", this.actionReceiver)
        appPreferences.putBoolean("GPS", this.gps)
        appPreferences.putBoolean("NETWORK", this.netWork)
    }

}
