package com.dierlisson.techevents.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateEventDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("venueName")
    val venueName: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("organizer")
    val organizer: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("totalSeats")
    val totalSeats: Int,
    @SerializedName("registeredParticipants")
    val registeredParticipants: Int = 0,
    @SerializedName("eventUrl")
    val eventUrl: String?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
)
