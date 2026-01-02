package com.group22.smartgreenhouse.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Passwords do not match")
            return
        }

        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val result = authRepository.register(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    userName = userName,
                    password = password,
                    confirmPassword = confirmPassword
                )

                result.onSuccess { response ->
                    _registerState.value = RegisterState.Success(
                        response.message ?: "Registration successful! Email verification sent."
                    )
                    _navigateToLogin.value = true // Trigger navigation
                }
                result.onFailure { e ->
                    _registerState.value = RegisterState.Error(e.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    fun resetNavigation() {
        _navigateToLogin.value = false
    }
}