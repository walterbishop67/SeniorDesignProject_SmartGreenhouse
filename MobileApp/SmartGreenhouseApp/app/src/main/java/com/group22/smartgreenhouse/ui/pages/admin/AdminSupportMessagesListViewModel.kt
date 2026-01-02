package com.group22.smartgreenhouse.ui.pages.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.data.repository.SupportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ResponseFilter { ANSWERED, UNANSWERED }

// SupportMessageViewModel.kt
class AdminSupportMessagesListViewModel(
    private val repository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportMessageUiState())
    val uiState: StateFlow<SupportMessageUiState> = _uiState

    // Add these pagination parameters
    private var currentPage = 1
    private var totalPages = 1
    private val pageSize = 10 // Define your page size here
    private var onlyUnopened = false

    init {
        loadMessages()
    }

    private var responseFilter: ResponseFilter? = null

    fun setResponseFilter(filter: ResponseFilter?) {
        responseFilter = filter
        _uiState.update {
            it.copy(messages = emptyList(), currentFilter = filter) // ✅ Update UI state
        }
        loadMessages(1)
    }

    fun loadMessages(page: Int = 1) {
        if (page > totalPages || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.getSupportMessagesWithStatus(onlyUnopened, page, pageSize).fold(
                onSuccess = { messages ->
                    currentPage = page
                    totalPages = (messages.size / pageSize) + 1
                    val filtered = when (responseFilter) {
                        ResponseFilter.ANSWERED -> messages.filter { it.isResponsed }
                        ResponseFilter.UNANSWERED -> messages.filter { !it.isResponsed }
                        else -> messages
                    }
                    _uiState.update {
                        it.copy(
                            messages = if (page == 1) filtered else it.messages + filtered,
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

    fun loadNextPage() {
        if (currentPage < totalPages) {
            loadMessages(currentPage + 1)
        }
    }

    fun toggleUnopenedFilter() {
        onlyUnopened = !onlyUnopened
        _uiState.update { it.copy(messages = emptyList()) }
        loadMessages(1)
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshMessages() {
        _uiState.update { it.copy(messages = emptyList()) }
        loadMessages(1) // Reload first page
    }
}

data class SupportMessageUiState(
    val messages: List<SupportMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentFilter: ResponseFilter? = null //
)
