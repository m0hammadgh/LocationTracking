package com.golriz.locationtracker

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.db.repository.RoomRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        GlobalBus.bus?.register(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        btnShowLocation.setOnClickListener {
            val locations = RoomRepository(this).getAllLocation()
            for (u in locations) {
                val sydney = LatLng(u.latitude!!, u.longtitude!!)
                mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))

            }

        }


    }

    override fun onStop() {
        super.onStop()
        GlobalBus.bus?.unregister(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun getMessage(fragmentActivityMessage: Events.SendLocation) {
        val location = fragmentActivityMessage.locationList


    }

}
