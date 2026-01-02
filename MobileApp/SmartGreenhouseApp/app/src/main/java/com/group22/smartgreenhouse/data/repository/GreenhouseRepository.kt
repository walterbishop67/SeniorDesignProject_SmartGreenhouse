package com.group22.smartgreenhouse.data.repository

import android.content.Context
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.model.DeleteResponse
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.model.GreenhouseRequest
import com.group22.smartgreenhouse.data.model.GreenhouseResponse

class GreenhouseRepository(context: Context) {
    private val api = RetrofitClient.getGreenhouseApi(context)

    suspend fun createGreenhouse(token: String, request: GreenhouseRequest): Result<Unit> {
        return try {
            val response = api.createGreenhouse(request, "Bearer $token")
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed: ${response.code()} ${response.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchGreenhouses(token: String): Result<List<GreenhouseResponse>> {
        return try {
            val response = api.getGreenhouses("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableElectronicCards(token: String): Result<List<ElectronicCard>> {
        return try {
            val response = api.getAvailableElectronicCards("Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGreenhouseDetails(token: String, greenhouseId: String): Result<List<ElectronicCard>> {
        return try {
            val response = api.getGreenhouseDetails("Bearer $token", greenhouseId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGreenhouse(token: String, greenhouseId: String): Result<DeleteResponse> {
        return try {
            val response = api.deleteGreenhouse(greenhouseId, "Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}