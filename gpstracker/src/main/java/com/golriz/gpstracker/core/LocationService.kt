package com.golriz.gpstracker.core

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.LocationSharePrefUtil
import com.golriz.gpstracker.utils.NotificationCreator
import com.golriz.gpstracker.utils.SettingsLocationTracker.startLocation
import com.golriz.gpstracker.utils.StoreLocationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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


    //endregion


    override fun onCreate() {
        super.onCreate()
        prefUtil = LocationSharePrefUtil(baseContext)  // init Shared Pref Manager
    }


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
            if (googleApiClient.isConnected) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
            }
        } catch (ex: SecurityException) {
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
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
        }
        stopForeground(true)
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
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            updateService()
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
