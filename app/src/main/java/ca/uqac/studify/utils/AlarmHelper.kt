package ca.uqac.studify.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleNotification(
    context: Context,
    notificationId: Int,
    timeInMillis: Long,
    title: String,
    message: String
) {
    val intent = Intent(context, ca.uqac.studify.receiver.TaskReminderReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("message", message)
        putExtra("notificationId", notificationId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}