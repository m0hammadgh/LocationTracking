package com.golriz.gpstracker.broadCast

import com.golriz.gpstracker.db.model.UserCurrentLocation
import com.golriz.gpstracker.model.UserActivity

class Events {
    class SendLocation(val locationList: List<UserCurrentLocation>)

    class SendActivityDetect(val userActivity: UserActivity)
}