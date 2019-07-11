package com.golriz.gpstracker.utils


object SettingsLocationTracker {

    const val PERMISSION_ACCESS_LOCATION_CODE = 99
    const val LOCATION_MESSAGE = "LOCATION_DATA"
    const val ACTION_CURRENT_LOCATION_BROADCAST = "current.location"
    const val ACTION_PERMISSION_DEINED = "location.denied"
    const val TAG = "Location Tracker"
    const val Pref_Action = "ACTION"
    const val PrefNewLocationInterval = "LOCATION_INTERVAL"
    const val PrefDistanceBetweenLastPoint = "LOCATION_Distance"
    const val PrefSyncInterval = "Sync_INTERVAL"
    const val PrefSyncItemCount = "Sync_Count"
    const val PrefIsUsingGps = "GPS"
    const val PrefIsUsingWifi = "NETWORK"
    const val Pref_PopulateDb = "Populate DB"
    const val PrefIsServiceRunning = "running_service"


    const val NotificationTitle = "گلریز"
    const val NotificationText = "سامانه دستیار گلریز"
    const val NotificationChannelId = "notification_channel"
    const val NotificationTicker = "Tracking"

    const val startLocation = "StartPoint"
    const val endLocation = "EndPoint"



}
