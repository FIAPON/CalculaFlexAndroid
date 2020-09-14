package br.com.heiderlopes.calculaflex.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import br.com.heiderlopes.calculaflex.R

class NotificationManagerUtils(
    private val context: Context
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createMainNotificationChannel(channelId: String, channelName: String, channelDesc: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelId, channelName, importance)
        mChannel.description = channelDesc
        mChannel.enableLights(true)
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
    }

    private fun createNotificationCompatBuilder(
        channelID: String
    ): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, channelID)
        } else {
            NotificationCompat.Builder(context)
        }
    }

    fun showNotification(
        notificationID: Int,
        pendingIntent: PendingIntent?,
        channelID: String,
        title: String,
        message: String
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder =
            createNotificationCompatBuilder(channelID)
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(notificationID, mBuilder.build())
    }
}
