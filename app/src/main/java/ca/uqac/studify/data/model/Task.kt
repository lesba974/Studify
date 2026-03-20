package ca.uqac.studify.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String,
    val category: String,
    val time: String,
    val location: String,
    val periodicity: String = "Une fois",
    val priority: String = "Moyenne",

    @Ignore
    val icon: ImageVector = Icons.Default.Bookmark
) {
    constructor(
        id: Long,
        title: String,
        description: String,
        category: String,
        time: String,
        location: String,
        periodicity: String,
        priority: String
    ) : this(id, title, description, category, time, location, periodicity, priority, Icons.Default.Bookmark)
}

fun Task.getIcon(): ImageVector {
    return when {
        title.contains("Cours", ignoreCase = true) -> Icons.Default.Notifications
        title.contains("Révision", ignoreCase = true) -> Icons.Default.Bookmark
        title.contains("Pause", ignoreCase = true) -> Icons.Default.Coffee
        title.contains("Sport", ignoreCase = true) -> Icons.Default.FitnessCenter
        else -> Icons.Default.Task
    }
}