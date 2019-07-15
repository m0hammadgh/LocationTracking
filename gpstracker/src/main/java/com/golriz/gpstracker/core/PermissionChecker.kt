package com.golriz.gpstracker.core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE

class PermissionChecker {


    //  CHECK FOR LOCATION PERMISSION
    fun checkPermission(activity: Activity): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermission(activity, PERMISSION_ACCESS_LOCATION_CODE)
            return false
        }
    }

    //REQUEST FOR PERMISSION
    private fun requestPermission(activity: Activity, code: Int) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
        ) {

            Toast.makeText(
                    activity,
                    "GPS permission ",
                    Toast.LENGTH_LONG
            ).show()

        } else {

            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), code)
        }
    }

}




