package com.golriz.gpstracker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefDistanceBetweenLastPoint
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefIsUsingGps
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefIsUsingWifi
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefNewLocationInterval
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefSyncInterval
import com.golriz.gpstracker.Core.SettingsLocationTracker.PrefSyncItemCount
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_Action
import com.golriz.gpstracker.Core.SettingsLocationTracker.Pref_PopulateDb


class SharedPrefManager
@SuppressLint("CommitPrefEdits")
constructor(private val context: Context) {

    private var pr: SharedPreferences = context.getSharedPreferences("GpsLocationTracker", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor? = pr.edit()


    /*******  Using GPS ******/

    val getIsUsingGps: Boolean?
        get() = pr.getBoolean(PrefIsUsingGps, false)

    fun setIsUsingGps(state: Boolean?) {
        editor?.putBoolean(PrefIsUsingGps, state!!)
        editor?.apply()
        editor?.commit()

    }

    /******* Using Wifi  ******/

    val getIsUsingWifi: Boolean?
        get() = pr.getBoolean(PrefIsUsingWifi, false)

    fun setIsUsingWifi(state: Boolean?) {
        editor?.putBoolean(PrefIsUsingWifi, state!!)
        editor?.apply()
        editor?.commit()

    }

    /*******  New Location Duration ******/


    val getNewLocationInterval: Long?
        get() = pr.getLong(PrefNewLocationInterval, 10000)

    fun setNewLocationInterval(interval: Long) {
        editor?.putLong(PrefNewLocationInterval, interval)
        editor?.apply()

    }

    /******* Minimum Distance Between Last DB record and Current Location ******/

    val getNewLocationDistance: Int?
        get() = pr.getInt(PrefDistanceBetweenLastPoint, 50)

    fun setNewLocationDistance(interval: Int) {
        editor?.putInt(PrefDistanceBetweenLastPoint, interval)
        editor?.apply()

    }

    /******* TIme Interval To Sync Data To Server ******/
    val getSyncInterval: Long?
        get() = pr.getLong(PrefSyncInterval, 60000)

    fun setSyncInterval(interval: Long) {
        editor?.putLong(PrefSyncInterval, interval)
        editor?.apply()

    }

    /******* Number of DB Records To Sync To The Server ******/
    val getSyncItemCount: Int?
        get() = pr.getInt(PrefSyncItemCount, 10)

    fun setSyncItemCount(interval: Int) {
        editor?.putInt(PrefSyncItemCount, interval)
        editor?.apply()

    }

    /******* Location Action ******/
    val getLocationAction: String?
        get() = pr.getString(Pref_Action, null)

    fun setLocationAction(action: String) {
        editor?.putString(Pref_Action, action)
        editor?.apply()

    }

    /******* Is DB populated ******/
    val getIsPopulatedDb: Boolean?
        get() = pr.getBoolean(Pref_PopulateDb, false)

    fun setDBPopulated(state: Boolean) {
        editor?.putBoolean(Pref_PopulateDb, state)
        editor?.apply()

    }

}