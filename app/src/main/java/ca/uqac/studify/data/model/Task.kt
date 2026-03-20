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
    val endTime: String? = null,

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
        priority: String,
        endTime: String?
    ) : this(id, title, description, category, time, location, periodicity, priority, endTime, Icons.Default.Bookmark)
}

fun Task.getIcon(): ImageVector {
    return when (category) {
        "Cours" -> Icons.Default.School
        "Révision" -> Icons.Default.AutoStories
        "Pause" -> Icons.Default.Coffee
        "Sport" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Task
    }
}