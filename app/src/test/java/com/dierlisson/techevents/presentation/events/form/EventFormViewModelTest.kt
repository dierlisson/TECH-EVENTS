package com.dierlisson.techevents.presentation.events.form

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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EventFormViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: EventsRepository = mock()

    private val validEvent = Event(
        id = 0L,
        title = "Novo Evento Teste",
        description = "Descrição detalhada",
        category = "Android",
        format = "ONLINE",
        date = "2024-05-15",
        startTime = "19:00",
        endTime = "21:00",
        venueName = null,
        address = null,
        city = null,
        state = null,
        organizer = "Comunidade Android",
        imageUrl = null,
        price = 0.0,
        totalSeats = 100,
        registeredParticipants = 10,
        eventUrl = null,
        latitude = null,
        longitude = null,
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
    fun `validateForm should return null for valid event`() {
        val viewModel = EventFormViewModel(repository)

        val error = viewModel.validateForm(validEvent)

        assertNull(error)
    }

    @Test
    fun `validateForm should return error when required title is blank`() {
        val viewModel = EventFormViewModel(repository)
        val invalidEvent = validEvent.copy(title = "")

        val error = viewModel.validateForm(invalidEvent)

        assertNotNull(error)
        assertEquals("O título é obrigatório", error)
    }

    @Test
    fun `validateForm should return error when registeredParticipants exceeds totalSeats`() {
        val viewModel = EventFormViewModel(repository)
        val invalidEvent = validEvent.copy(totalSeats = 50, registeredParticipants = 60)

        val error = viewModel.validateForm(invalidEvent)

        assertNotNull(error)
        assertEquals("O número de inscritos não pode exceder o total de vagas", error)
    }

    @Test
    fun `submitForm when new event should invoke createEvent and update submitState to Success`() = runBlocking {
        whenever(repository.createEvent(any())).thenReturn(NetworkResult.Success(validEvent.copy(id = 99L)))

        val viewModel = EventFormViewModel(repository)
        val validationError = viewModel.submitForm(validEvent)

        assertNull(validationError)
        testDispatcher.scheduler.advanceUntilIdle()

        val submitState = viewModel.submitState.value
        assertTrue(submitState is UiState.Success)
        assertEquals(99L, (submitState as UiState.Success).data.id)
    }

    @Test
    fun `loadEventForEdit should set formState with event data`() = runBlocking {
        whenever(repository.getEventById(10L)).thenReturn(NetworkResult.Success(validEvent.copy(id = 10L)))

        val viewModel = EventFormViewModel(repository)
        viewModel.loadEventForEdit(10L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.formState.value
        assertTrue(state is UiState.Success)
        assertEquals(10L, (state as UiState.Success).data.id)
    }
}
