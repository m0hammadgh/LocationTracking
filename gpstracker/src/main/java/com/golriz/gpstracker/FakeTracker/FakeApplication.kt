package com.golriz.gpstracker.fakeTracker

import android.content.Context
import android.content.pm.PackageManager


class FakeApplication(val context: Context) {
    fun checkForApps(): Boolean {
        val pm = context.packageManager
        for (s: String in FakePackagesConfig().getPackages()) {
            if (isPackageInstalled(s, pm))
                return true
        }
        return false
    }


    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        var found = true
        try {

            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {

            found = false
        }
        return found
    }
}
