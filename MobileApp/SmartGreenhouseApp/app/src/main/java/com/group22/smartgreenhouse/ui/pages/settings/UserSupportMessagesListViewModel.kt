package com.group22.smartgreenhouse.ui.pages.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.data.repository.SupportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.filter
import kotlin.collections.filterNot
import kotlin.fold

enum class ResponseFilter { ALL, ANSWERED, UNANSWERED }

class UserSupportMessagesListViewModel(
    private val repository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSupportMessagesUiState())
    val uiState: StateFlow<UserSupportMessagesUiState> = _uiState

    private var currentFilter: ResponseFilter = ResponseFilter.ALL

    init {
        loadMessages()
    }

    fun setResponseFilter(filter: ResponseFilter) {
        currentFilter = filter
        _uiState.update { it.copy(messages = emptyList()) }
        loadMessages()
    }

    fun loadMessages() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.getUserSupportMessages().fold(
                onSuccess = { messages ->
                    val filtered = when (currentFilter) {
                        ResponseFilter.ANSWERED -> messages.filter { it.isResponsed }
                        ResponseFilter.UNANSWERED -> messages.filter { !it.isResponsed }
                        else -> messages
                    }
                    _uiState.update {
                        it.copy(
                            messages = filtered,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load messages"
                        )
                    }
                }
            )
        }
    }

    fun refreshMessages() {
        _uiState.update { it.copy(messages = emptyList()) }
        loadMessages()
    }

    fun deleteMessage(messageId: Int) {
        viewModelScope.launch {
            repository.deleteUserSupportMessage(messageId).fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(messages = current.messages.filterNot { it.id == messageId })
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Failed to delete message")
                    }
                }
            )
        }
    }

}

data class UserSupportMessagesUiState(
    val messages: List<SupportMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)