//package com.golriz.gpstracker
//
//import android.R.attr.foreground
//import android.content.Context.ACTIVITY_SERVICE
//import androidx.core.content.ContextCompat.getSystemService
//import com.google.android.gms.location.LocationRequest
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import android.content.Intent
//import androidx.test.orchestrator.junit.BundleJUnitUtils.getResult
//import org.junit.experimental.results.ResultMatchers.isSuccessful
//import androidx.annotation.NonNull
//import com.google.android.gms.tasks.OnCompleteListener
//import android.R
//import androidx.core.app.NotificationCompat
//import com.golriz.locationtracker.MainActivity
//import androidx.core.app.ServiceCompat.stopForeground
//import android.app.Service.START_NOT_STICKY
//import android.content.Context.NOTIFICATION_SERVICE
//import android.R.attr.start
//import android.app.*
//import android.content.res.Configuration
//import android.location.Location
//import android.os.*
//import android.util.Log
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.FusedLocationProviderClient
//
//
//class LocationUpdatesService : Service() {
//
//    private val mBinder = LocalBinder()
//
//    /**
//     * Used to check whether the bound activity has really gone away and not unbound as part of an
//     * orientation change. We create a foreground service notification only if the former takes
//     * place.
//     */
//    private var mChangingConfiguration = false
//
//    private var mNotificationManager: NotificationManager? = null
//
//    /**
//     * Contains parameters used by [com.google.android.gms.location.FusedLocationProviderApi].
//     */
//    private var mLocationRequest: LocationRequest? = null
//
//    /**
//     * Provides access to the Fused Location Provider API.
//     */
//    private var mFusedLocationClient: FusedLocationProviderClient? = null
//
//    /**
//     * Callback for changes in location.
//     */
//    private var mLocationCallback: LocationCallback? = null
//
//    private var mServiceHandler: Handler? = null
//
//    /**
//     * The current location.
//     */
//    private var mLocation: Location? = null
//
//    /**
//     * Returns the [NotificationCompat] used as part of the foreground service.
//     */
//    // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
//    // The PendingIntent that leads to a call to onStartCommand() in this service.
//    // The PendingIntent to launch activity.
//    // Set the Channel ID for Android O.
//    // Channel ID
//
//    override fun onCreate() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                super.onLocationResult(locationResult)
//                onNewLocation(locationResult!!.lastLocation)
//            }
//        }
//
//        createLocationRequest()
//        getLastLocation()
//
//        val handlerThread = HandlerThread(TAG)
//        handlerThread.start()
//        mServiceHandler = Handler(handlerThread.looper)
//        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
//
//        // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.app_name)
//            // Create the channel for the notification
//            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
//
//            // Set the Notification Channel for the Notification Manager.
//            mNotificationManager!!.createNotificationChannel(mChannel)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        Log.i(TAG, "Service started")
//        val startedFromNotification = intent.getBooleanExtra(
//            EXTRA_STARTED_FROM_NOTIFICATION,
//            false
//        )
//
//        // We got here because the user decided to remove location updates from the notification.
//        if (startedFromNotification) {
//            removeLocationUpdates()
//            stopSelf()
//        }
//        // Tells the system to not try to recreate the service after it has been killed.
//        return START_NOT_STICKY
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        mChangingConfiguration = true
//    }
//
//    override fun onBind(intent: Intent): IBinder {
//        // Called when a client (MainActivity in case of this sample) comes to the foreground
//        // and binds with this service. The service should cease to be a foreground service
//        // when that happens.
//        Log.i(TAG, "in onBind()")
//        stopForeground(true)
//        mChangingConfiguration = false
//        return mBinder
//    }
//
//    override fun onRebind(intent: Intent) {
//        // Called when a client (MainActivity in case of this sample) returns to the foreground
//        // and binds once again with this service. The service should cease to be a foreground
//        // service when that happens.
//        Log.i(TAG, "in onRebind()")
//        stopForeground(true)
//        mChangingConfiguration = false
//        super.onRebind(intent)
//    }
//
//    override fun onUnbind(intent: Intent): Boolean {
//        Log.i(TAG, "Last client unbound from service")
//
//        // Called when the last client (MainActivity in case of this sample) unbinds from this
//        // service. If this method is called due to a configuration change in MainActivity, we
//        // do nothing. Otherwise, we make this service a foreground service.
//        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
//            Log.i(TAG, "Starting foreground service")
//
//
//        }
//        return true // Ensures onRebind() is called when a client re-binds.
//    }
//
//    override fun onDestroy() {
//        mServiceHandler!!.removeCallbacksAndMessages(null)
//    }
//
//    /**
//     * Makes a request for location updates. Note that in this sample we merely log the
//     * [SecurityException].
//     */
//    fun requestLocationUpdates() {
//        Log.i(TAG, "Requesting location updates")
//        Utils.setRequestingLocationUpdates(this, true)
//        startService(Intent(getApplicationContext(), LocationUpdatesService::class.java))
//        try {
//            mFusedLocationClient!!.requestLocationUpdates(
//                mLocationRequest,
//                mLocationCallback!!, Looper.myLooper()
//            )
//        } catch (unlikely: SecurityException) {
//            Utils.setRequestingLocationUpdates(this, false)
//            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
//        }
//
//    }
//
//    /**
//     * Removes location updates. Note that in this sample we merely log the
//     * [SecurityException].
//     */
//    fun removeLocationUpdates() {
//        Log.i(TAG, "Removing location updates")
//        try {
//            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
//            Utils.setRequestingLocationUpdates(this, false)
//            stopSelf()
//        } catch (unlikely: SecurityException) {
//            Utils.setRequestingLocationUpdates(this, true)
//            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
//        }
//
//    }
//
//    private fun getLastLocation() {
//        try {
//            mFusedLocationClient!!.lastLocation
//                .addOnCompleteListener(object : OnCompleteListener<Location> {
//                    fun onComplete(task: Task<Location>) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            mLocation = task.getResult()
//                        } else {
//                            Log.w(TAG, "Failed to get location.")
//                        }
//                    }
//                })
//        } catch (unlikely: SecurityException) {
//            Log.e(TAG, "Lost location permission.$unlikely")
//        }
//
//    }
//
//    private fun onNewLocation(location: Location) {
//        Log.i(TAG, "New location: $location")
//
//        mLocation = location
//
//        // Notify anyone listening for broadcasts about the new location.
//        val intent = Intent(ACTION_BROADCAST)
//        intent.putExtra(EXTRA_LOCATION, location)
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)
//
//        // Update notification content if running as a foreground service.
//        if (serviceIsRunningInForeground(this)) {
//            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
//        }
//    }
//
//    /**
//     * Sets the location request parameters.
//     */
//    private fun createLocationRequest() {
//        mLocationRequest = LocationRequest()
//        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
//        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
//        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//
//    /**
//     * Class used for the client Binder.  Since this service runs in the same process as its
//     * clients, we don't need to deal with IPC.
//     */
//    inner class LocalBinder : Binder() {
//        internal val service: LocationUpdatesService
//            get() = this@LocationUpdatesService
//    }
//
//    /**
//     * Returns true if this is a foreground service.
//     *
//     * @param context The [Context].
//     */
//    fun serviceIsRunningInForeground(context: Context): Boolean {
//        val manager = context.getSystemService(
//            Context.ACTIVITY_SERVICE
//        ) as ActivityManager
//        for (service in manager.getRunningServices(
//            Integer.MAX_VALUE
//        )) {
//            if (getClass().getName().equals(service.service.className)) {
//                if (service.foreground) {
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    companion object {
//
//        private val PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice"
//
//        private val TAG = LocationUpdatesService::class.java.simpleName
//
//        /**
//         * The name of the channel for notifications.
//         */
//        private val CHANNEL_ID = "channel_01"
//
//        internal val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
//
//        internal val EXTRA_LOCATION = "$PACKAGE_NAME.location"
//        private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
//
//        /**
//         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
//         */
//        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
//
//        /**
//         * The fastest rate for active location updates. Updates will never be more frequent
//         * than this value.
//         */
//        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
//
//        /**
//         * The identifier for the notification displayed for the foreground service.
//         */
//        private val NOTIFICATION_ID = 12345678
//    }
//}