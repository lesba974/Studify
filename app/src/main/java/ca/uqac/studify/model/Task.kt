package ca.uqac.studify.model
import androidx.compose.ui.graphics.vector.ImageVector
data class Task
    (   val title: String,
        val description: String,
        val category: String,
        val time: String,
        val location: String,
        val icon: ImageVector
    )
