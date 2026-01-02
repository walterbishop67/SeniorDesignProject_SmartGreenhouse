package com.group22.smartgreenhouse.data.repository

import com.group22.smartgreenhouse.data.api.AdminApi
import com.group22.smartgreenhouse.data.model.CardStatsResponse
import com.group22.smartgreenhouse.data.model.DeviceListResponse
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.model.UserResponse
import com.group22.smartgreenhouse.data.model.UserStatsResponse
import com.group22.smartgreenhouse.util.SessionManager
import retrofit2.Response

class AdminRepository(private val api: AdminApi) {

    suspend fun fetchUserStats(token: String): Result<UserStatsResponse> =
        try {
            val r = api.getUserStats("Bearer $token")
            if (r.isSuccessful && r.body()!=null) Result.success(r.body()!!)
            else Result.failure(Exception(r.errorBody()?.string() ?: "Error"))
        } catch (e: Exception) { Result.failure(e) }


    suspend fun fetchElectronicCardStats(token: String): Result<CardStatsResponse> =
        try {
            val r = api.getElectronicCardStats("Bearer $token")
            if (r.isSuccessful && r.body()!=null) Result.success(r.body()!!)
            else Result.failure(Exception(r.errorBody()?.string() ?: "Error"))
        } catch (e: Exception) { Result.failure(e) }

    suspend fun fetchAllUsers(page: Int): Result<UserResponse> = try {
        val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
        val response = api.getAllUsers("Bearer $token", page, 10)

        if (response.isSuccessful) {
            response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
        } else {
            when (response.code()) {
                401 -> {
                    SessionManager.clearSession()
                    Result.failure(Exception("Session expired"))
                }
                else -> Result.failure(
                    Exception(response.errorBody()?.string() ?: "Unknown error")
                )
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }


    suspend fun getUnavailableDevices(pageNumber: Int = 1, pageSize: Int = 10): Result<DeviceListResponse> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getUnavailableDevices("Bearer $token", pageNumber, pageSize)

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                when (response.code()) {
                    401 -> {
                        SessionManager.clearSession()
                        Result.failure(Exception("Session expired"))
                    }
                    else -> Result.failure(
                        Exception(response.errorBody()?.string() ?: "Unknown error")
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableDevices(
        pageNumber: Int = 1,
        pageSize: Int = 10
    ): Result<DeviceListResponse> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getAvailableDevices("Bearer $token", pageNumber, pageSize)
            handleDeviceResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getErrorDevices(
        pageNumber: Int = 1,
        pageSize: Int = 10
    ): Result<DeviceListResponse> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getErrorDevices("Bearer $token", pageNumber, pageSize)
            handleDeviceResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleDeviceResponse(response: Response<DeviceListResponse>): Result<DeviceListResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
        } else {
            when (response.code()) {
                401 -> {
                    SessionManager.clearSession()
                    Result.failure(Exception("Session expired"))
                }
                else -> Result.failure(
                    Exception(response.errorBody()?.string() ?: "Unknown error")
                )
            }
        }
    }


}
