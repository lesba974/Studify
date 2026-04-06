package ca.uqac.studify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.uqac.studify.data.model.Course
import ca.uqac.studify.data.model.Exam
import ca.uqac.studify.data.model.Task

@Database(
    entities = [Task::class, Course::class, Exam::class],
    version = 4,
    exportSchema = false
)
abstract class StudifyDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun courseDao(): CourseDao
    abstract fun examDao(): ExamDao

    companion object {
        @Volatile
        private var INSTANCE: StudifyDatabase? = null

        fun getDatabase(context: Context): StudifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudifyDatabase::class.java,
                    "studify_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}