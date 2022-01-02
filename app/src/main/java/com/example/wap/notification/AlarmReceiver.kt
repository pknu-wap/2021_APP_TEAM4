package com.example.wap.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.wap.MainActivity
import com.example.wap.notification.Constants.Companion.NOTIFICATION_ID
import com.example.wap.R

private const val CHANNEL_ID = "alarm_channel"

class AlarmReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        val todo = intent.getStringExtra("todo")
        val date = intent.getStringExtra("deadline")
        val intent = Intent(context, MainActivity::class.java)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0 ,intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(date)
            .setContentText(todo)
            .setSmallIcon(R.drawable.bell)
            .setColor(ContextCompat.getColor(context, R.color.black))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply{
            description = "My channel description"
            enableLights(true)
            lightColor = Color.LTGRAY
        }
        notificationManager.createNotificationChannel(channel)
    }
}