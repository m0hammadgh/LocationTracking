package com.golriz.gpspointer.Config

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionChecker(private val context: Activity) {
    private val TAG_PERMISSION_CODE = 8585

    object CheckPermission {

        //  CHECK FOR LOCATION PERMISSION
        internal fun checkPermission(activity: Activity): Boolean {
            val result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            return result == PackageManager.PERMISSION_GRANTED
        }


        //REQUEST FOR PERMISSSION
        internal fun requestPermission(activity: Activity, code: Int) {

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

    fun checkPermission() {
        if (!CheckPermission.checkPermission(context)) {
            CheckPermission.requestPermission(context, TAG_PERMISSION_CODE)
        }
    }


}
