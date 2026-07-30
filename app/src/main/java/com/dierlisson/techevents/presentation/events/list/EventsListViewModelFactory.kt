package com.dierlisson.techevents.presentation.events.list

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.dierlisson.techevents.domain.repository.EventsRepository

class EventsListViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: EventsRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(EventsListViewModel::class.java)) {
            return EventsListViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
