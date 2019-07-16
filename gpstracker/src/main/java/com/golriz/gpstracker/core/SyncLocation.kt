package com.golriz.gpstracker.core

import android.content.Context
import com.golriz.gpstracker.broadCast.Events
import com.golriz.gpstracker.broadCast.GlobalBus
import com.golriz.gpstracker.db.repository.RoomRepository
import com.golriz.gpstracker.model.SharePrefSettings
import com.golriz.gpstracker.utils.LocationSettings.timeThreadName
import java.util.*
import kotlin.concurrent.fixedRateTimer

class SyncLocation(private val sharePrefSettings: SharePrefSettings, val context: Context) {
    fun startSyncProcess(): Timer {
        return fixedRateTimer(timeThreadName, false, 0L, this.sharePrefSettings.syncToServerInterval) {
            val locations = RoomRepository(context).getUnSyncedLocations(sharePrefSettings.syncItemCount)
            val activityFragmentMessageEvent = Events.SendLocation(locations)
            GlobalBus.bus?.post(activityFragmentMessageEvent)

        }
    }
}