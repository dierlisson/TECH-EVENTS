package com.dierlisson.techevents.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val format: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val venueName: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val organizer: String,
    val imageUrl: String?,
    val price: Double,
    val totalSeats: Int,
    val registeredParticipants: Int,
    val eventUrl: String?,
    val latitude: Double?,
    val longitude: Double?
)
