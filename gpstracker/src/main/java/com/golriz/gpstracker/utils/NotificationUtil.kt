package com.golriz.gpstracker.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.golriz.gpstracker.R
import com.golriz.gpstracker.utils.LocationSettings.NotificationText
import com.golriz.gpstracker.utils.LocationSettings.NotificationTitle


class NotificationUtil(val context: Context, private val service: Service) {
    fun createForeGroundService() {
        val mBuilder = NotificationCompat.Builder(context, "")

        service.startForeground(1, createNotification(mBuilder))
    }

    private fun createNotification(mBuilder: NotificationCompat.Builder): Notification {
        val notification: Notification?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker(NotificationTitle).setWhen(0)
                .setAutoCancel(false)
                .setCategory(Notification.EXTRA_BIG_TEXT)
                .setContentTitle(NotificationTitle)
                .setContentText(NotificationText)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setChannelId(LocationSettings.NotificationChannelId)
                .setShowWhen(true)
                .setOngoing(true)
                .build()
        } else {
            notification =
                mBuilder.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) R.drawable.ic_launcher else R.drawable.ic_launcher)
                    .setTicker(LocationSettings.NotificationTicker).setWhen(0)
                    .setAutoCancel(false)
                    .setContentTitle(NotificationTitle)
                    .setContentText(NotificationText)
                    .setOngoing(true)
                    .build()
        }
        createNotificationChannel()

        return notification
    }

    private fun createNotificationChannel() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(
                    LocationSettings.NotificationChannelId,
                    LocationSettings.NotificationTicker,
                    NotificationManager.IMPORTANCE_HIGH
                )
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(mChannel)
        }
    }


}