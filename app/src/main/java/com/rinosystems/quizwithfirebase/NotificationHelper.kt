package com.rinosystems.quizwithfirebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

object NotificationHelper {


    fun displayNotification(context: Context, title: String, body: String) {

        val intent = Intent(context, PrincipalActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(context, PrincipalActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_muy_bien)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(PrincipalActivity.CHANNEL_ID, PrincipalActivity.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = PrincipalActivity.CHANNEL_DESC
            val manager = getSystemService(context,NotificationManager::class.java)
            manager!!.createNotificationChannel(channel)
        }

        val mNotificationMgr = NotificationManagerCompat.from(context)
        mNotificationMgr.notify(100, mBuilder.build())

    }




}