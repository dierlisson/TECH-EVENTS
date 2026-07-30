package com.dierlisson.techevents.presentation.events.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository
import com.dierlisson.techevents.presentation.state.PaginationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class EventsListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: EventsRepository = mock()
    private val savedStateHandle = SavedStateHandle()

    private val sampleEvents = listOf(
        Event(
            id = 1L,
            title = "Android Summit",
            description = "Desc",
            category = "Android",
            format = "PRESENCIAL",
            date = "2024-03-15",
            startTime = "19:00",
            endTime = "22:00",
            venueName = null,
            address = null,
            city = "São Paulo",
            state = "SP",
            organizer = "GDG SP",
            imageUrl = null,
            price = 0.0,
            totalSeats = 100,
            registeredParticipants = 50,
            eventUrl = null,
            latitude = null,
            longitude = null,
            isFavorite = false
        ),
        Event(
            id = 2L,
            title = "Kotlin Multiplatform",
            description = "Desc",
            category = "Kotlin",
            format = "ONLINE",
            date = "2024-03-20",
            startTime = "19:30",
            endTime = "21:30",
            venueName = null,
            address = null,
            city = null,
            state = null,
            organizer = "Kotlin BR",
            imageUrl = null,
            price = 0.0,
            totalSeats = 500,
            registeredParticipants = 300,
            eventUrl = null,
            latitude = null,
            longitude = null,
            isFavorite = true
        )
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
    fun `loadInitialEvents should update uiState with events list on success`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state)
        assertFalse(state!!.isLoading)
        assertEquals(2, state.filteredEvents.size)
    }

    @Test
    fun `search query change with 400ms debounce should filter events correctly`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Android")
        testDispatcher.scheduler.advanceTimeBy(399L)
        // Before 400ms, search shouldn't have filtered yet
        assertEquals(2, viewModel.uiState.value?.filteredEvents?.size)

        testDispatcher.scheduler.advanceTimeBy(2L)
        // After 400ms, search is applied
        assertEquals(1, viewModel.uiState.value?.filteredEvents?.size)
        assertEquals("Android Summit", viewModel.uiState.value?.filteredEvents?.get(0)?.title)
    }

    @Test
    fun `category filter should filter list by category`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCategorySelected("Kotlin")

        assertEquals(1, viewModel.uiState.value?.filteredEvents?.size)
        assertEquals("Kotlin Multiplatform", viewModel.uiState.value?.filteredEvents?.get(0)?.title)
    }

    @Test
    fun `format filter should filter list by format`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onFormatSelected("ONLINE")

        assertEquals(1, viewModel.uiState.value?.filteredEvents?.size)
        assertEquals("Kotlin Multiplatform", viewModel.uiState.value?.filteredEvents?.get(0)?.title)
    }

    @Test
    fun `showOnlyFavorites should filter only favorite events`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onFavoritesOnlyToggled(true)

        assertEquals(1, viewModel.uiState.value?.filteredEvents?.size)
        assertTrue(viewModel.uiState.value?.filteredEvents?.get(0)?.isFavorite == true)
    }

    @Test
    fun `loadNextPage when end of list reached should set EndOfList paginationState`() = runBlocking {
        whenever(repository.getEvents(0, 10)).thenReturn(NetworkResult.Success(sampleEvents))
        whenever(repository.getEvents(2, 10)).thenReturn(NetworkResult.Success(emptyList()))

        val viewModel = EventsListViewModel(repository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadNextPage()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(PaginationState.EndOfList, viewModel.uiState.value?.paginationState)
    }

    private fun assertNotNull(actual: Any?) {
        assertTrue(actual != null)
    }
}
