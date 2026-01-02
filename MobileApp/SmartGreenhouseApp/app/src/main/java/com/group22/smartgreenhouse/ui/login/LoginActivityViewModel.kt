package com.group22.smartgreenhouse.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.AuthResponse
import com.group22.smartgreenhouse.data.repository.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.group22.smartgreenhouse.data.model.ResetPasswordRequest
import com.group22.smartgreenhouse.util.SessionManager


class LoginActivityViewModel(private val repository: AuthRepository) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = repository.login(email, password)
            uiState = if (result.isSuccess) {
                val authResponse = result.getOrNull()
                SessionManager.jwtToken = authResponse?.jwToken
                SessionManager.userId = authResponse?.id
                SessionManager.userEmail = authResponse?.email
                SessionManager.userName = authResponse?.userName
                UiState.Success(authResponse)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val response: AuthResponse?) : UiState()
        data class ForgotPasswordSuccess(val message: String) : UiState()
        data class ResetPasswordSuccess(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = repository.forgotPassword(email)
            uiState = if (result.isSuccess) {
                val response = result.getOrNull()
                if (response?.emailSent == true) {
                    UiState.ForgotPasswordSuccess(response.message)
                } else {
                    UiState.Error(response?.message ?: "Failed to send reset email")
                }
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetPassword(email: String, token: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            val request = ResetPasswordRequest(
                email = email,
                token = token,
                password = newPassword,
                confirmPassword = confirmPassword
            )

            val authToken = SessionManager.jwtToken ?: ""
            val result = repository.resetPassword("Bearer $authToken", request)

            uiState = if (result.isSuccess) {
                val response = result.getOrNull()
                if (response?.success == true) {
                    UiState.ResetPasswordSuccess(response.message)
                } else {
                    UiState.Error(response?.message ?: "Password reset failed")
                }
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error occurred")
            }
        }
    }
}
