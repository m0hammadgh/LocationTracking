package com.golriz.gpstracker.BroadCast

import org.greenrobot.eventbus.EventBus

object GlobalBus {
    private var sBus: EventBus? = null

    val bus: EventBus?
        get() {
            if (sBus == null)
                sBus = EventBus.getDefault()
            return sBus
        }
}