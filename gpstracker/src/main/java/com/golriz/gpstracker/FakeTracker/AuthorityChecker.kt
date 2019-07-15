package com.golriz.gpstracker.FakeTracker

import android.content.Context
import com.golriz.gpstracker.enums.FakeMode

class AuthorityChecker(val context: Context) {
    fun check(): FakeMode {
        return when {
            FakeApplicationManager(context).checkForApps() -> FakeMode.FakeApplication
            DeveloperModeStatus().isDevMode(context) -> FakeMode.DeveloperMode
            else -> FakeMode.None
        }

    }
}