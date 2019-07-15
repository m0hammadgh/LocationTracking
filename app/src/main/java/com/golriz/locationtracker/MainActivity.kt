package com.golriz.locationtracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.core.LocationTracker
import com.golriz.gpstracker.utils.SettingsLocationTracker.PERMISSION_ACCESS_LOCATION_CODE
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {


    var locationTracker: LocationTracker? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        btnStop.setOnClickListener {
            init()
            locationTracker?.start(baseContext, this)
        }

        btnStart.setOnClickListener {
            stopService()
            tvGpsMode.text = ""
        }


    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        if (validate()) {
            locationTracker = LocationTracker(this)
                    .setNewPointInterval(edtInterval.text.toString().toLong())
                    .setOnlyGpsMode(true)
                    .setMinDistanceBetweenLocations(edtMinDistance.text.toString().toInt())
                    .setCountOfSyncItems(edtSyncCount.text.toString().toInt())
                    .setSyncToServerInterval(edtSynInterval.text.toString().toLong())
                    .setHighAccuracyMode(true)
            tvGpsMode.text = "Current Gps status : ${locationTracker?.getGpsStatus(this)}"

        }

    }

    private fun validate(): Boolean {
        return !TextUtils.isEmpty(edtInterval.text) && !TextUtils.isEmpty(edtMinDistance.text) && !TextUtils.isEmpty(
                edtSynInterval.text
        ) && !TextUtils.isEmpty(edtSyncCount.text)
    }


    private fun stopService() {
        locationTracker?.stopLocationService(this)
    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {

        LocationTracker(this).onRequestPermission(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ACCESS_LOCATION_CODE) {
            (locationTracker?.start(baseContext, this))


        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getMessage(fragmentActivityMessage: Events.SendLocation) {
        val location = fragmentActivityMessage.locationList


    }


}
