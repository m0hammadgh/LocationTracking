package com.golriz.gpstracker

import android.app.Application
import com.golriz.gpstracker.GpsInfo.Singleton

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Singleton.getInstance().setContext(applicationContext)

    }
}