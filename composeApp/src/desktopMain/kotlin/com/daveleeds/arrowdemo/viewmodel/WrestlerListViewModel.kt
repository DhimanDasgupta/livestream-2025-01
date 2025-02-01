package com.daveleeds.arrowdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either.Companion.catch
import arrow.fx.coroutines.parMap
import com.daveleeds.arrowdemo.Wrestler
import com.daveleeds.arrowdemo.data.WrestlerRepository
import com.daveleeds.arrowdemo.viewmodel.WrestlerListStatus.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WrestlerListUiState(
    val wrestlers: List<Wrestler> = emptyList(),
    val exception: Throwable? = null,
    val status: WrestlerListStatus = START
)

enum class WrestlerListStatus { START, LOADING, LOADED, ERROR }

class WrestlerListViewModel(
    private val repository: WrestlerRepository = WrestlerRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(WrestlerListUiState())
    val uiState = _uiState
        .onStart { refresh() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _uiState.value
        )
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = LOADING) }

            val result = catch {
                repository
                    .fetchWrestlerIds()
                    .parMap { id -> repository.fetchWrestler(id) }
            }

            _uiState.update { state ->
                result.fold(
                    ifLeft = { state.copy(status = ERROR, exception = it) },
                    ifRight = { state.copy(status = LOADED, wrestlers = it, exception = null) }
                )
            }
        }
    }
}
