package com.golriz.locationtracker

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.core.LocationTracker
import com.golriz.gpstracker.db.model.UserCurrentLocation
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.gpsInfo.GpsSetting
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


        val i = GpsSetting.instance?.gpsData?.satellitesSize


    }


    private fun stopService() {
        locationTracker?.stopLocationService(this)
    }

    private fun addDataToDB() {
        RoomRepository(this).insertTask(123.565, 15.6663, null)
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


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        LocationTracker("my.action.mohammad").onRequestPermission(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ACCESS_LOCATION_CODE) {
            (locationTracker?.start(baseContext, this))
            GpsSetting.instance?.startCollectingLocationData()

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
