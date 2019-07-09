package com.golriz.gpstracker.Core

import android.app.Notification
import android.app.Notification.Builder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.golriz.gpstracker.R
import com.golriz.gpstracker.utils.SettingsLocationTracker

@Suppress("DEPRECATION")
class NotificationCreator(val context: Context, private val service: Service) {
    fun createNotification() {
        val mBuilder = Builder(
            context
        )
        val notification: Notification?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = mBuilder.setSmallIcon(R.drawable.ic_launcher).setTicker("Tracking").setWhen(0)
                .setAutoCancel(false)
                .setCategory(Notification.EXTRA_BIG_TEXT)
                .setContentTitle(SettingsLocationTracker.NotificationTitle)
                .setContentText(SettingsLocationTracker.NotificationText)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setStyle(
                    Notification.BigTextStyle()
                        .bigText(SettingsLocationTracker.NotificationText)
                )
                .setChannelId(SettingsLocationTracker.NotificationChannelId)
                .setShowWhen(true)
                .setOngoing(true)
                .build()
        } else {
            notification =
                mBuilder.setSmallIcon(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) R.drawable.ic_launcher else R.drawable.ic_launcher)
                    .setTicker(SettingsLocationTracker.NotificationTicker).setWhen(0)
                    .setAutoCancel(false)
                    .setContentTitle(SettingsLocationTracker.NotificationTitle)
                    .setContentText(SettingsLocationTracker.NotificationText)
                    .setStyle(
                        Notification.BigTextStyle()
                            .bigText(SettingsLocationTracker.NotificationText)
                    )
                    .setOngoing(true)
                    .build()
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(
                    SettingsLocationTracker.NotificationChannelId,
                    SettingsLocationTracker.NotificationTicker,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(mChannel)
        }
        /*assert notificationManager != null;
        notificationManager.notify(0, notification);*/
        service.startForeground(1, notification) //for foreground service, don't use 0 as id. it will not work.
    }
}