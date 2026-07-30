package com.dierlisson.techevents.data.repository

import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.data.cache.MemoryCache
import com.dierlisson.techevents.data.local.dao.EventDao
import com.dierlisson.techevents.data.local.dao.FavoriteDao
import com.dierlisson.techevents.data.local.entity.EventEntity
import com.dierlisson.techevents.data.local.entity.FavoriteEntity
import com.dierlisson.techevents.data.remote.api.EventsApiService
import com.dierlisson.techevents.data.remote.dto.EventDto
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class EventsRepositoryImplTest {

    private val apiService: EventsApiService = mock()
    private val eventDao: EventDao = mock()
    private val favoriteDao: FavoriteDao = mock()
    private val memoryCache: MemoryCache = MemoryCache()

    private lateinit var repository: EventsRepositoryImpl

    @Before
    fun setUp() {
        memoryCache.clear()
        repository = EventsRepositoryImpl(apiService, eventDao, favoriteDao, memoryCache)
    }

    @Test
    fun `getEvents success should persist in Room and update MemoryCache`() = runBlocking {
        val dto = EventDto(
            id = 10L,
            title = "Test Event",
            description = "Desc",
            category = "Android",
            format = "ONLINE",
            date = "2024-03-15",
            startTime = "19:00",
            endTime = "21:00",
            venueName = null,
            address = null,
            city = null,
            state = null,
            organizer = "Org",
            imageUrl = null,
            price = 0.0,
            totalSeats = 100,
            registeredParticipants = 10,
            eventUrl = null,
            latitude = null,
            longitude = null
        )

        whenever(favoriteDao.getFavoriteEventIds()).thenReturn(listOf(10L))
        whenever(apiService.getEvents(0, 10)).thenReturn(Response.success(listOf(dto)))

        val result = repository.getEvents(0, 10)

        assertTrue(result is NetworkResult.Success)
        val events = (result as NetworkResult.Success).data
        assertEquals(1, events.size)
        assertTrue(events[0].isFavorite)
        verify(eventDao).insertAll(any())
        assertEquals(1, memoryCache.getAll().size)
    }

    @Test
    fun `getEvents network error should fallback to local Room DB`() = runBlocking {
        val localEntity = EventEntity(
            id = 5L,
            title = "Offline Event",
            description = "Desc",
            category = "Kotlin",
            format = "PRESENCIAL",
            date = "2024-04-01",
            startTime = "10:00",
            endTime = "12:00",
            venueName = "Local",
            address = "Rua X",
            city = "SP",
            state = "SP",
            organizer = "Org",
            imageUrl = null,
            price = 0.0,
            totalSeats = 50,
            registeredParticipants = 5,
            eventUrl = null,
            latitude = null,
            longitude = null
        )

        whenever(favoriteDao.getFavoriteEventIds()).thenReturn(emptyList())
        whenever(apiService.getEvents(0, 10)).thenReturn(Response.error(500, "Server Error".toResponseBody()))
        whenever(eventDao.getAllEvents()).thenReturn(listOf(localEntity))

        val result = repository.getEvents(0, 10)

        assertTrue(result is NetworkResult.Success)
        val events = (result as NetworkResult.Success).data
        assertEquals(1, events.size)
        assertEquals("Offline Event", events[0].title)
        assertFalse(events[0].isFavorite)
    }

    @Test
    fun `toggleFavorite should insert favorite when not favorited and update MemoryCache`() = runBlocking {
        whenever(favoriteDao.isFavorite(100L)).thenReturn(false)

        val newFavState = repository.toggleFavorite(100L)

        assertTrue(newFavState)
        verify(favoriteDao).insert(any())
        verify(favoriteDao, never()).deleteByEventId(100L)
    }

    @Test
    fun `toggleFavorite should delete favorite when already favorited`() = runBlocking {
        whenever(favoriteDao.isFavorite(100L)).thenReturn(true)

        val newFavState = repository.toggleFavorite(100L)

        assertFalse(newFavState)
        verify(favoriteDao).deleteByEventId(100L)
        verify(favoriteDao, never()).insert(any())
    }
}
