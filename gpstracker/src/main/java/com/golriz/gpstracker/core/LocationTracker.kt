package com.golriz.gpstracker.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.kayvannj.permission_utils.PermissionUtil
import com.golriz.gpstracker.FakeTracker.AuthorityChecker
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.enums.FakeMode
import com.golriz.gpstracker.enums.GpsModes
import com.golriz.gpstracker.enums.LocationSharedPrefEnums
import com.golriz.gpstracker.gpsInfo.GpsInfo
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.LocationSharePrefUtil
import com.golriz.gpstracker.utils.SettingsLocationTracker.TAG
import com.golriz.gpstracker.utils.StoreLocationManager
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

    fun setOnlyGpsMode(gps: Boolean): LocationTracker {
        sharedPrefSetting.isGpsMode = gps
        return this
    }

    fun setHighAccuracyMode(isUsingWifi: Boolean): LocationTracker {
        sharedPrefSetting.isHighAccuracyMode = isUsingWifi
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

    fun getGpsStatus(context: Context): GpsModes {
        val gpsInfo = GpsInfo(context)
        return gpsInfo.currentGpsInfo()

    }

    fun setnotificationTitle(title: String): LocationTracker {
        sharedPrefSetting.notificationTitle = title
        return this
    }

    fun setnotificationText(text: String): LocationTracker {
        sharedPrefSetting.notificationText = text
        return this
    }


    fun start(context: Context, appCompatActivity: Activity): LocationTracker? {
        if (!PermissionChecker().checkPermission(appCompatActivity)) {
            Log.d(TAG, "Permission denied")
        } else if (AuthorityChecker(context).check() != FakeMode.None) {
            Log.d(
                    TAG,
                    "Error :  To use this service YOU MUST Uninstall all Fake Gps Applications or Turn off Developer option "
            )
            return null
        } else {
            if (!isServiceRunning(context)) {
                startLocationService(context)
            }
        }
        return this
    }

    private fun startLocationService(context: Context) {
        LocationSharePrefUtil(context).saveToSharedPref(LocationSharedPrefEnums.IsServiceRunning, true)
        saveSettingsToSharedPreferences(context)
        //Start service
        val serviceIntent = Intent(context, LocationService::class.java)
        context.startService(serviceIntent)
        //Register Event Bus if is not subscribed
        if (GlobalBus.bus?.isRegistered(subscriber) == false) {
            try {
                GlobalBus.bus?.register(subscriber)

            } catch (e: Exception) {
            }

        }


    }

    private fun isServiceRunning(context: Context): Boolean {

        return LocationSharePrefUtil(context).getLocationItem(LocationSharedPrefEnums.IsServiceRunning, false) as Boolean

    }


    fun stopLocationService(context: Context) {
        LocationSharePrefUtil(context).saveToSharedPref(LocationSharedPrefEnums.IsServiceRunning, false)
        val serviceIntent = Intent(context, LocationService::class.java)
        context.stopService(serviceIntent)
    }


    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (null != mBothPermissionRequest) {
            mBothPermissionRequest!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveSettingsToSharedPreferences(context: Context) {
        StoreLocationManager(sharedPrefSetting, context).saveLocationSettings()
    }


}
