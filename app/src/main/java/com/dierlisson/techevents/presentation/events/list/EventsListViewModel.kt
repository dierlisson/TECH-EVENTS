package com.dierlisson.techevents.presentation.events.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository
import com.dierlisson.techevents.presentation.state.EventsUiState
import com.dierlisson.techevents.presentation.state.PaginationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EventsListViewModel(
    private val repository: EventsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SEARCH_QUERY = "key_search_query"
        private const val KEY_CATEGORY = "key_category"
        private const val KEY_FORMAT = "key_format"
        private const val KEY_SORT = "key_sort"
        private const val KEY_FAVORITES_ONLY = "key_favorites_only"
        const val PAGE_SIZE = 10
    }

    private val _uiState = MutableLiveData<EventsUiState>()
    val uiState: LiveData<EventsUiState> get() = _uiState

    private var currentOffset = 0
    private var isFetchingNextPage = false
    private var searchDebounceJob: Job? = null

    init {
        val initialQuery = savedStateHandle.get<String>(KEY_SEARCH_QUERY) ?: ""
        val initialCategory = savedStateHandle.get<String>(KEY_CATEGORY) ?: "Todos"
        val initialFormat = savedStateHandle.get<String>(KEY_FORMAT) ?: "Todos"
        val initialSort = savedStateHandle.get<String>(KEY_SORT) ?: "Data mais próxima"
        val initialFavoritesOnly = savedStateHandle.get<Boolean>(KEY_FAVORITES_ONLY) ?: false

        _uiState.value = EventsUiState(
            searchQuery = initialQuery,
            selectedCategory = initialCategory,
            selectedFormat = initialFormat,
            selectedSort = initialSort,
            showOnlyFavorites = initialFavoritesOnly
        )

        loadInitialEvents()
    }

    fun loadInitialEvents() {
        if (_uiState.value?.isLoading == true) return

        currentOffset = 0
        isFetchingNextPage = false

        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            errorMessage = null,
            paginationState = PaginationState.Idle
        )

        viewModelScope.launch {
            when (val result = repository.getEvents(offset = 0, limit = PAGE_SIZE)) {
                is NetworkResult.Success -> {
                    val loadedEvents = result.data
                    val isEnd = loadedEvents.size < PAGE_SIZE

                    val currentState = _uiState.value ?: EventsUiState()
                    val updatedState = currentState.copy(
                        isLoading = false,
                        events = loadedEvents,
                        errorMessage = null,
                        paginationState = if (isEnd) PaginationState.EndOfList else PaginationState.Idle
                    )
                    _uiState.value = applyFiltersAndSorting(updatedState)
                    currentOffset = loadedEvents.size
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Exception -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = result.throwable.localizedMessage ?: "Erro ao se comunicar com a API"
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value ?: return
        if (isFetchingNextPage || currentState.isLoading || currentState.paginationState is PaginationState.EndOfList) {
            return
        }

        isFetchingNextPage = true
        _uiState.value = currentState.copy(paginationState = PaginationState.LoadingMore)

        viewModelScope.launch {
            when (val result = repository.getEvents(offset = currentOffset, limit = PAGE_SIZE)) {
                is NetworkResult.Success -> {
                    isFetchingNextPage = false
                    val newEvents = result.data

                    // Filter out duplicates
                    val existingIds = currentState.events.map { it.id }.toSet()
                    val distinctNewEvents = newEvents.filter { !existingIds.contains(it.id) }

                    val allEvents = currentState.events + distinctNewEvents
                    val isEnd = distinctNewEvents.isEmpty() || newEvents.size < PAGE_SIZE

                    currentOffset += distinctNewEvents.size

                    val updatedState = currentState.copy(
                        events = allEvents,
                        paginationState = if (isEnd) PaginationState.EndOfList else PaginationState.Idle
                    )
                    _uiState.value = applyFiltersAndSorting(updatedState)
                }
                is NetworkResult.Error -> {
                    isFetchingNextPage = false
                    _uiState.value = currentState.copy(
                        paginationState = PaginationState.Error(result.message)
                    )
                }
                is NetworkResult.Exception -> {
                    isFetchingNextPage = false
                    _uiState.value = currentState.copy(
                        paginationState = PaginationState.Error(result.throwable.localizedMessage ?: "Erro de conexão ao paginar")
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
        val currentState = _uiState.value?.copy(searchQuery = query) ?: EventsUiState(searchQuery = query)
        _uiState.value = currentState

        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(400L) // 400ms debounce
            val latestState = _uiState.value ?: return@launch
            _uiState.value = applyFiltersAndSorting(latestState)
        }
    }

    fun onCategorySelected(category: String) {
        savedStateHandle[KEY_CATEGORY] = category
        val currentState = _uiState.value?.copy(selectedCategory = category) ?: return
        _uiState.value = applyFiltersAndSorting(currentState)
    }

    fun onFormatSelected(format: String) {
        savedStateHandle[KEY_FORMAT] = format
        val currentState = _uiState.value?.copy(selectedFormat = format) ?: return
        _uiState.value = applyFiltersAndSorting(currentState)
    }

    fun onSortSelected(sortOption: String) {
        savedStateHandle[KEY_SORT] = sortOption
        val currentState = _uiState.value?.copy(selectedSort = sortOption) ?: return
        _uiState.value = applyFiltersAndSorting(currentState)
    }

    fun onFavoritesOnlyToggled(showFavoritesOnly: Boolean) {
        savedStateHandle[KEY_FAVORITES_ONLY] = showFavoritesOnly
        val currentState = _uiState.value?.copy(showOnlyFavorites = showFavoritesOnly) ?: return
        _uiState.value = applyFiltersAndSorting(currentState)
    }

    fun clearFilters() {
        savedStateHandle[KEY_SEARCH_QUERY] = ""
        savedStateHandle[KEY_CATEGORY] = "Todos"
        savedStateHandle[KEY_FORMAT] = "Todos"
        savedStateHandle[KEY_SORT] = "Data mais próxima"
        savedStateHandle[KEY_FAVORITES_ONLY] = false

        val clearedState = (_uiState.value ?: EventsUiState()).copy(
            searchQuery = "",
            selectedCategory = "Todos",
            selectedFormat = "Todos",
            selectedSort = "Data mais próxima",
            showOnlyFavorites = false
        )
        _uiState.value = applyFiltersAndSorting(clearedState)
    }

    fun toggleFavorite(event: Event) {
        viewModelScope.launch {
            val newFavStatus = repository.toggleFavorite(event.id)

            val currentState = _uiState.value ?: return@launch
            val updatedEvents = currentState.events.map {
                if (it.id == event.id) it.copy(isFavorite = newFavStatus) else it
            }
            val updatedState = currentState.copy(events = updatedEvents)
            _uiState.value = applyFiltersAndSorting(updatedState)
        }
    }

    private fun applyFiltersAndSorting(state: EventsUiState): EventsUiState {
        var filtered = state.events.toList()

        // 1. Search Filter (Case insensitive + unaccented)
        if (state.searchQuery.isNotBlank()) {
            val queryNormalized = state.searchQuery.trim().lowercase()
            filtered = filtered.filter { event ->
                event.title.lowercase().contains(queryNormalized) ||
                        event.description.lowercase().contains(queryNormalized) ||
                        event.category.lowercase().contains(queryNormalized) ||
                        event.organizer.lowercase().contains(queryNormalized) ||
                        (event.city?.lowercase()?.contains(queryNormalized) == true) ||
                        (event.state?.lowercase()?.contains(queryNormalized) == true)
            }
        }

        // 2. Category Filter
        if (state.selectedCategory != "Todos") {
            filtered = filtered.filter {
                it.category.equals(state.selectedCategory, ignoreCase = true)
            }
        }

        // 3. Format & Status Filter
        when (state.selectedFormat.uppercase()) {
            "PRESENCIAL" -> {
                filtered = filtered.filter {
                    it.format.equals("PRESENCIAL", ignoreCase = true)
                }
            }
            "ONLINE" -> {
                filtered = filtered.filter {
                    it.format.equals("ONLINE", ignoreCase = true)
                }
            }
            "FINALIZADOS" -> {
                filtered = filtered.filter {
                    it.title.contains("[ENCERRADO]", ignoreCase = true) || it.date < "2026-07-30"
                }
            }
            else -> { // "TODOS"
                // No format filter
            }
        }

        // 4. Favorites Only Filter
        if (state.showOnlyFavorites) {
            filtered = filtered.filter { it.isFavorite }
        }

        // 5. Sorting
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        filtered = when (state.selectedSort) {
            "Nome" -> filtered.sortedBy { it.title }
            "Mais inscritos" -> filtered.sortedByDescending { it.registeredParticipants }
            else -> filtered.sortedBy { event ->
                try {
                    dateFormat.parse(event.date)?.time ?: Long.MAX_VALUE
                } catch (e: Exception) {
                    Long.MAX_VALUE
                }
            }
        }

        return state.copy(filteredEvents = filtered)
    }
}
