package com.dierlisson.techevents.data.remote.api

import com.dierlisson.techevents.data.remote.dto.CreateEventDto
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventsApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: EventsApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventsApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getEvents should send offset and limit queries and parse JSON list correctly`() = runBlocking {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "title": "Android Dev Summit",
                    "description": "Evento de Android",
                    "category": "Android",
                    "format": "PRESENCIAL",
                    "date": "2024-03-15",
                    "startTime": "19:00",
                    "endTime": "22:00",
                    "venueName": "Fiesp",
                    "address": "Av. Paulista",
                    "city": "São Paulo",
                    "state": "SP",
                    "organizer": "GDG",
                    "imageUrl": null,
                    "price": 0.0,
                    "totalSeats": 100,
                    "registeredParticipants": 50,
                    "eventUrl": null,
                    "latitude": -23.56,
                    "longitude": -46.65
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = apiService.getEvents(offset = 0, limit = 10)
        val request = mockWebServer.takeRequest()

        assertTrue(response.isSuccessful)
        assertNotNull(response.body())
        assertEquals(1, response.body()?.size)
        assertEquals("Android Dev Summit", response.body()?.get(0)?.title)
        assertEquals("/events?offset=0&limit=10", request.path)
    }

    @Test
    fun `getEventById should return event when HTTP 200`() = runBlocking {
        val jsonResponse = """
            {
                "id": 42,
                "title": "Kotlin Masterclass",
                "description": "Aprenda Kotlin",
                "category": "Kotlin",
                "format": "ONLINE",
                "date": "2024-04-10",
                "startTime": "19:00",
                "endTime": "21:00",
                "venueName": null,
                "address": null,
                "city": null,
                "state": null,
                "organizer": "Kotlin BR",
                "imageUrl": null,
                "price": 0.0,
                "totalSeats": 200,
                "registeredParticipants": 150,
                "eventUrl": null,
                "latitude": null,
                "longitude": null
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = apiService.getEventById(42)
        val request = mockWebServer.takeRequest()

        assertTrue(response.isSuccessful)
        assertEquals(42L, response.body()?.id)
        assertEquals("Kotlin Masterclass", response.body()?.title)
        assertEquals("/events/42", request.path)
    }

    @Test
    fun `getEventById should return 404 error when event not found`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("""{"error":"Not found"}"""))

        val response = apiService.getEventById(999)

        assertEquals(404, response.code())
        assertTrue(!response.isSuccessful)
    }

    @Test
    fun `createEvent should send POST request with correct body`() = runBlocking {
        val jsonResponse = """
            {
                "id": 101,
                "title": "Novo Evento Backend",
                "description": "Descrição",
                "category": "Backend",
                "format": "ONLINE",
                "date": "2024-05-20",
                "startTime": "19:00",
                "endTime": "21:00",
                "venueName": null,
                "address": null,
                "city": null,
                "state": null,
                "organizer": "Backend BR",
                "imageUrl": null,
                "price": 49.9,
                "totalSeats": 100,
                "registeredParticipants": 0,
                "eventUrl": null,
                "latitude": null,
                "longitude": null
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(jsonResponse))

        val createDto = CreateEventDto(
            title = "Novo Evento Backend",
            description = "Descrição",
            category = "Backend",
            format = "ONLINE",
            date = "2024-05-20",
            startTime = "19:00",
            endTime = "21:00",
            venueName = null,
            address = null,
            city = null,
            state = null,
            organizer = "Backend BR",
            imageUrl = null,
            price = 49.9,
            totalSeats = 100,
            registeredParticipants = 0,
            eventUrl = null,
            latitude = null,
            longitude = null
        )

        val response = apiService.createEvent(createDto)
        val request = mockWebServer.takeRequest()

        assertEquals("POST", request.method)
        assertEquals("/events", request.path)
        assertTrue(response.isSuccessful)
        assertEquals(101L, response.body()?.id)
    }

    @Test
    fun `deleteEvent should send DELETE request`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val response = apiService.deleteEvent(42)
        val request = mockWebServer.takeRequest()

        assertEquals("DELETE", request.method)
        assertEquals("/events/42", request.path)
        assertTrue(response.isSuccessful)
    }
}
