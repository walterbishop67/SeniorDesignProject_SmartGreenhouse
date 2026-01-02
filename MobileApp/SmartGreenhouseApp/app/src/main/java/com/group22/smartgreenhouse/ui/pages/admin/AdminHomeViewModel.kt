package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.UserStatsResponse
import com.group22.smartgreenhouse.data.repository.AdminRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.group22.smartgreenhouse.data.model.CardStatsResponse
import kotlinx.coroutines.launch
import kotlin.fold

class AdminHomeViewModel(private val repo: AdminRepository) : ViewModel() {
    // Separate sealed classes for each type of data
    sealed class UserState {
        object Loading : UserState()
        data class Data(val dto: UserStatsResponse) : UserState()
        data class Error(val msg: String) : UserState()
    }

    sealed class CarState {
        object Loading : CarState()
        data class Data(val dto: CardStatsResponse) : CarState()
        data class Error(val msg: String) : CarState()
    }

    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> get() = _userState

    private val _cardState = mutableStateOf<CarState>(CarState.Loading)
    val cardState: State<CarState> get() = _cardState

    fun loadData(token: String) = viewModelScope.launch {
        // Load user stats
        _userState.value = UserState.Loading
        _userState.value = repo.fetchUserStats(token)
            .fold(
                onSuccess = { UserState.Data(it) },
                onFailure = { UserState.Error(it.message ?: "Network error") }
            )

        // Load car stats
        _cardState.value = CarState.Loading
        _cardState.value = repo.fetchElectronicCardStats(token)
            .fold(
                onSuccess = { CarState.Data(it) },
                onFailure = { CarState.Error(it.message ?: "Network error") }
            )
    }
}
