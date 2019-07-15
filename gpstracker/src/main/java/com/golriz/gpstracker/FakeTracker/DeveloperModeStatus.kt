package com.golriz.gpstracker.FakeTracker

import android.content.Context
import android.os.Build
import android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED
import android.provider.Settings.Secure.getInt

class DeveloperModeStatus {
    @android.annotation.TargetApi(17)
    fun isDevMode(context: Context): Boolean {
        return when {
            Integer.valueOf(Build.VERSION.SDK_INT) >= 17 -> getInt(
                    context.contentResolver,
                    DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
            else -> false
        }
    }
}