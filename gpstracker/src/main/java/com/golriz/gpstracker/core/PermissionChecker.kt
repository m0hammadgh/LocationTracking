package com.golriz.gpstracker.core

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionChecker(private val context: Activity) {
    private val TAG_PERMISSION_CODE = 8585


    //  CHECK FOR LOCATION PERMISSION
    fun checkPermission(activity: Activity): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    //REQUEST FOR PERMISSSION
    fun requestPermission(activity: Activity, code: Int) {

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




