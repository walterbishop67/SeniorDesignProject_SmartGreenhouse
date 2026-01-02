package com.group22.smartgreenhouse.ui.pages

import Greenhouse
import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.GreenhouseRequest
import com.group22.smartgreenhouse.data.repository.GreenhouseRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.group22.smartgreenhouse.R
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.util.SessionManager

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GreenhouseRepository(application.applicationContext)
    private val _greenhouses = mutableStateListOf<Greenhouse>()
    val greenhouses: List<Greenhouse> = _greenhouses


    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    suspend fun addGreenhouse(token: String, name: String, type: String, area: String, code: String): Boolean {
        val token = SessionManager.jwtToken ?: return false
        val request = GreenhouseRequest(name, type, area, code)
        val result = repository.createGreenhouse(token, request)
        return if (result.isSuccess) {
            _greenhouses += Greenhouse(id = "", name = name, imageRes = R.drawable.logo)
            _message.value = "Greenhouse added successfully"
            true
        } else {
            _message.value = result.exceptionOrNull()?.message ?: "Something went wrong"
            false
        }
    }

    fun loadGreenhouses(token: String) {
        val token = SessionManager.jwtToken ?: return
        viewModelScope.launch {
            val result = repository.fetchGreenhouses(token)
            if (result.isSuccess) {
                val response = result.getOrNull() ?: emptyList()
                _greenhouses.clear()
                _greenhouses.addAll(
                    response.map {
                        Greenhouse(
                            id = it.id.toString(),
                            name = it.productName,
                            imageRes = R.drawable.logo
                        )
                    }
                )
            } else {
                _message.value = result.exceptionOrNull()?.message
            }
        }
    }

    private val _availableDevices = mutableStateListOf<ElectronicCard>()
    val availableDevices: List<ElectronicCard> = _availableDevices

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadAvailableDevices(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getAvailableElectronicCards(token)
            _isLoading.value = false
            if (result.isSuccess) {
                _availableDevices.clear()
                _availableDevices.addAll(result.getOrDefault(emptyList()))
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Failed to load devices"
            }
        }
    }
}
