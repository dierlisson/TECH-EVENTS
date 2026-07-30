package com.dierlisson.techevents.presentation.events.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dierlisson.techevents.core.network.NetworkResult
import com.dierlisson.techevents.domain.model.Event
import com.dierlisson.techevents.domain.repository.EventsRepository
import com.dierlisson.techevents.presentation.state.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EventFormViewModel(
    private val repository: EventsRepository
) : ViewModel() {

    private val _formState = MutableLiveData<UiState<Event>?>()
    val formState: LiveData<UiState<Event>?> get() = _formState

    private val _submitState = MutableLiveData<UiState<Event>?>()
    val submitState: LiveData<UiState<Event>?> get() = _submitState

    var currentEventId: Long = -1L
        private set

    fun loadEventForEdit(eventId: Long) {
        if (eventId <= 0 || currentEventId == eventId) return
        currentEventId = eventId
        _formState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = repository.getEventById(eventId)) {
                is NetworkResult.Success -> {
                    _formState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _formState.value = UiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _formState.value = UiState.Error(
                        result.throwable.localizedMessage ?: "Erro ao carregar evento para edição"
                    )
                }
            }
        }
    }

    fun submitForm(event: Event): String? {
        val validationError = validateForm(event)
        if (validationError != null) {
            return validationError
        }

        if (_submitState.value is UiState.Loading) return null

        _submitState.value = UiState.Loading

        viewModelScope.launch {
            val result = if (currentEventId > 0) {
                repository.updateEvent(event.copy(id = currentEventId))
            } else {
                repository.createEvent(event)
            }

            when (result) {
                is NetworkResult.Success -> {
                    _submitState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _submitState.value = UiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _submitState.value = UiState.Error(
                        result.throwable.localizedMessage ?: "Erro ao salvar evento"
                    )
                }
            }
        }

        return null
    }

    fun validateForm(event: Event): String? {
        if (event.title.isBlank()) return "O título é obrigatório"
        if (event.description.isBlank()) return "A descrição é obrigatória"
        if (event.category.isBlank()) return "A categoria é obrigatória"
        if (event.format.isBlank()) return "O formato é obrigatório"
        if (event.date.isBlank()) return "A data é obrigatória"
        if (event.startTime.isBlank()) return "O horário inicial é obrigatório"
        if (event.endTime.isBlank()) return "O horário final é obrigatório"
        if (event.organizer.isBlank()) return "O organizador é obrigatório"

        if (event.price < 0.0) return "O preço não pode ser negativo"
        if (event.totalSeats <= 0) return "O total de vagas deve ser maior que zero"
        if (event.registeredParticipants < 0) return "O número de inscritos não pode ser negativo"
        if (event.registeredParticipants > event.totalSeats) {
            return "O número de inscritos não pode exceder o total de vagas"
        }

        // Validate End Time > Start Time if HH:mm
        if (!isEndTimeAfterStartTime(event.startTime, event.endTime)) {
            return "O horário final deve ser posterior ao horário inicial"
        }

        // Presencial location validation
        if (event.format.equals("PRESENCIAL", ignoreCase = true)) {
            if (event.venueName.isNull_or_blank() && event.address.isNull_or_blank() && event.city.isNull_or_blank()) {
                return "Eventos presenciais exigem o preenchimento de local ou endereço"
            }
        }

        if (event.latitude != null && (event.latitude < -90.0 || event.latitude > 90.0)) {
            return "Latitude inválida (deve estar entre -90 e 90)"
        }

        if (event.longitude != null && (event.longitude < -180.0 || event.longitude > 180.0)) {
            return "Longitude inválida (deve estar entre -180 e 180)"
        }

        return null
    }

    private fun isEndTimeAfterStartTime(startTime: String, endTime: String): Boolean {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val start = format.parse(startTime)
            val end = format.parse(endTime)
            if (start != null && end != null) {
                end.after(start)
            } else true
        } catch (e: Exception) {
            true
        }
    }

    fun onSubmitStateHandled() {
        _submitState.value = null
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
}
