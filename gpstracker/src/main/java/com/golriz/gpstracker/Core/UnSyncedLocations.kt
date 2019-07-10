package com.golriz.gpstracker.Core

import com.golriz.gpstracker.DB.model.UserCurrentLocation

interface UnSyncedLocations {

    fun UnSynchedLcoations(userCurrentLocations: List<UserCurrentLocation>)
}
