package com.golriz.gpstracker.core

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.LocationSharePrefUtil
import com.golriz.gpstracker.utils.NotificationCreator
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.startLocation
import com.golriz.gpstracker.utils.StoreLocationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import java.util.*


class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var schedulerSync: Timer
    //region Core values
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null
    var sharePrefSettings = SharePrefSettings()
    private var prefUtil: LocationSharePrefUtil? = null

    private var mLocationCallback: LocationCallback? = null
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


        /******/
        NotificationCreator(baseContext, this).createForeGroundService()
        return START_STICKY
    }

    private fun readFromSharedPref() {
        this.sharePrefSettings = StoreLocationManager(sharePrefSettings, baseContext).readLocationSettings()
    }

    @Synchronized
    private fun buildGoogleApiClient() {  // init Google Api client

        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        createLocationRequest()

        googleApiClient.connect()
        if (googleApiClient.isConnected) {
            startLocationUpdates()
        }
    }

    private fun createLocationRequest() { //start location update based on interval
        locationRequest = LocationRequest()

        locationRequest.interval = this.sharePrefSettings.interval

        if (this.sharePrefSettings.isHighAccuracyMode) {
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else if (this.sharePrefSettings.isGpsMode) {
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback!!, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {

            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }

    }

    private fun updateService() {
        if (null != currentLocation) {
            val currentPoint = Location(startLocation)
            currentPoint.latitude = currentLocation!!.latitude
            currentPoint.longitude = this.currentLocation!!.longitude
            CalculateDistance(baseContext, currentPoint, prefUtil).calculateDistance()

            Log.d("Info: ", "calculating distance")
        }
    }

    private fun stopLocationUpdates() {

        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback!!)
            stopSelf()
            stopForeground(true)
        } catch (unlikely: SecurityException) {

            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }

    }

    override fun onDestroy() {
        stopLocationUpdates()
        schedulerSync.cancel()
        googleApiClient.disconnect()
        super.onDestroy()
    }

    @Throws(SecurityException::class)
    override fun onConnected(connectionHint: Bundle?) {
        if (currentLocation == null) {
            try {
                mFusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            val mLocation: Location = task.result!!
                            Log.d(SettingsLocationTracker.TAG, mLocation.altitude.toString())
                            currentLocation = mLocation
                            updateService()

                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
            } catch (unlikely: SecurityException) {
                Log.e(TAG, "Lost location permission.$unlikely")
            }
        }
        startLocationUpdates()
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        updateService()
    }

    override fun onConnectionSuspended(cause: Int) {
        googleApiClient.connect()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "Connection failed: ${result.errorCode}")
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("hanooz implement nashode")
    }

    companion object {

        private val TAG = LocationService::class.java.simpleName

    }


    private fun calculateSyncInterval() {

        this.schedulerSync = SyncManager(sharePrefSettings, baseContext).startSyncProcess()

    }


}
