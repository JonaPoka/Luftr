package com.jonapoka.luftr.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jonapoka.luftr.data.dao.ExerciseDao
import com.jonapoka.luftr.data.dao.ExerciseSetDao
import com.jonapoka.luftr.data.dao.WorkoutDao
import com.jonapoka.luftr.data.entities.Exercise
import com.jonapoka.luftr.data.entities.ExerciseSet
import com.jonapoka.luftr.data.entities.Workout

@Database(
    entities = [
        Workout::class,
        Exercise::class,
        ExerciseSet::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LuftrDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao

    companion object {
        @Volatile
        private var INSTANCE: LuftrDatabase? = null

        fun getDatabase(context: Context): LuftrDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuftrDatabase::class.java,
                    "luftr_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
