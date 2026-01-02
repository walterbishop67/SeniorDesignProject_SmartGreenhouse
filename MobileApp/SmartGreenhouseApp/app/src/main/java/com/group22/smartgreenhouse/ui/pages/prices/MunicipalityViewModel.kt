// AdminMunicipalityListViewModel.kt
package com.group22.smartgreenhouse.ui.pages.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.model.Municipality
import com.group22.smartgreenhouse.data.repository.MunicipalityRepository
import kotlinx.coroutines.launch

class MunicipalityListViewModel(
    private val repository: MunicipalityRepository
) : ViewModel() {
    private val _municipalityList = mutableStateOf<List<Municipality>>(emptyList())
    val municipalityList: List<Municipality> get() = _municipalityList.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    init {
        loadMunicipalities()
    }

    fun loadMunicipalities() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.fetchAllMunicipalities().fold(
                onSuccess = { municipalities ->
                    _municipalityList.value = municipalities
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to load municipalities"
                }
            )
            _isLoading.value = false
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}