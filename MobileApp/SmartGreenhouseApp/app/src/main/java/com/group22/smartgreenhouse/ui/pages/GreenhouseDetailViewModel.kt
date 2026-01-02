package com.group22.smartgreenhouse.ui.pages


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group22.smartgreenhouse.data.repository.GreenhouseRepository
import kotlinx.coroutines.launch
import kotlin.collections.find
import kotlin.collections.firstOrNull

data class GreenhouseDetails(
    val id: String,
    val name: String,
    val temperature: String?,
    val humidity: String?
)


class GreenhouseDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GreenhouseRepository(application.applicationContext)

    val greenhouseDetails = mutableStateOf<GreenhouseDetails?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun fetchGreenhouseDetails(token: String, greenhouseId: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                // First get the greenhouse name from the list API
                val greenhousesResult = repository.fetchGreenhouses(token)
                if (greenhousesResult.isSuccess) {
                    val greenhouseName = greenhousesResult.getOrNull()
                        ?.find { it.id.toString() == greenhouseId }
                        ?.productName ?: "Greenhouse $greenhouseId"

                    // Then get the sensor data
                    val detailsResult = repository.getGreenhouseDetails(token, greenhouseId)
                    if (detailsResult.isSuccess) {
                        val data = detailsResult.getOrNull()?.firstOrNull()
                        greenhouseDetails.value = GreenhouseDetails(
                            id = greenhouseId,
                            name = greenhouseName,
                            temperature = data?.temperature,
                            humidity = data?.humidity
                        )
                    } else {
                        errorMessage.value = detailsResult.exceptionOrNull()?.message ?: "Failed to load details"
                    }
                } else {
                    errorMessage.value = greenhousesResult.exceptionOrNull()?.message ?: "Failed to load greenhouse info"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteGreenhouse(token: String, greenhouseId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val result = repository.deleteGreenhouse(token, greenhouseId)
                if (result.isSuccess) {
                    // Handle successful deletion
                    val response = result.getOrNull()
                    // You might want to show the success message to the user
                    errorMessage.value = response?.message ?: "Greenhouse deleted successfully"
                    // Navigate back after successful deletion
                    //navController.popBackStack()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete greenhouse"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "An error occurred"
            } finally {
                isLoading.value = false
            }
        }
    }
}

