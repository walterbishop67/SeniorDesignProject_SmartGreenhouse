package com.group22.smartgreenhouse.ui.pages.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.fold
import kotlin.text.replace

class AdminDevicesListViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState

    private var currentPage = 1
    private var totalPages = 1

    init {
        loadDevices()
    }

    fun loadDevices(page: Int = 1) {
        if (page > totalPages || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = when (_uiState.value.selectedFilter) {
                    "Available" -> repository.getAvailableDevices(page)
                    "Unavailable" -> repository.getUnavailableDevices(page)
                    "Error" -> repository.getErrorDevices(page)
                    else -> Result.failure(Exception("Invalid filter"))
                }

                result.fold(
                    onSuccess = { response ->
                        currentPage = response.currentPage
                        totalPages = response.totalPages
                        _uiState.update {
                            it.copy(
                                devices = if (page == 1) response.cards else it.devices + response.cards,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { error ->
                        // Extract clean message from error
                        val cleanMessage = error.message?.replace(Regex(".*\"message\"\\s*:\\s*\"([^\"]+)\".*"), "$1")
                            ?: "An error occurred"
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = cleanMessage
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                val cleanMessage = e.message?.replace(Regex(".*\"message\"\\s*:\\s*\"([^\"]+)\".*"), "$1")
                    ?: "An unknown error occurred"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = cleanMessage
                    )
                }
            }
        }
    }

    fun refreshDevices() {
        _uiState.update { it.copy(devices = emptyList()) }
        currentPage = 1
        loadDevices(1)
    }

    fun loadNextPage() {
        if (currentPage < totalPages) {
            loadDevices(currentPage + 1)
        }
    }

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter, devices = emptyList()) }
        loadDevices(1)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class DevicesUiState(
    val devices: List<ElectronicCard> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String = "Available",
    val searchQuery: String = ""
)