package ca.uqac.studify.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("STUDIFY_GPS", "⚡ Le GeofenceReceiver a été réveillé par le système !")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e("STUDIFY_GPS", " Erreur : L'événement GPS est nul.")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("STUDIFY_GPS", " Erreur GPS interne : $errorMessage")
            return
        }

        // Si l'utilisateur ENTRE dans la zone
        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val locationName = triggeringGeofences?.get(0)?.requestId ?: "Ta destination"

            Log.d("STUDIFY_GPS", " Entrée détectée dans la zone : $locationName ! Affichage de la notification...")

            showNotification(context, locationName)
        } else {
            Log.d("STUDIFY_GPS", " Transition ignorée : ${geofencingEvent.geofenceTransition}")
        }
    }

    private fun showNotification(context: Context, locationName: String) {
        try {
            val channelId = "location_channel"
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                channelId,
                "Localisation",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Studify")
                .setContentText("📍 Tu es arrivé(e) à : $locationName")
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            manager.notify(System.currentTimeMillis().toInt(), notification)
            Log.d("STUDIFY_GPS", " Notification envoyée avec succès !")
        } catch (e: Exception) {
            Log.e("STUDIFY_GPS", " Erreur lors de l'affichage de la notification : ${e.message}")
        }
    }
}