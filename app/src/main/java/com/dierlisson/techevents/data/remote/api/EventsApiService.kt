package com.dierlisson.techevents.data.remote.api

import com.dierlisson.techevents.data.remote.dto.CreateEventDto
import com.dierlisson.techevents.data.remote.dto.EventDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EventsApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): Response<List<EventDto>>

    @GET("events/{id}")
    suspend fun getEventById(
        @Path("id") id: Long
    ): Response<EventDto>

    @POST("events")
    suspend fun createEvent(
        @Body eventDto: CreateEventDto
    ): Response<EventDto>

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Long,
        @Body eventDto: CreateEventDto
    ): Response<EventDto>

    @DELETE("events/{id}")
    suspend fun deleteEvent(
        @Path("id") id: Long
    ): Response<Unit>
}
