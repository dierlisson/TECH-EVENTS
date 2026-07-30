package com.dierlisson.techevents.domain.repository

import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event

interface EventsRepository {
    suspend fun getEvents(offset: Int = 0, limit: Int = 10): NetworkResult<List<Event>>
    suspend fun getEventById(id: Long): NetworkResult<Event>
    suspend fun createEvent(event: Event): NetworkResult<Event>
    suspend fun updateEvent(event: Event): NetworkResult<Event>
    suspend fun deleteEvent(id: Long): NetworkResult<Unit>
    suspend fun toggleFavorite(eventId: Long): Boolean
    suspend fun getFavoriteEventIds(): Set<Long>
    fun getCachedEvents(): List<Event>
}
