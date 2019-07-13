@file:Suppress("DEPRECATION", "UNREACHABLE_CODE")

package com.golriz.gpstracker.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log


object AppUtils {

    fun hasM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        if (context != null) {

            return false
            Log.d("", "contextIsNotNull: ")

        }
        val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


}
