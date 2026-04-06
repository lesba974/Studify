package ca.uqac.studify.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val location: String,
    val startDate: String,
    val endDate: String? = null
)