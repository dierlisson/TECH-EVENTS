package com.dierlisson.techevents.data.mapper

import com.dierlisson.techevents.data.local.entity.EventEntity
import com.dierlisson.techevents.data.mapper.EventMapper.toCreateDto
import com.dierlisson.techevents.data.mapper.EventMapper.toDomain
import com.dierlisson.techevents.data.mapper.EventMapper.toEntity
import com.dierlisson.techevents.data.remote.dto.EventDto
import com.dierlisson.techevents.domain.model.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EventMapperTest {

    private val sampleDto = EventDto(
        id = 1L,
        title = "Android Summit",
        description = "Descrição",
        category = "Android",
        format = "PRESENCIAL",
        date = "2024-03-15",
        startTime = "19:00",
        endTime = "22:00",
        venueName = "Fiesp",
        address = "Av Paulista",
        city = "São Paulo",
        state = "SP",
        organizer = "GDG",
        imageUrl = "https://example.com/img.jpg",
        price = 0.0,
        totalSeats = 100,
        registeredParticipants = 50,
        eventUrl = "https://example.com",
        latitude = -23.56,
        longitude = -46.65
    )

    @Test
    fun `toDomain should convert EventDto to Event with favorite status`() {
        val domainEvent = sampleDto.toDomain(isFavorite = true)

        assertEquals(sampleDto.id, domainEvent.id)
        assertEquals(sampleDto.title, domainEvent.title)
        assertEquals(sampleDto.category, domainEvent.category)
        assertTrue(domainEvent.isFavorite)
    }

    @Test
    fun `toEntity should convert EventDto to EventEntity correctly`() {
        val entity = sampleDto.toEntity()

        assertEquals(sampleDto.id, entity.id)
        assertEquals(sampleDto.title, entity.title)
        assertEquals(sampleDto.price, entity.price, 0.001)
    }

    @Test
    fun `toDomain should convert EventEntity to Event correctly`() {
        val entity = EventEntity(
            id = 2L,
            title = "Kotlin Event",
            description = "Desc",
            category = "Kotlin",
            format = "ONLINE",
            date = "2024-04-01",
            startTime = "19:00",
            endTime = "20:00",
            venueName = null,
            address = null,
            city = null,
            state = null,
            organizer = "Kotlin BR",
            imageUrl = null,
            price = 50.0,
            totalSeats = 200,
            registeredParticipants = 100,
            eventUrl = null,
            latitude = null,
            longitude = null
        )

        val domain = entity.toDomain(isFavorite = false)

        assertEquals(2L, domain.id)
        assertEquals("Kotlin Event", domain.title)
        assertFalse(domain.isFavorite)
    }

    @Test
    fun `toCreateDto should convert Event to CreateEventDto correctly`() {
        val domain = Event(
            id = 3L,
            title = "Novo Evento",
            description = "Desc",
            category = "Backend",
            format = "ONLINE",
            date = "2024-05-01",
            startTime = "10:00",
            endTime = "12:00",
            venueName = null,
            address = null,
            city = null,
            state = null,
            organizer = "Backend Organizers",
            imageUrl = null,
            price = 0.0,
            totalSeats = 150,
            registeredParticipants = 10,
            eventUrl = null,
            latitude = null,
            longitude = null
        )

        val createDto = domain.toCreateDto()

        assertEquals("Novo Evento", createDto.title)
        assertEquals("Backend", createDto.category)
        assertEquals(150, createDto.totalSeats)
    }
}
