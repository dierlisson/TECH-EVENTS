package com.dierlisson.techevents.presentation.events.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository
import com.dierlisson.techevents.presentation.state.UiState
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val repository: EventsRepository
) : ViewModel() {

    private val _eventDetailState = MutableLiveData<UiState<Event>>()
    val eventDetailState: LiveData<UiState<Event>> get() = _eventDetailState

    private val _deletionState = MutableLiveData<UiState<Unit>?>()
    val deletionState: LiveData<UiState<Unit>?> get() = _deletionState

    private var currentEventId: Long = -1L

    fun loadEventDetail(eventId: Long) {
        currentEventId = eventId
        _eventDetailState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = repository.getEventById(eventId)) {
                is NetworkResult.Success -> {
                    _eventDetailState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _eventDetailState.value = UiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _eventDetailState.value = UiState.Error(
                        result.throwable.localizedMessage ?: "Erro ao carregar detalhes do evento"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _eventDetailState.value
        if (currentState is UiState.Success) {
            val currentEvent = currentState.data
            viewModelScope.launch {
                val newFavState = repository.toggleFavorite(currentEvent.id)
                _eventDetailState.value = UiState.Success(currentEvent.copy(isFavorite = newFavState))
            }
        }
    }

    fun deleteEvent() {
        if (currentEventId <= 0) return
        _deletionState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = repository.deleteEvent(currentEventId)) {
                is NetworkResult.Success -> {
                    _deletionState.value = UiState.Success(Unit)
                }
                is NetworkResult.Error -> {
                    _deletionState.value = UiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _deletionState.value = UiState.Error(
                        result.throwable.localizedMessage ?: "Erro ao excluir o evento"
                    )
                }
            }
        }
    }

    fun onDeletionStateHandled() {
        _deletionState.value = null
    }
}
