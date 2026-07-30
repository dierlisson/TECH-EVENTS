package com.dierlisson.techevents.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dierlisson.techevents.data.local.dao.EventDao
import com.dierlisson.techevents.data.local.dao.FavoriteDao
import com.dierlisson.techevents.data.local.entity.EventEntity
import com.dierlisson.techevents.data.local.entity.FavoriteEntity

@Database(
    entities = [EventEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TechEventsDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: TechEventsDatabase? = null

        fun getInstance(context: Context): TechEventsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TechEventsDatabase::class.java,
                    "tech_events.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
