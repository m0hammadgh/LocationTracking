package com.golriz.gpstracker.core

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
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

    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    //endregion


    override fun onCreate() {
        super.onCreate()
        prefUtil = LocationSharePrefUtil(baseContext)  // init Shared Pref Manager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initLocationCallback()

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


}
