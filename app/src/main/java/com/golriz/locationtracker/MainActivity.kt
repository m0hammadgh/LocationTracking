package com.golriz.locationtracker

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.golriz.gpstracker.BroadCast.Events
import com.golriz.gpstracker.BroadCast.GlobalBus
import com.golriz.gpstracker.Core.CalculateLocationDistance
import com.golriz.gpstracker.Core.LocationTracker
import com.golriz.gpstracker.DB.model.UserCurrentLocation
import com.golriz.gpstracker.DB.repository.RoomRepository
import com.golriz.gpstracker.GpsInfo.Singleton
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    var locationTracker: LocationTracker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()


        btnStop.setOnClickListener {

            locationTracker?.start(baseContext, this)
        }

        btnStart.setOnClickListener {
            stopService()
        }

        insertData.setOnClickListener {


        }

        getsize.setOnClickListener {

            Toast.makeText(this, "${RoomRepository(this).getLasSubmittedItem().latitude}", Toast.LENGTH_LONG).show()
//            getLocationList()
//            getLastInsertedItem()
        }

    }

    private fun init() {

        locationTracker = LocationTracker("my.action.mohammad")
            .setNewPointInterval(5000)
            .setOnlyGpsMode(true)
            .setMinDistanceBetweenLocations(50)
            .setCountOfSyncItems(5)
            .setSyncToServerInterval(5000)
            .setHighAccuracyMode(true)
        GlobalBus.bus?.register(this)

        Singleton.getInstance().startCollectingLocationData()

        val i = Singleton.getInstance().gpsData.satellitesSize


    }


    private fun stopService() {
        locationTracker?.stopLocationService(this)
    }

    private fun addDataToDB() {
        RoomRepository(this).insertTask(123.565, 15.6663)
    }

    private fun getLocationList() {

        RoomRepository(this).tasks.observe(
            this,
            Observer<List<UserCurrentLocation>> { locations ->
                Toast.makeText(this, "${locations.size}", Toast.LENGTH_LONG).show()
            })
    }

    private fun getLastInsertedItem() {
        RoomRepository(this).getLasSubmittedRecord().observe(this,
            Observer<UserCurrentLocation> { lastItem ->
                Toast.makeText(this, "${lastItem.latitude} ${lastItem.id}", Toast.LENGTH_LONG).show()


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
            (locationTracker?.start(baseContext, this))
        }

    }

    companion object {

        private val TAG_PERMISSION_CODE = 10052
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getMessage(fragmentActivityMessage: Events.SendLocation) {
        val location = fragmentActivityMessage.locationList


    }
}
