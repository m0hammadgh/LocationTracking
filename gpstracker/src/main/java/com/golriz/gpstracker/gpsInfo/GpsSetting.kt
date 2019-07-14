@file:Suppress("DEPRECATION")

package com.golriz.gpstracker.gpsInfo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.location.GpsStatus.NmeaListener
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.System.AIRPLANE_MODE_ON
import android.widget.Toast
import com.golriz.gpstracker.core.MyNetworkStatus
import java.util.*


@Suppress("DEPRECATION")
class GpsSetting
/**
 * GpsSetting Constructor
 */
private constructor() : LocationListener, NmeaListener, GpsStatus.Listener {
    // **** Application Fields
    private var mContext: Context? = null
    // **** Location Manager Fields
    private var mLocationManager: LocationManager? = null
    // **** Location Data Fields
    //	public Context getContext() {
    //		return mContext;
    //	}
    var gpsData: LocationData? = null
        private set
    private var networkData: LocationData? = null
    private var passiveData: LocationData? = null
    // **** Network Fields
    private var networkStatus: MyNetworkStatus? = null

    init {
        // Initialise the location data fields
        gpsData = LocationData()
        networkData = LocationData()
        passiveData = LocationData()

        networkStatus = MyNetworkStatus()
    }

    private fun notifyGPSDataChanged() {
        if (mLocationManager != null) {
            // Set the GPS Location according to the GPS Provider
            if (checkLocationPermission()) return
            gpsData!!.location = mLocationManager!!
                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
            // Broadcast that data has changed
            val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_GPS_CHANGED.string)
            mContext!!.sendBroadcast(broadcastIntent)
        }
    }

    private fun notifyNetworkDataChanged() {
        if (mLocationManager != null) {
            // Set the Network Location according to the Network Provider
            if (checkLocationPermission()) return
            networkData!!.location = mLocationManager!!
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            // Broadcast that data has changed
            val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_NETWORK_CHANGED.string)
            mContext!!.sendBroadcast(broadcastIntent)
        }
    }

    private fun notifyNetworkStateChanged() {
        if (mLocationManager != null) {
            networkStatus!!.isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (isAirplaneModeOn(mContext!!)) {
                networkStatus!!.isCellNetworkEnabled = false
            } else {
                networkStatus!!.isCellNetworkEnabled =
                    mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
            // Broadcast that data has changed
            val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_NETWORK_STATE_CHANGED.string)
            AppLog.i("Broadcasting Network State Changed")
            mContext!!.sendBroadcast(broadcastIntent)
        }
    }

    private fun notifyGPSStateChanged() {
        // Broadcast that data has changed
        val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_GPS_STATE_CHANGED.string)
        mContext!!.sendBroadcast(broadcastIntent)
    }

    private fun notifyNMEAChanged() {
        // Broadcast that data has changed
        val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_NMEA_CHANGED.string)
        mContext!!.sendBroadcast(broadcastIntent)
    }

    private fun notifyPassiveDataChanged() {
        if (mLocationManager != null) {
            if (checkLocationPermission()) return
            // Set the Network Location according to the Network Provider
            passiveData!!.location = mLocationManager!!
                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            // Broadcast that data has changed
            val broadcastIntent = Intent(GpsInfoConstants.Broadcasts.BROADCAST_PASSIVE_CHANGED.string)
            mContext!!.sendBroadcast(broadcastIntent)
        }
    }

    override fun onGpsStatusChanged(event: Int) {
        when (event) {
            GpsStatus.GPS_EVENT_STARTED -> gpsData?.setGPSEvent("GPS Started")

            GpsStatus.GPS_EVENT_STOPPED -> gpsData?.setGPSEvent("GPS Stopped")

            GpsStatus.GPS_EVENT_FIRST_FIX -> gpsData?.setGPSEvent("GPS First Fix")

            GpsStatus.GPS_EVENT_SATELLITE_STATUS -> gpsData?.setGPSEvent("Signal Detected")
            else -> gpsData?.setGPSEvent("Inactive")
        }

        val satellites = ArrayList<GpsSatellite>()
        var countSatellitesInFix = 0
        if (checkLocationPermission()) return
        for (sat in Objects.requireNonNull(mLocationManager!!.getGpsStatus(null)).satellites) {
            if (sat.usedInFix()) {
                countSatellitesInFix++
            }
            satellites.add(sat)
        }
        gpsData?.setSatellites(satellites)
        gpsData?.setSatellitesInFix(countSatellitesInFix)
        notifyGPSDataChanged()
        notifyGPSStateChanged()
    }

    override fun onLocationChanged(location: Location) {
        notifyGPSDataChanged()
        notifyNetworkDataChanged()
        notifyPassiveDataChanged()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        notifyNetworkStateChanged()
    }

    override fun onProviderEnabled(provider: String) {
        notifyNetworkStateChanged()
    }

    override fun onProviderDisabled(provider: String) {
        notifyNetworkStateChanged()
    }

    override fun onNmeaReceived(timestamp: Long, nmea: String) {}


    private fun requestGPSLocationUpdates() {
        AppLog.d(
            "Requesting GPS Updates with time between updates " + MIN_TIME_BW_UPDATES
                    + " and min distance change for updates " + MIN_DISTANCE_CHANGE_FOR_UPDATES
        )
        if (!mLocationManager!!.allProviders.contains(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                mContext, "GPS Provider not supported on this Device",
                Toast.LENGTH_LONG
            ).show()
            AppLog.e("No GPS Provider")
            return
        }
        if (checkLocationPermission()) return
        mLocationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
        )
        AppLog.d("GPS Updates Enabled")
    }

    /**
     * Notifies the location manager to request updates using predefined parameters
     */
    private fun requestNetworkLocationUpdates() {
        AppLog.d(
            "Requesting WiFi Updates with time between updates " + MIN_TIME_BW_UPDATES
                    + " and min distance change for updates " + MIN_DISTANCE_CHANGE_FOR_UPDATES
        )
        if (!mLocationManager!!.allProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(
                mContext, "Network Provider not supported on this Device",
                Toast.LENGTH_LONG
            ).show()
            AppLog.e("No Network Provider")
            return
        }
        if (checkLocationPermission()) return
        mLocationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
        )
        AppLog.d("Network Updates Enabled")
    }

    private fun requestNetworkUpdate() {
        AppLog.d("Requesting Network Status Update")
        if (mLocationManager != null) {
            notifyNetworkStateChanged()
        } else {
            mLocationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            notifyNetworkStateChanged()
        }
    }

    /**
     * Notifies the location manager to request updates using predefined parameters
     */
    private fun requestPassiveLocationUpdates() {
        AppLog.d(
            "Requesting Passive Updates with time between updates " + MIN_TIME_BW_UPDATES
                    + " and min distance change for updates " + MIN_DISTANCE_CHANGE_FOR_UPDATES
        )
        if (!mLocationManager!!.allProviders.contains(LocationManager.PASSIVE_PROVIDER)) {
            Toast.makeText(
                mContext, "Passive Provider not supported on this Device",
                Toast.LENGTH_LONG
            ).show()
            AppLog.e("No Passive Provider")
            return
        }
        if (checkLocationPermission()) return
        mLocationManager!!.requestLocationUpdates(
            LocationManager.PASSIVE_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
        )
        AppLog.d("Passive Updates Enabled")
    }

    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContext!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext!!.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        } else false
    }

    fun setContext(mContext: Context) {
        AppLog.d("Setting GpsSetting Context " + mContext.applicationContext.packageName)
        this.mContext = mContext.applicationContext

    }

    @SuppressLint("MissingPermission")
    fun startCollectingLocationData() {
        AppLog.d("Attempting to Start GPS")
        // Initialise the location manager for GPS collection
        mLocationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (mLocationManager != null) {
            mLocationManager!!.addGpsStatusListener(this)
        }


        requestGPSLocationUpdates()
        requestNetworkLocationUpdates()
        requestPassiveLocationUpdates()
        requestNetworkUpdate()
    }

    fun stopCollectingLocationData() {
        AppLog.d("Attempting to Stop GPS")
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(this)
            mLocationManager!!.removeGpsStatusListener(this)

        }
    }

    companion object {

        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 0
        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 2000
        /**
         * @return Instance of the GpsSetting
         */
        @SuppressLint("StaticFieldLeak")
        @Volatile
        var instance: GpsSetting? = null
            get() {
                if (field == null) {
                    synchronized(GpsSetting::class.java) {
                        if (field == null) {
                            instance = GpsSetting()
                        }
                    }
                }
                return field
            }

        /**
         * Gets the state of Airplane Mode.
         *
         * @return true if enabled.
         */
        private fun isAirplaneModeOn(context: Context): Boolean {
            val contentResolver = context.contentResolver
            return Settings.System.getInt(contentResolver, AIRPLANE_MODE_ON, 0) != 0
        }
    }


}
