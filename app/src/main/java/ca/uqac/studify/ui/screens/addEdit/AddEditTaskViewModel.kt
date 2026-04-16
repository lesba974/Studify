package ca.uqac.studify.ui.screens.addEdit
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uqac.studify.data.model.Task
import ca.uqac.studify.data.repository.TaskRepository
import ca.uqac.studify.ui.screens.detail.getTodayISO
import ca.uqac.studify.utils.scheduleNotification
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import android.location.Geocoder
import android.content.Intent
import android.app.PendingIntent
import android.util.Log
import java.util.Locale
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.Geofence
import android.widget.Toast


class AddEditTaskViewModel : ViewModel() {

    private lateinit var repository: TaskRepository

    //VARIABLES D'ÉTAT
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

    fun saveTask(context: Context, onSuccess: () -> Unit) {
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

            if (currentTaskId == null) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }

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
            if (location.isNotBlank() && isReminderEnabled) {
                val coordinates = getCoordinatesFromAddress(context, location)
                if (coordinates != null) {
                    val (lat, lng) = coordinates
                    setupLocationTrigger(context, location, lat, lng)
                    Toast.makeText(context, "📍 Zone GPS activée pour : $location", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, " Adresse GPS introuvable. Essaie d'être plus précis.", Toast.LENGTH_LONG).show()
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
            e.printStackTrace()
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
    private fun getCoordinatesFromAddress(context: Context, address: String): Pair<Double, Double>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationTrigger(context: Context, locationName: String, latitude: Double, longitude: Double) {
        try {
            val geofencingClient = LocationServices.getGeofencingClient(context)

            val geofence = Geofence.Builder()
                .setRequestId(locationName)
                .setCircularRegion(latitude, longitude, 150f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            val intent = Intent(context, ca.uqac.studify.receiver.GeofenceReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    android.util.Log.d("STUDIFY_GPS", " Geofence ajouté avec succès !")
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("STUDIFY_GPS", " Erreur Geofence : ${exception.message}")
                }

        } catch (e: SecurityException) {
            android.util.Log.e("STUDIFY_GPS", "🛡 CRASH ÉVITÉ : Il manque la permission 'Toujours autoriser' !")
        } catch (e: Exception) {
            android.util.Log.e("STUDIFY_GPS", "🛡 CRASH ÉVITÉ : Erreur inconnue : ${e.message}")
        }
    }
}