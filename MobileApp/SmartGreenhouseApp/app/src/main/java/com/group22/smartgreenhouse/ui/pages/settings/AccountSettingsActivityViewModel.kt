package com.group22.smartgreenhouse.ui.pages.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.repository.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.group22.smartgreenhouse.data.model.UserBasicInfo


class AccountSettingsActivityViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val msg: String) : UiState()
        data class Error(val msg: String) : UiState()
    }

    private val _state = mutableStateOf<UiState>(UiState.Idle)
    val state: State<UiState> get() = _state

    fun change(token: String, cur: String, new: String, confirm: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.changePassword(token, cur, new, confirm)
                .fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Unknown error") }
                )
        }
    }

    val userInfo = mutableStateOf<UserBasicInfo?>(null)

    fun loadUserInfo(token: String) {
        viewModelScope.launch {
            val result = repo.getUserBasicInfo(token)
            if (result.isSuccess) {
                userInfo.value = result.getOrNull()
            }
        }
    }
}
