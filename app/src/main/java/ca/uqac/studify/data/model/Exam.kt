package ca.uqac.studify.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val courseId: Long,
    val courseName: String,
    val examDate: String,
    val examTime: String,
    val location: String,
    val revisionWeeks: Int
)