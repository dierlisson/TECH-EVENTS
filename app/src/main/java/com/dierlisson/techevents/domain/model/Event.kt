package com.dierlisson.techevents.domain.model

data class Event(
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
    val longitude: Double?,
    val isFavorite: Boolean = false
)
