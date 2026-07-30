package com.dierlisson.techevents.presentation.events.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dierlisson.techevents.domain.repository.EventsRepository

class EventFormViewModelFactory(
    private val repository: EventsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventFormViewModel::class.java)) {
            return EventFormViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}
