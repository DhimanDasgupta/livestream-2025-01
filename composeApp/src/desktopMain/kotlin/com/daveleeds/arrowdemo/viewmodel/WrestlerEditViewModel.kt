package com.daveleeds.arrowdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either.Companion.catch
import arrow.optics.optics
import arrow.optics.updateCopy
import com.daveleeds.arrowdemo.*
import com.daveleeds.arrowdemo.data.WrestlerRepository
import com.daveleeds.arrowdemo.viewmodel.WrestlerEditStatus.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@optics
data class WrestlerEditUiState(
    val wrestler: Wrestler = Wrestler(-1, "", -1, -1, Hometown("", "")),
    val exception: Throwable? = null,
    val status: WrestlerEditStatus = START
) {
    companion object
}

enum class WrestlerEditStatus { START, LOADING, LOADED, SAVING, SAVED, ERROR }

class WrestlerEditViewModel(
    private val id: Int,
    private val repository: WrestlerRepository = WrestlerRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(WrestlerEditUiState())
    val uiState = _uiState
        .onStart { load(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _uiState.value
        )

    fun save(wrestler: Wrestler) = viewModelScope.launch {
        _uiState.updateCopy {
            WrestlerEditUiState.status set SAVING
        }

        val result = catch { _uiState.value.wrestler.let { repository.saveWrestler(wrestler) } }

        _uiState.update { state ->
            result.fold(
                ifLeft = { state.copy(status = ERROR, exception = it) },
                ifRight = { state.copy(status = SAVED, wrestler = it, exception = null) }
            )
        }
    }

    fun setName(name: String) = _uiState.updateCopy {
        WrestlerEditUiState.wrestler.name set name
    }

    fun setAge(age: Int) = _uiState.updateCopy {
        WrestlerEditUiState.wrestler.age set age
    }

    fun setWeight(weight: Int) = _uiState.updateCopy {
        WrestlerEditUiState.wrestler.weight set weight
    }

    fun setCity(city: String) = _uiState.updateCopy {
        WrestlerEditUiState.wrestler.hometown.city set city
    }

    fun setCountry(country: String) = _uiState.updateCopy {
        WrestlerEditUiState.wrestler.hometown.country set country
    }

    private fun load(id: Int) = viewModelScope.launch {
        _uiState.updateCopy {
            WrestlerEditUiState.status set LOADING
        }

        val wrestler = catch { repository.fetchWrestler(id) }

        _uiState.updateCopy {
            wrestler.fold(
                ifLeft = { e ->
                    WrestlerEditUiState.status set ERROR
                    WrestlerEditUiState.exception set e
                },
                ifRight = { wrestler ->
                    WrestlerEditUiState.status set LOADED
                    WrestlerEditUiState.wrestler set wrestler
                    WrestlerEditUiState.exception set null
                }
            )
        }
    }
}
