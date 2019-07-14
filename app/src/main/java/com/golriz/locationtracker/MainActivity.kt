package com.golriz.locationtracker

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.core.LocationTracker
import com.golriz.gpstracker.gpsInfo.GpsSetting
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import com.golriz.gpstracker.utils.SettingsLocationTracker.actionReceiverName
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


    }

    private fun init() {
        locationTracker = LocationTracker(actionReceiverName, this)
            .setNewPointInterval(20000)
            .setOnlyGpsMode(true)
            .setMinDistanceBetweenLocations(50)
            .setCountOfSyncItems(20)
            .setSyncToServerInterval(100000)
            .setHighAccuracyMode(true)

    }


    private fun stopService() {
        locationTracker?.stopLocationService(this)
    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        LocationTracker(actionReceiverName, this).onRequestPermission(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ACCESS_LOCATION_CODE) {
            (locationTracker?.start(baseContext, this))
            GpsSetting.instance?.startCollectingLocationData()

        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getMessage(fragmentActivityMessage: Events.SendLocation) {
        val location = fragmentActivityMessage.locationList


    }


}
