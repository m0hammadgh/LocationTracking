package com.golriz.gpspointer.Config

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import java.util.Timer
import java.util.TimerTask

import android.content.ContentValues.TAG

class LocationTrackerService : Service, LocationListener {
    var counter = 0
    internal lateinit var context: Context
    private var newLocationDistance = 0
    private var newLocationTimeInterval = 0
    private var syncToserverTimeInterval = 0
    private var numberOfRecordsToSync = 0

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    constructor(
        applicationContext: Context,
        newLocationDistance: Int,
        newLocationTimeInterval: Int,
        syncToserverTimeInterval: Int,
        numberOfRecordsToSync: Int
    ) : super() {
        context = applicationContext
        this.newLocationDistance = newLocationDistance
        this.newLocationTimeInterval = newLocationTimeInterval
        this.syncToserverTimeInterval = syncToserverTimeInterval
        this.numberOfRecordsToSync = numberOfRecordsToSync

    }

    constructor(context: Context) {
        this.context = context
    }

    constructor() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i("EXIT", "ondestroy!")

        val broadcastIntent = Intent("service.locationTracker.stopped")
        sendBroadcast(broadcastIntent)
        stoptimertask()

    }

    fun startTimer() {
        Log.d(TAG, "startTimer: ")
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("in timer", "in timer ++++  " + counter++)
            }
        }
    }

    fun stoptimertask() {
        Log.d(TAG, "stoptimertask: ")

        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }

    companion object {
        var stopTime = false
    }
}