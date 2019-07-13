package com.golriz.gpstracker.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.gpsInfo.AppLog
import com.golriz.gpstracker.utils.AppUtils
import com.golriz.gpstracker.utils.NotificationCreator
import com.golriz.gpstracker.utils.SettingsLocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST
import com.golriz.gpstracker.utils.SettingsLocationTracker.endLocation
import com.golriz.gpstracker.utils.SettingsLocationTracker.startLocation
import com.golriz.gpstracker.utils.SharedPrefManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.concurrent.fixedRateTimer

@Suppress("DEPRECATION")
class LocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private lateinit var schedulerSync: Timer
    //region Google Api Values
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: LocationRequest
    private var mCurrentLocation: Location? = null

    //endregion
    //region Core values
    private var newLocationInterval: Long = 0
    private var syncToServerItemCount: Int = 0
    internal var syncToServerInterval: Long? = 0
    private var receiverName: String? = null
    private var isUsingGps: Boolean? = null
    private var isHighAccuracyMode: Boolean? = null
    private var prefManager: SharedPrefManager? = null


    //endregion


    override fun onCreate() {
        super.onCreate()
        prefManager = SharedPrefManager(baseContext)


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


        if (this.receiverName == null) {
            this.receiverName = prefManager?.getLocationAction
        }

        this.syncToServerInterval = prefManager?.getSyncInterval

        this.syncToServerItemCount = prefManager?.getSyncItemCount!!

        if (this.newLocationInterval <= 0) {
            this.newLocationInterval = prefManager?.getNewLocationInterval!!
        }

        if (this.isUsingGps == null) {
            this.isUsingGps = prefManager?.getIsUsingGps
        }

        if (this.isHighAccuracyMode == null) {
            this.isHighAccuracyMode = prefManager?.getIsUsingWifi
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
        mLocationRequest.interval = this.newLocationInterval
        mLocationRequest.fastestInterval = this.newLocationInterval / 2
        if (this.isUsingGps!!) {
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } else if (this.isHighAccuracyMode!!) {
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
            calculateDistance(this.mCurrentLocation!!.latitude, this.mCurrentLocation!!.longitude)

            sendLocationBroadcast(this.mCurrentLocation!!)
            sendCurrentLocationBroadCast(this.mCurrentLocation!!)
            Log.d("Info: ", "calculating distance")
        } else {
            sendPermissionDeniedBroadCast()
            Log.d("Error: ", "Permission dastrasi nadarad")
        }
    }

    private fun sendLocationBroadcast(sbLocationData: Location) {
        val locationIntent = Intent()
        locationIntent.action = this.receiverName
        locationIntent.putExtra(SettingsLocationTracker.LOCATION_MESSAGE, sbLocationData)
        sendBroadcast(locationIntent)
    }

    private fun sendCurrentLocationBroadCast(sbLocationData: Location) {
        val locationIntent = Intent()
        locationIntent.action = ACTION_CURRENT_LOCATION_BROADCAST
        locationIntent.putExtra(SettingsLocationTracker.LOCATION_MESSAGE, sbLocationData)
        sendBroadcast(locationIntent)


    }

    private fun sendPermissionDeniedBroadCast() {
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
        schedulerSync.cancel()
        mGoogleApiClient.disconnect()
        super.onDestroy()
    }

    @Throws(SecurityException::class)
    override fun onConnected(connectionHint: Bundle?) {
        AppLog.i("Connected to GoogleApiClient")
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
        Log.i(TAG, "Connection failed: ${result.errorCode}")
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
        this.schedulerSync = fixedRateTimer("default", false, 0L, syncToServerInterval!!) {
            val locations = RoomRepository(baseContext).getUnSyncedLocations(syncToServerItemCount)
            val activityFragmentMessageEvent = Events.SendLocation(locations)
            GlobalBus.bus?.post(activityFragmentMessageEvent)
        }
    }

    private fun calculateDistance(latitude: Double, longitude: Double) {
        val lastItem = RoomRepository(baseContext).getLasSubmittedItem()
        val currentPoint = Location(startLocation)
        currentPoint.latitude = latitude
        currentPoint.longitude = longitude
        val lastInsertedPoint = Location(endLocation)
        lastInsertedPoint.longitude = lastItem.longtitude!!
        lastInsertedPoint.latitude = lastItem.latitude!!
        val distance = CalculateLocationDistance(currentPoint, lastInsertedPoint).calculateDistance()
        val desiredDistance = prefManager?.getNewLocationDistance
        if (distance > desiredDistance!!) {
            Log.d("distance", "distance is bigger")
            insertToDB(latitude, longitude)
        } else {
            Log.d("distance", "distance is not bigger")
        }


    }

    private fun insertToDB(latitude: Double, longitude: Double) {
        RoomRepository(baseContext).insertTask(latitude, longitude, null)
    }


}
