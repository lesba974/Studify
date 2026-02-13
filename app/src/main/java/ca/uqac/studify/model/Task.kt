package ca.uqac.studify.model
import androidx.compose.ui.graphics.vector.ImageVector
data class Task
    (   val title: String,
        val description: String,
        val category: String, // "Travail" ou "Santé"
        val time: String,
        val location: String,
        val icon: ImageVector // Pour l'icône à gauche (cloche, signet, etc.)
    )
