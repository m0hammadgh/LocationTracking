package com.golriz.gpstracker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.golriz.gpstracker.enums.LocationSharedPrefEnums


class LocationSharePrefUtil
@SuppressLint("CommitPrefEdits")
constructor(context: Context) {
    fun customPrefs(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private var pr: SharedPreferences = context.getSharedPreferences("GpsLocationTracker", Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = pr.edit()


    fun saveToSharedPref(key: LocationSharedPrefEnums, type: Any) {

        when (type) {
            is String -> editor.putString(key.name, type)
            is Int -> editor.putInt(key.name, type)
            is Long -> editor.putLong(key.name, type)
            is Boolean -> editor.putBoolean(key.name, type)
            is Float -> editor.putFloat(key.name, type)
        }
        editor.apply()
    }

    fun getLocationItem(key: LocationSharedPrefEnums, defaultValue: Any): Any? {
        return when (defaultValue) {
            is String -> pr.getString(key.name, null)
            is Int -> pr.getInt(key.name, 0)
            is Long -> pr.getLong(key.name, 0L)
            is Boolean -> pr.getBoolean(key.name, false)
            is Float -> pr.getFloat(key.name, 0.0f)
            else -> null
        }

    }
}


