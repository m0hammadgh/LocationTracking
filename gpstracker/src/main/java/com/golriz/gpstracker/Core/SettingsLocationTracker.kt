package com.golriz.gpstracker.Core


object SettingsLocationTracker {

    val PERMISSION_ACCESS_LOCATION_CODE = 99

    val LOCATION_MESSAGE = "LOCATION_DATA"

    val ACTION_CURRENT_LOCATION_BROADCAST = "current.location"

    val ACTION_PERMISSION_DEINED = "location.deined"


    var Pref_Action = "ACTION"
    var PrefNewLocationInterval = "LOCATION_INTERVAL"
    var PrefDistanceBetweenLastPoint = "LOCATION_Distance"
    var PrefSyncInterval = "Sync_INTERVAL"
    var PrefSyncItemCount = "Sync_Count"
    var PrefIsUsingGps = "GPS"
    var PrefIsUsingWifi = "NETWORK"
    var Pref_PopulateDb = "Populate DB"

    var isServiceRunning: Boolean = false


}
