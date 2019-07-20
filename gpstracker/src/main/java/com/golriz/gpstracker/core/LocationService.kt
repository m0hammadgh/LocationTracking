package com.golriz.gpstracker.core

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.golriz.gpstracker.activityRecognision.ActivitySettings
import com.golriz.gpstracker.activityRecognision.ActivitySettings.drivingInterval
import com.golriz.gpstracker.activityRecognision.ActivitySettings.stillInterval
import com.golriz.gpstracker.activityRecognision.ActivitySettings.walkingInterval
import com.golriz.gpstracker.enums.LocationSharedPrefEnums.Confidence
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.LocationDbUtil
import com.golriz.gpstracker.utils.LocationSharePrefUtil
import com.golriz.gpstracker.utils.NotificationUtil
import com.google.android.gms.location.*
import java.util.*


class LocationService : Service(),
    LocationListener {

    private lateinit var schedulerSync: Timer
    //region Core values
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null
    var sharePrefSettings = SharePrefSettings()
    private var prefUtil: LocationSharePrefUtil? = null
    lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    //endregion


    override fun onCreate() {
        super.onCreate()
        prefUtil = LocationSharePrefUtil(baseContext)  // init Shared Pref Manager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initLocationCallback()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == ActivitySettings.BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    changeIntervalOnActivityChanged(type, confidence)


                }
            }
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(ActivitySettings.BROADCAST_DETECTED_ACTIVITY)
        )

    }

    private fun initLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult?.lastLocation
                updateService()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        readFromSharedPref()
        buildGoogleApiClient()
        calculateSyncInterval()
        NotificationUtil(baseContext, this).createForeGroundService()
        return START_STICKY
    }

    private fun readFromSharedPref() {
        this.sharePrefSettings = LocationDbUtil(sharePrefSettings, baseContext).readLocationSettings()
    }

    private fun buildGoogleApiClient() {

        createLocationRequest()
        startLocationUpdates()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequestInitilizer().createLocationRequest(sharePrefSettings)

    }

    private fun startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        try {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {

            Log.d(TAG, "Lost location permission. Could not request updates. $unlikely")
        }

    }

    private fun updateService() {
        if (null != currentLocation) {
            DistanceCalculation(baseContext, currentLocation!!, prefUtil).calculateDistance()
        }
    }

    private fun stopLocationUpdates() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)

        Log.d(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            stopSelf()
            stopForeground(true)
        } catch (unlikely: SecurityException) {
            Log.d(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }

    }

    override fun onDestroy() {
        stopLocationUpdates()
        schedulerSync.cancel()
        super.onDestroy()
    }


    override fun onLocationChanged(location: Location) {
        currentLocation = location
        updateService()
    }


    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("failed to implement")
    }

    companion object {

        private val TAG = LocationService::class.java.simpleName

    }


    private fun calculateSyncInterval() {

        this.schedulerSync = SyncLocation(sharePrefSettings, baseContext).startSyncProcess()

    }

    private fun changeIntervalOnActivityChanged(type: Int, confidence: Int) {

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                if (checkDesiredConfidence(confidence)) {
                    updateInterval(drivingInterval)
                }
            }
            DetectedActivity.STILL -> {
                if (checkDesiredConfidence(confidence)) {
                    updateInterval(stillInterval)
                }
            }
            DetectedActivity.WALKING -> {
                if (checkDesiredConfidence(confidence)) {
                    updateInterval(walkingInterval)
                }
            }
        }


    }

    private fun checkDesiredConfidence(confidence: Int): Boolean {
        return confidence > (prefUtil?.getLocationItem(Confidence, 70) as Int)
    }

    private fun updateInterval(interval: Long) {
        locationRequest.interval = interval
        try {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (exception: SecurityException) {

            Log.d(TAG, "Lost location permission. Could not request updates. $exception")
        }
    }


}
