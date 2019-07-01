package com.golriz.gpstracker.Core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.golriz.gpstracker.Core.SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Action
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Gps
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Internet
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Location_Interval
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    protected lateinit var mGoogleApiClient: GoogleApiClient

    protected lateinit var mLocationRequest: LocationRequest

    protected var mCurrentLocation: Location? = null

    protected var interval: Long = 0


    protected var actionReceiver: String? = null

    protected var gps: Boolean? = null

    protected var netWork: Boolean? = null

    private var appPreferences: AppPreferences? = null

    override fun onCreate() {
        super.onCreate()
        appPreferences = AppPreferences(baseContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (this.actionReceiver == null) {
            this.actionReceiver = this.appPreferences!!.getString(Pref_Action, "LOCATION.ACTION")
        }

        if (this.interval <= 0) {
            this.interval = this.appPreferences!!.getLong(Pref_Location_Interval, 10000L)!!
        }

        if (this.gps == null) {
            this.gps = this.appPreferences!!.getBoolean(Pref_Gps, true)
        }

        if (this.netWork == null) {
            this.netWork = this.appPreferences!!.getBoolean(Pref_Internet, false)
        }

        buildGoogleApiClient()

        mGoogleApiClient.connect()
        if (mGoogleApiClient.isConnected) {
            startLocationUpdates()
        }
        return Service.START_STICKY
    }

    @Synchronized
    protected fun buildGoogleApiClient() {

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        createLocationRequest()
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = this.interval
        mLocationRequest.fastestInterval = this.interval / 2
        if (this.gps!!) {
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else if (this.netWork!!) {
            mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    protected fun startLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
            }
        } catch (ex: SecurityException) {
        }

    }

    private fun updateService() {
        if (null != mCurrentLocation) {
            sendLocationBroadcast(this.mCurrentLocation!!)
            sendCurrentLocationBroadCast(this.mCurrentLocation!!)
            Log.d("Info: ", "send broadcast location data")
        } else {
            sendPermissionDeinedBroadCast()
            Log.d("Error: ", "Permission dastrasi nadarad")
        }
    }

    private fun sendLocationBroadcast(sbLocationData: Location) {
        val locationIntent = Intent()
        locationIntent.action = this.actionReceiver
        locationIntent.putExtra(SettingsLocationTracker.LOCATION_MESSAGE, sbLocationData)
        sendBroadcast(locationIntent)
    }

    private fun sendCurrentLocationBroadCast(sbLocationData: Location) {
        val locationIntent = Intent()
        locationIntent.action = ACTION_CURRENT_LOCATION_BROADCAST
        locationIntent.putExtra(SettingsLocationTracker.LOCATION_MESSAGE, sbLocationData)
        sendBroadcast(locationIntent)
    }

    private fun sendPermissionDeinedBroadCast() {
        val locationIntent = Intent()
        locationIntent.action = SettingsLocationTracker.ACTION_PERMISSION_DEINED
        sendBroadcast(locationIntent)
    }

    protected fun stopLocationUpdates() {
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    override fun onDestroy() {
        stopLocationUpdates()
        mGoogleApiClient.disconnect()
        super.onDestroy()
    }

    @Throws(SecurityException::class)
    override fun onConnected(connectionHint: Bundle?) {
        Log.i(TAG, "Connected to GoogleApiClient")
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            updateService()
        }
        startLocationUpdates()
    }

    override fun onLocationChanged(location: Location) {
        mCurrentLocation = location
        updateService()
    }

    override fun onConnectionSuspended(cause: Int) {
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.errorCode)
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("hanooz implement nashode")
    }

    companion object {

        private val TAG = LocationService::class.java.simpleName

        fun isRunning(context: Context): Boolean {
            return AppUtils.isServiceRunning(context, LocationService::class.java)
        }
    }

}
