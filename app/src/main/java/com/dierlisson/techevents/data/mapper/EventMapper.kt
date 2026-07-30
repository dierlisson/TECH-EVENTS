package com.dierlisson.techevents.data.mapper

import com.dierlisson.techevents.data.local.entity.EventEntity
import com.dierlisson.techevents.data.remote.dto.CreateEventDto
import com.dierlisson.techevents.data.remote.dto.EventDto
import com.dierlisson.techevents.domain.model.Event

object EventMapper {

    fun EventDto.toDomain(isFavorite: Boolean = false): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            category = category,
            format = format,
            date = date,
            startTime = startTime,
            endTime = endTime,
            venueName = venueName,
            address = address,
            city = city,
            state = state,
            organizer = organizer,
            imageUrl = imageUrl,
            price = price,
            totalSeats = totalSeats,
            registeredParticipants = registeredParticipants,
            eventUrl = eventUrl,
            latitude = latitude,
            longitude = longitude,
            isFavorite = isFavorite
        )
    }

    fun EventDto.toEntity(): EventEntity {
        return EventEntity(
            id = id,
            title = title,
            description = description,
            category = category,
            format = format,
            date = date,
            startTime = startTime,
            endTime = endTime,
            venueName = venueName,
            address = address,
            city = city,
            state = state,
            organizer = organizer,
            imageUrl = imageUrl,
            price = price,
            totalSeats = totalSeats,
            registeredParticipants = registeredParticipants,
            eventUrl = eventUrl,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun EventEntity.toDomain(isFavorite: Boolean = false): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            category = category,
            format = format,
            date = date,
            startTime = startTime,
            endTime = endTime,
            venueName = venueName,
            address = address,
            city = city,
            state = state,
            organizer = organizer,
            imageUrl = imageUrl,
            price = price,
            totalSeats = totalSeats,
            registeredParticipants = registeredParticipants,
            eventUrl = eventUrl,
            latitude = latitude,
            longitude = longitude,
            isFavorite = isFavorite
        )
    }

    fun Event.toEntity(): EventEntity {
        return EventEntity(
            id = id,
            title = title,
            description = description,
            category = category,
            format = format,
            date = date,
            startTime = startTime,
            endTime = endTime,
            venueName = venueName,
            address = address,
            city = city,
            state = state,
            organizer = organizer,
            imageUrl = imageUrl,
            price = price,
            totalSeats = totalSeats,
            registeredParticipants = registeredParticipants,
            eventUrl = eventUrl,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun Event.toCreateDto(): CreateEventDto {
        return CreateEventDto(
            title = title,
            description = description,
            category = category,
            format = format,
            date = date,
            startTime = startTime,
            endTime = endTime,
            venueName = venueName,
            address = address,
            city = city,
            state = state,
            organizer = organizer,
            imageUrl = imageUrl,
            price = price,
            totalSeats = totalSeats,
            registeredParticipants = registeredParticipants,
            eventUrl = eventUrl,
            latitude = latitude,
            longitude = longitude
        )
    }
}
