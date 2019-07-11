package com.golriz.gpstracker.core

import com.golriz.gpstracker.db.model.UserCurrentLocation

interface UnSyncedLocations {

    fun UnSynchedLcoations(userCurrentLocations: List<UserCurrentLocation>)
}
