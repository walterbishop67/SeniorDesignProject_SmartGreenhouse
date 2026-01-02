package com.group22.smartgreenhouse.ui.pages.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.api.AdminApi
import com.group22.smartgreenhouse.data.model.ElectronicCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UserDetailViewModel.kt
class UserDetailViewModel(
    private val adminApi: AdminApi
) : ViewModel() {
    private val _devices = MutableStateFlow<List<ElectronicCard>>(emptyList())
    val devices: StateFlow<List<ElectronicCard>> = _devices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog


    fun loadUserDevices(userId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = adminApi.getUserDevices("Bearer $token", userId)
                if (response.isSuccessful) {
                    _devices.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error loading devices: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showAddDeviceDialog() {
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    // Add this function to clear the message
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    // Update your existing functions to use _snackbarMessage
    fun addDeviceForUser(userId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = adminApi.addDeviceForUser("Bearer $token", userId)
                if (response.isSuccessful) {
                    loadUserDevices(userId, token)
                    _snackbarMessage.value = "Device added successfully!"
                } else {
                    _snackbarMessage.value = "Failed to add device"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
                _showDialog.value = false
            }
        }
    }
}


