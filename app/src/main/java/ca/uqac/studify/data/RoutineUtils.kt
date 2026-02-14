package ca.uqac.studify.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import ca.uqac.studify.model.Task

object RoutineUtils {
    //  retourne la liste des routines
    fun getMockRoutines(): List<Task> {
        return listOf(
            Task("Cours", "Cours de mobile - notification 10 avant", "Travail", "Lundi 8:00", "UQAC", Icons.Default.Notifications),
            Task("Révisions", "Session de révision - déclenché si à la maison", "Travail", "Quotidien 18:00", "À la maison", Icons.Default.Bookmark),
            Task("Pause", "Pause, bouge un peu - alerte automatique", "Santé", "Si imobile 2h", "N'importe où", Icons.Default.Coffee),
            Task("Sport", "Jogging - Suggéré selon la météo et l'heure", "Santé", "Vers 19:00", "Au parc", Icons.Default.DirectionsRun)
        )
    }
}