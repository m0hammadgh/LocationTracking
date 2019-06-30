package com.golriz.locationtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.golriz.gpspointer.Config.LocationTracker

class MainActivity : AppCompatActivity() {
    private var locationTracker: LocationTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}


//        findViewById<View>(R.id.btnStop).setOnClickListener {
//            locationTracker = LocationTracker("my.action", this@MainActivity)
//                .setInterval(5000)
//                .setGps(true)
//                .setNetWork(true)
//                .start(getBaseContext())
//        }
//
//        findViewById<View>(R.id.btnStart).setOnClickListener {
//            val locationTrack = SampleTrack(this@MainActivity)
//
//
//            if (locationTrack.canGetLocation()) {
//
//
//                val longitude = locationTrack.getLongitude()
//                val latitude = locationTrack.getLatitude()
//
//                Toast.makeText(
//                    getApplicationContext(),
//                    "Longitude:" + java.lang.Double.toString(longitude) + "\nLatitude:" + java.lang.Double.toString(
//                        latitude
//                    ),
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//
//                locationTrack.showSettingsAlert()
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
//        locationTracker!!.onRequestPermission(requestCode, permissions, grantResults)
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        locationTracker = LocationTracker("my.action", this@MainActivity)
//            .setInterval(5000)
//            .setGps(true)
//            .setNetWork(true)
//            .start(getBaseContext())
//    }
//
//    companion object {
//
//        private val TAG_PERMISSION_CODE = 10052
//    }

//}
