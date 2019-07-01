package com.golriz.locationtracker

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.golriz.gpstracker.Core.LocationTracker
import com.golriz.gpstracker.Core.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var locationTracker: LocationTracker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        btnStop.setOnClickListener {


            if (locationTracker!!.validatePermissions(baseContext, this)) {
                (locationTracker?.start(baseContext))
            } else {
                locationTracker?.askPermissions(baseContext, this)

            }
        }

        btnStart.setOnClickListener {
            locationTracker?.stopLocationService(this)
        }

    }

    private fun init() {

        locationTracker = LocationTracker("my.action.mohammad")
                .setInterval(5000)
                .setGps(true)
                .setDistance(50)
                .setNetWork(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        LocationTracker("my.action.mohammad").onRequestPermission(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ACCESS_LOCATION_CODE) {
            (locationTracker?.start(baseContext))
        }

    }

    companion object {

        private val TAG_PERMISSION_CODE = 10052
    }


}
