package com.golriz.gpstracker.BroadCast

import android.location.Location

class Events {


    // Event used to send message from activity to fragment.
    class ActivityFragmentMessage(val location: Location)

}