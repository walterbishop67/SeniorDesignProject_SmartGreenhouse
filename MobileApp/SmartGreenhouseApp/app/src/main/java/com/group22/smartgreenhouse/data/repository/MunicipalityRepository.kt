package com.group22.smartgreenhouse.data.repository

import com.group22.smartgreenhouse.data.api.MunicipalityApi
import com.group22.smartgreenhouse.data.model.AgriProductPriceResponse
import com.group22.smartgreenhouse.data.model.Municipality
import com.group22.smartgreenhouse.util.SessionManager

// MunicipalityRepository.kt
class MunicipalityRepository(
    private val api: MunicipalityApi,
    private val sessionManager: SessionManager
) {
    suspend fun fetchAllMunicipalities(): Result<List<Municipality>> {
        return try {
            val token = "Bearer ${sessionManager.jwtToken}"
            val response = api.getMunicipalities(token)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAgriProductPrices(municipalityId: Int, pageNumber: Int = 1, pageSize: Int = 10): Result<AgriProductPriceResponse> {
        return try {
            val token = "Bearer ${sessionManager.jwtToken}"
            val response = api.getAgriProductPrices(municipalityId, pageNumber, pageSize, token)
            if (response.isSuccessful) {
                Result.success(response.body() ?: AgriProductPriceResponse(pageNumber, pageSize,
                    emptyList()
                ))
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}