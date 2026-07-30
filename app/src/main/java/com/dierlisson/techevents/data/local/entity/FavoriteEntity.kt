package com.dierlisson.techevents.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val eventId: Long,
    val favoritedAt: Long = System.currentTimeMillis()
)
