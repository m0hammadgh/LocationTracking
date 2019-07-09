package com.golriz.gpstracker.Core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.golriz.gpstracker.BroadCast.Events
import com.golriz.gpstracker.BroadCast.GlobalBus
import com.golriz.gpstracker.DB.repository.RoomRepository
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST
import com.golriz.gpstracker.utils.SharedPrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

@Suppress("DEPRECATION")
class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mLocationRequest: LocationRequest

    private var mCurrentLocation: Location? = null

    private var interval: Long = 0
    private var syncItemCount: Int = 0

    internal var handler = Handler()
    internal var delay: Long? = 0
    private var actionReceiver: String? = null

    private var gps: Boolean? = null

    private var netWork: Boolean? = null


    private var prefManager: SharedPrefManager? = null

    override fun onCreate() {
        super.onCreate()
        prefManager = SharedPrefManager(baseContext)


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        if (this.actionReceiver == null) {
            this.actionReceiver = prefManager?.getLocationAction
        }

        this.delay = prefManager?.getSyncInterval

        this.syncItemCount = prefManager?.getSyncItemCount!!

        if (this.interval <= 0) {
            this.interval = prefManager?.getNewLocationInterval!!
        }

        if (this.gps == null) {
            this.gps = prefManager?.getIsUsingGps
        }

        if (this.netWork == null) {
            this.netWork = prefManager?.getIsUsingWifi
        }

        buildGoogleApiClient()

        mGoogleApiClient.connect()
        if (mGoogleApiClient.isConnected) {
            startLocationUpdates()
        }

        calculateSyncInterval()
        NotificationCreator(baseContext, this).createNotification()
        return START_STICKY
    }

    @Synchronized
    private fun buildGoogleApiClient() {

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        createLocationRequest()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = this.interval
        mLocationRequest.fastestInterval = this.interval / 2
        if (this.gps!!) {
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else if (this.netWork!!) {
            mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
            }
        } catch (ex: SecurityException) {
        }

    }

    private fun updateService() {
        if (null != mCurrentLocation) {
            calculateDistance(this.mCurrentLocation!!.latitude, this.mCurrentLocation!!.longitude, baseContext)

            sendLocationBroadcast(this.mCurrentLocation!!)
            sendCurrentLocationBroadCast(this.mCurrentLocation!!)
            Log.d("Info: ", "calculating distance")
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

    private fun stopLocationUpdates() {
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
        stopForeground(true)
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

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    fun getMessage(fragmentActivityMessage: Events.ActivityFragmentMessage) {
////        val location = fragmentActivityMessage.location
////        calculateDistance(location!!.latitude, location.longitude, baseContext)
//
//
//    }

    private fun calculateSyncInterval() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val locations = RoomRepository(baseContext).getUnSyncedLocations(syncItemCount)
                val activityFragmentMessageEvent = Events.SendLocation(locations)
                GlobalBus.bus?.post(activityFragmentMessageEvent)
                handler.postDelayed(this, delay!!)
            }
        }, delay!!)
    }

    private fun calculateDistance(latitude: Double, longitude: Double, context: Context) {
        val lastItem = RoomRepository(context).getLasSubmittedItem()
        val currentPoint = Location("current User Point")
        currentPoint.latitude = latitude
        currentPoint.longitude = longitude
        val lastInsertedPoint = Location("Last Inserted Point")
        lastInsertedPoint.longitude = lastItem.longtitude!!
        lastInsertedPoint.latitude = lastItem.latitude!!
        val distance = CalculateLocationDistance(currentPoint, lastInsertedPoint).calculateDistance()
        val desiredDistance = prefManager?.getNewLocationDistance
        if (distance > desiredDistance!!) {
            Log.d("distance", "distance is bigger")
            insertToDB(context, latitude, longitude)
        } else {
            Log.d("distance", "distance is not bigger")
        }


    }

    private fun insertToDB(context: Context, latitude: Double, longitude: Double) {
        RoomRepository(context).insertTask(latitude, longitude)
    }


}
