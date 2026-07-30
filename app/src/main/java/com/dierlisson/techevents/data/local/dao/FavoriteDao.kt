package com.dierlisson.techevents.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dierlisson.techevents.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT eventId FROM favorites")
    suspend fun getFavoriteEventIds(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE eventId = :eventId LIMIT 1)")
    suspend fun isFavorite(eventId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE eventId = :eventId")
    suspend fun deleteByEventId(eventId: Long)
}
