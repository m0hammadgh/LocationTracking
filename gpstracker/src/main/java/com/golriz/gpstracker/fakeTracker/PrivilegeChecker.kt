package com.golriz.gpstracker.fakeTracker

import android.content.Context
import com.golriz.gpstracker.enums.FakeMode

class PrivilegeChecker(val context: Context) {
    fun check(): FakeMode {
        return when {
            FakeApplication(context).checkForApps() -> FakeMode.FakeApplication
            DeveloperMode().isDevMode(context) -> FakeMode.DeveloperMode
            else -> FakeMode.None
        }

    }
}