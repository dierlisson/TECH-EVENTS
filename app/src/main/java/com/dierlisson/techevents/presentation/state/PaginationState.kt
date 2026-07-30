package com.dierlisson.techevents.presentation.state

sealed interface PaginationState {
    data object Idle : PaginationState
    data object LoadingMore : PaginationState
    data class Error(val message: String) : PaginationState
    data object EndOfList : PaginationState
}
