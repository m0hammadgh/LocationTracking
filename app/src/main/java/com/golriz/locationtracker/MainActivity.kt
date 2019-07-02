package com.golriz.locationtracker

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.golriz.gpstracker.Core.CalculateLocationDistance
import com.golriz.gpstracker.Core.LocationTracker
import com.golriz.gpstracker.Core.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import com.golriz.gpstracker.DB.model.UserCurrentLocation
import com.golriz.gpstracker.DB.repository.NoteRepository
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var locationTracker: LocationTracker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        Toast.makeText(this, "${calcutePoints()}", Toast.LENGTH_LONG).show()
        btnStop.setOnClickListener {


            if (locationTracker!!.validatePermissions(baseContext, this)) {
                (locationTracker?.start(baseContext))
            } else {
                locationTracker?.askPermissions(baseContext, this)

            }
        }

        btnStart.setOnClickListener {

        }

    }

    private fun init() {

        locationTracker = LocationTracker("my.action.mohammad")
            .setInterval(5000)
            .setGps(true)
            .setDistance(50)
            .setNetWork(true)
    }


    private fun stopService() {
        locationTracker?.stopLocationService(this)
    }

    private fun addDataToDB() {
        NoteRepository(this).insertTask(123.565, 15.6663)
    }

    private fun getLocationList() {
        NoteRepository(this).tasks.observe(this,
            Observer<List<UserCurrentLocation>> { locations ->
                if (locations.isNotEmpty()) {

                }
            })
    }


    private fun calcutePoints(): Float {
        var location1: Location = Location("Location 1")
        var location2: Location = Location("Location 2")
        location1.latitude = 36.342247
        location1.longitude = 59.555205

        location2.latitude = 36.342126
        location2.longitude = 59.555763
        return CalculateLocationDistance(location1, location2).calculateDistance()
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
