package ca.uqac.studify.ui.screens.addEdit

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.receiver.GeofenceReceiver
import ca.uqac.studify.ui.screens.detail.getTodayISO
import ca.uqac.studify.utils.scheduleNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

class AddEditTaskViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    // États UI
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var category by mutableStateOf("Cours")
        private set
    var time by mutableStateOf("08:00")
        private set
    var location by mutableStateOf("")
        private set
    var periodicity by mutableStateOf("Une fois")
        private set
    var priority by mutableStateOf("Moyenne")
        private set
    private var currentTaskId: Long? = null
    var endTime by mutableStateOf("")
        private set
    var date by mutableStateOf(getTodayISO())
        private set
    var isReminderEnabled by mutableStateOf(true)
        private set

    // Pour éviter d'ajouter plusieurs geofences pour la même tâche
    private var lastAddedGeofenceId: String? = null

    fun setRepository(repo: TaskRepository) {
        repository = repo
    }

    fun updateTitle(newTitle: String) { title = newTitle }
    fun updateDescription(newDescription: String) { description = newDescription }
    fun updateCategory(newCategory: String) { category = newCategory }
    fun updateTime(newTime: String) { time = newTime }
    fun updateLocation(newLocation: String) { location = newLocation }
    fun updatePeriodicity(newPeriodicity: String) { periodicity = newPeriodicity }
    fun updatePriority(newPriority: String) { priority = newPriority }
    fun updateEndTime(newEndTime: String) { endTime = newEndTime }
    fun updateDate(newDate: String) { date = newDate }
    fun updateIsReminderEnabled(enabled: Boolean) { isReminderEnabled = enabled }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let { task ->
                currentTaskId = task.id
                title = task.title
                description = task.description
                category = task.category
                time = task.time
                location = task.location
                periodicity = task.periodicity
                priority = task.priority
                endTime = task.endTime ?: ""
                date = task.date ?: ""
                isReminderEnabled = true
            }
        }
    }

    fun saveTask(
        context: Context,
        onSuccess: () -> Unit,
        onLocationError: (String) -> Unit = {}
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val task = Task(
                id = currentTaskId ?: 0,
                title = title,
                description = description,
                category = category,
                time = time,
                endTime = endTime.ifBlank { null },
                location = location,
                date = date.ifBlank { null },
                periodicity = periodicity,
                priority = priority
            )

            // Sauvegarde en base
            if (currentTaskId == null) {
                repository.insertTask(task)
                // Récupérer l'ID généré si besoin
                val newId = repository.getTaskById(task.id)?.id ?: task.id
                currentTaskId = newId
            } else {
                repository.updateTask(task)
            }

            // Notification temporelle
            if (isReminderEnabled) {
                val timeInMillis = calculateTimeInMillis(date, time)
                if (timeInMillis != null && timeInMillis > System.currentTimeMillis()) {
                    val notificationId = currentTaskId?.toInt() ?: System.currentTimeMillis().toInt()
                    scheduleNotification(
                        context = context,
                        notificationId = notificationId,
                        timeInMillis = timeInMillis,
                        title = title,
                        message = description.ifBlank { "C'est l'heure de ta routine !" }
                    )
                }
            }

            // Géofence
            if (location.isNotBlank() && isReminderEnabled) {
                // Vérifier les permissions de localisation (à implémenter via un callback)
                if (!hasLocationPermissions(context)) {
                    onLocationError("Permissions de localisation insuffisantes. Active 'Toujours autoriser'.")
                    onSuccess()
                    return@launch
                }

                val coordinates = withContext(Dispatchers.IO) {
                    getCoordinatesFromAddress(context, location)
                }

                if (coordinates != null) {
                    val (lat, lng) = coordinates
                    val taskId = currentTaskId ?: return@launch
                    setupLocationTrigger(context, location, lat, lng, taskId)
                    Toast.makeText(context, "📍 Zone GPS activée pour : $location", Toast.LENGTH_SHORT).show()
                } else {
                    onLocationError("Adresse introuvable. Essaie d'être plus précis.")
                }
            }

            onSuccess()
        }
    }

    private fun calculateTimeInMillis(dateString: String, timeString: String): Long? {
        return try {
            val localDate = LocalDate.parse(dateString)
            val localTime = LocalTime.parse(timeString)
            val localDateTime = LocalDateTime.of(localDate, localTime)
            localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }

    fun resetForm() {
        title = ""
        description = ""
        category = "Cours"
        time = "08:00"
        endTime = ""
        location = ""
        periodicity = "Une fois"
        priority = "Moyenne"
        currentTaskId = null
        date = getTodayISO()
        isReminderEnabled = true
    }

    private suspend fun getCoordinatesFromAddress(context: Context, address: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(address, 1)
                addresses?.firstOrNull()?.let { Pair(it.latitude, it.longitude) }
            } catch (e: Exception) {
                Log.e("STUDIFY_GPS", "Erreur géocodage : ${e.message}")
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationTrigger(
        context: Context,
        locationName: String,
        latitude: Double,
        longitude: Double,
        taskId: Long
    ) {
        try {
            val geofencingClient = LocationServices.getGeofencingClient(context)

            // Utiliser un requestId unique basé sur l'ID de la tâche
            val uniqueRequestId = "task_${taskId}_${System.currentTimeMillis()}"
            lastAddedGeofenceId = uniqueRequestId

            val geofence = Geofence.Builder()
                .setRequestId(uniqueRequestId)
                .setCircularRegion(latitude, longitude, 150f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER) // seulement ENTER
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            val intent = Intent(context, GeofenceReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId.toInt(), // utilise l'ID comme requestCode pour distinguer
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    Log.d("STUDIFY_GPS", " Geofence ajouté : $uniqueRequestId")
                }
                .addOnFailureListener { exception ->
                    Log.e("STUDIFY_GPS", " Échec geofence : ${exception.message}")
                }
        } catch (e: SecurityException) {
            Log.e("STUDIFY_GPS", "Permission manquante pour ajouter le geofence")
        } catch (e: Exception) {
            Log.e("STUDIFY_GPS", "Erreur inattendue : ${e.message}")
        }
    }

    // Fonction utilitaire à appeler depuis l'activité (ou à passer en paramètre)
    private fun hasLocationPermissions(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    // À appeler lors de la suppression d'une tâche pour nettoyer le geofence
    fun removeGeofence(context: Context, taskId: Long) {
        val geofencingClient = LocationServices.getGeofencingClient(context)
        val intent = Intent(context, GeofenceReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.d("STUDIFY_GPS", "Geofence supprimé pour task $taskId")
            }
            .addOnFailureListener {
                Log.e("STUDIFY_GPS", "Échec suppression geofence")
            }
    }
}