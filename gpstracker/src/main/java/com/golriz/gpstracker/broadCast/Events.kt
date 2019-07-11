package com.golriz.gpstracker.broadCast

import com.golriz.gpstracker.db.model.UserCurrentLocation

class Events {



    class SendLocation(val locationList: List<UserCurrentLocation>)

}