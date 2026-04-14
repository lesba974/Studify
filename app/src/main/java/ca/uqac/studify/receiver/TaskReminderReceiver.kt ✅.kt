package ca.uqac.studify.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title") ?: "Rappel"
        val message = intent.getStringExtra("message") ?: "C'est l'heure !"
        // On récupère l'ID (par défaut 1 si on ne le trouve pas)
        val notificationId = intent.getIntExtra("notificationId", 1)

        val channelId = "task_channel"

        val channel = NotificationChannel(
            channelId,
            "Rappels",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {

                notificationManager.notify(notificationId, notification)
            }
        } else {

            notificationManager.notify(notificationId, notification)
        }
    }
}