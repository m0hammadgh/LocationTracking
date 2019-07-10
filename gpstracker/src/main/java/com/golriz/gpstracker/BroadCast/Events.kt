package com.golriz.gpstracker.BroadCast

import com.golriz.gpstracker.DB.model.UserCurrentLocation

class Events {



    class SendLocation(val locationList: List<UserCurrentLocation>)

}