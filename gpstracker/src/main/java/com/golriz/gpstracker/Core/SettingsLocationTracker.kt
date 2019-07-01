package com.golriz.gpstracker.Core


object SettingsLocationTracker {

    val PERMISSION_ACCESS_LOCATION_CODE = 99

    val LOCATION_MESSAGE = "LOCATION_DATA"

    val ACTION_CURRENT_LOCATION_BROADCAST = "current.location"

    val ACTION_PERMISSION_DEINED = "location.deined"


    var Pref_Action = "ACTION"
    var Pref_Location_Interval = "LOCATION_INTERVAL"
    var Pref_Last_Point_Distance = "LOCATION_Distance"
    var Pref_Sync_Time = "Sync_INTERVAL"
    var Pref_Sync_Count = "Sync_Count"
    var Pref_Gps = "GPS"
    var Pref_Internet = "NETWORK"

    var isServiceRunning: Boolean = false


}
