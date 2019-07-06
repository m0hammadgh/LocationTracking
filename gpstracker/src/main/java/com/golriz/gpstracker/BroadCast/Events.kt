package com.golriz.gpstracker.BroadCast

import android.location.Location
import com.golriz.gpstracker.DB.model.UserCurrentLocation

class Events {


    // Event used to send message from activity to fragment.
    class ActivityFragmentMessage(val location: Location)

    class SendLocation(val locationList: List<UserCurrentLocation>)

}