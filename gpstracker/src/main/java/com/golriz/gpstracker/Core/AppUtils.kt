package com.golriz.gpspointer.Config

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log

/**
 * @author Mohammad
 */
object AppUtils {

    /**
     * return true If gooshi is Marshmallow ya balatar
     */
    fun hasM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        if (context != null) {
            Log.d("", "contextIsNotNull: ")
        }
        val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager ?: return false
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
