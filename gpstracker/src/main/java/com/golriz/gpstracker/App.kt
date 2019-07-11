package com.golriz.gpstracker

import android.app.Application
import com.golriz.gpstracker.gpsInfo.GpsSetting

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        GpsSetting.instance?.setContext(applicationContext)

    }
}