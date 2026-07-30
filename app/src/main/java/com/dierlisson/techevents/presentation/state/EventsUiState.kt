package com.dierlisson.techevents.presentation.state

import com.dierlisson.techevents.domain.model.Event

data class EventsUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val errorMessage: String? = null,
    val isOffline: Boolean = false,
    val paginationState: PaginationState = PaginationState.Idle,
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val selectedFormat: String = "Todos",
    val selectedSort: String = "Data mais próxima",
    val showOnlyFavorites: Boolean = false
)
