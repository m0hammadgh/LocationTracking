package com.golriz.gpstracker.Core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.golriz.gpstracker.BroadCast.Events
import com.golriz.gpstracker.BroadCast.GlobalBus
import com.golriz.gpstracker.Core.SettingsLocationTracker.ACTION_CURRENT_LOCATION_BROADCAST
import com.golriz.gpstracker.DB.repository.RoomRepository
import com.golriz.gpstracker.R
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
        createNotification()
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

    private fun createNotification() {

        //TODO  This is a test . all variables need to be changes to declaration
        val mBuilder = Notification.Builder(
            baseContext
        )
        val notification: Notification?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker("Tracking").setWhen(0)
                .setAutoCancel(false)
                .setCategory(Notification.EXTRA_BIG_TEXT)
                .setContentTitle("گلریز")
                .setContentText("سامانه دستیار گلریز")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setColor(ContextCompat.getColor(baseContext, R.color.red))
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText("سامانه دستیار گلریز")
                )
                .setChannelId("track_marty")
                .setShowWhen(true)
                .setOngoing(true)
                .build()
        } else {
            notification =
                mBuilder.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) R.drawable.ic_launcher else R.drawable.ic_launcher)
                    .setTicker("Tracking").setWhen(0)
                    .setAutoCancel(false)
                    .setContentTitle("گلریز")
                    .setContentText("سامانه دستیار گلریز")
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText("سامانه دستیار گلریز")
                    )
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true)
                    .build()
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("track_marty", "Track", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
        /*assert notificationManager != null;
        notificationManager.notify(0, notification);*/
        startForeground(1, notification) //for foreground service, don't use 0 as id. it will not work.
    }


}
