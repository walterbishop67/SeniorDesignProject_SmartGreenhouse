package com.group22.smartgreenhouse.ui.pages.prices


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.AgriProductPriceResponse
import com.group22.smartgreenhouse.data.repository.MunicipalityRepository
import kotlinx.coroutines.launch
import kotlin.fold

class MunicipalityDetailViewModel(
    private val repository: MunicipalityRepository,
    private val municipalityId: Int
) : ViewModel() {
    private val _productPrices = mutableStateOf<AgriProductPriceResponse?>(null)
    val productPrices: AgriProductPriceResponse? get() = _productPrices.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    init {
        loadProductPrices()
    }

    fun loadProductPrices(pageNumber: Int = 1, pageSize: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.fetchAgriProductPrices(municipalityId, pageNumber, pageSize).fold(
                onSuccess = { response ->
                    _productPrices.value = response
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to load product prices"
                }
            )
            _isLoading.value = false
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}