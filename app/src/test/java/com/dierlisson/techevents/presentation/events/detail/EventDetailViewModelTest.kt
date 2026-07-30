package com.dierlisson.techevents.presentation.events.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository
import com.dierlisson.techevents.presentation.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: EventsRepository = mock()

    private val sampleEvent = Event(
        id = 1L,
        title = "Android Dev Summit",
        description = "Descrição detalhada",
        category = "Android",
        format = "PRESENCIAL",
        date = "2024-03-15",
        startTime = "19:00",
        endTime = "22:00",
        venueName = "Fiesp",
        address = "Av. Paulista",
        city = "São Paulo",
        state = "SP",
        organizer = "GDG SP",
        imageUrl = null,
        price = 0.0,
        totalSeats = 150,
        registeredParticipants = 87,
        eventUrl = null,
        latitude = -23.56,
        longitude = -46.65,
        isFavorite = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEventDetail should set UiState Success when repository returns event`() = runBlocking {
        whenever(repository.getEventById(1L)).thenReturn(NetworkResult.Success(sampleEvent))

        val viewModel = EventDetailViewModel(repository)
        viewModel.loadEventDetail(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.eventDetailState.value
        assertTrue(state is UiState.Success)
        assertEquals("Android Dev Summit", (state as UiState.Success).data.title)
    }

    @Test
    fun `toggleFavorite should update favorite status in detail uiState`() = runBlocking {
        whenever(repository.getEventById(1L)).thenReturn(NetworkResult.Success(sampleEvent))
        whenever(repository.toggleFavorite(1L)).thenReturn(true)

        val viewModel = EventDetailViewModel(repository)
        viewModel.loadEventDetail(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.eventDetailState.value
        assertTrue(state is UiState.Success)
        assertTrue((state as UiState.Success).data.isFavorite)
    }

    @Test
    fun `deleteEvent success should set deletionState to Success`() = runBlocking {
        whenever(repository.getEventById(1L)).thenReturn(NetworkResult.Success(sampleEvent))
        whenever(repository.deleteEvent(1L)).thenReturn(NetworkResult.Success(Unit))

        val viewModel = EventDetailViewModel(repository)
        viewModel.loadEventDetail(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteEvent()
        testDispatcher.scheduler.advanceUntilIdle()

        val deletionState = viewModel.deletionState.value
        assertTrue(deletionState is UiState.Success)
    }
}
