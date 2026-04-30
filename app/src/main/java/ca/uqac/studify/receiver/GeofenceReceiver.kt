package ca.uqac.studify.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("STUDIFY_GPS", " GeofenceReceiver déclenché")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("STUDIFY_GPS", "Événement geofencing nul")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMsg = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("STUDIFY_GPS", "Erreur geofence : $errorMsg")
            return
        }

        val transition = geofencingEvent.geofenceTransition
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val locationName = triggeringGeofences?.firstOrNull()?.requestId ?: "Ta destination"
            Log.d("STUDIFY_GPS", " Entrée détectée : $locationName")
            showNotification(context, locationName)
        } else {
            Log.d("STUDIFY_GPS", "Transition ignorée : $transition")
        }
    }

    private fun showNotification(context: Context, locationName: String) {
        val channelId = "location_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Localisation",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Studify")
            .setContentText("📍 Tu es arrivé à : $locationName")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("STUDIFY_GPS", "Notification envoyée")
    }
}