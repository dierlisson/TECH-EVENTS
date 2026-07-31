package com.dierlisson.techevents.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigateToEventsList = MutableLiveData<Boolean>()
    val navigateToEventsList: LiveData<Boolean> get() = _navigateToEventsList

    init {
        startSplashTimer()
    }

    
    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(1200L) // 1.2s preparation delay
            _navigateToEventsList.value = true
        }
    }

    fun onNavigationHandled() {
        _navigateToEventsList.value = false
    }
}
