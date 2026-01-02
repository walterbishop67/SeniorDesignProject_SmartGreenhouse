package com.group22.smartgreenhouse.ui.pages.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.data.repository.SupportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.fold

class AdminSupportMessageDetailViewModel(
    private val repository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportMessageDetailUiState())
    val uiState: StateFlow<SupportMessageDetailUiState> = _uiState

    fun loadMessage(messageId: Int) {
        if (messageId <= 0) {
            _uiState.update { it.copy(error = "Invalid message ID") }
            return
        }

        viewModelScope.launch {
            repository.getSupportMessage(messageId).fold(
                onSuccess = { message ->
                    _uiState.update {
                        it.copy(
                            message = message,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load message"
                        )
                    }
                }
            )
        }
    }

    fun submitResponse(messageId: Int, response: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.submitResponse(messageId, response).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = it.message?.copy(
                                isResponsed = true,
                                messageResponse = response
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to submit response"
                        )
                    }
                }
            )
        }
    }
}

data class SupportMessageDetailUiState(
    val message: SupportMessage? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)