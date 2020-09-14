package br.com.heiderlopes.calculaflex.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.heiderlopes.calculaflex.MainActivity
import br.com.heiderlopes.calculaflex.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CalculaFlexFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        selectNotification(p0)
    }

    private fun selectNotification(p0: RemoteMessage) {
        val isDeeplink = p0.data.containsKey("deeplink")

        val title = p0.data["title"] ?: p0.notification?.title
        val message = p0.data["message"] ?: p0.notification?.body

        val intent =
            if (isDeeplink) Intent(Intent.ACTION_VIEW, Uri.parse(p0.data["deeplink"])) else Intent(
                this,
                MainActivity::class.java
            )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        sendNotification(intent, title, message)
    }

    private fun sendNotification(intent: Intent, title: String?, message: String?) {

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channel = getString(R.string.default_notification_channel_id)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setSound(sound)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channel,
                "CALCULA FLEX AVISOS",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, notification.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}