package ca.uqac.studify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.uqac.studify.data.model.Task

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
abstract class StudifyDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

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