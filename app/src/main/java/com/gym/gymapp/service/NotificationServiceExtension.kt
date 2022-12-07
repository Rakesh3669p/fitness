package com.gym.gymapp.service

import android.content.Context
import androidx.core.app.NotificationCompat
import com.gym.gymapp.R
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler


class NotificationServiceExtension : OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(
        context: Context,
        notificationReceivedEvent: OSNotificationReceivedEvent
    ) {
        OneSignal.onesignalLog(
            OneSignal.LOG_LEVEL.VERBOSE, "OSRemoteNotificationReceivedHandler fired!" +
                    " with OSNotificationReceived: " + notificationReceivedEvent.toString()
        )
        val notification = notificationReceivedEvent.notification
        if (notification.actionButtons != null) {
            for (button in notification.actionButtons) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "ActionButton: $button")
            }
        }
        val mutableNotification = notification.mutableCopy()
        mutableNotification.setExtender { builder: NotificationCompat.Builder ->
            builder.setColor(
                context.resources.getColor(R.color.themeOrange)
            )
        }

        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        notificationReceivedEvent.complete(mutableNotification)
    }
}