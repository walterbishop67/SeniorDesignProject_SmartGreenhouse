package com.group22.smartgreenhouse.data.api

import com.group22.smartgreenhouse.data.model.CardStatsResponse
import com.group22.smartgreenhouse.data.model.DeviceListResponse
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.model.UserResponse
import com.group22.smartgreenhouse.data.model.UserStatsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminApi {
    @GET("api/v1/AdminPanel/users/user-stats-count")
    suspend fun getUserStats(
        @Header("Authorization") bearer: String
    ): Response<UserStatsResponse>

    // AdminApi.kt
    @GET("api/v1/AdminPanel/electronic-card/counts")
    suspend fun getElectronicCardStats(
        @Header("Authorization") bearer: String
    ): Response<CardStatsResponse>

    @GET("api/v1/AdminPanel/users/get-all-users")
    suspend fun getAllUsers(
        @Header("Authorization") bearer: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): Response<UserResponse>

    @GET("api/v1/AdminPanel/electronic-card/by-user-id/all")
    suspend fun getUserDevices(
        @Header("Authorization") bearer: String,
        @Query("userId") userId: String
    ): Response<List<ElectronicCard>>

    @POST("api/v1/AdminPanel/electronic-card/add-card-by-user-id")
    suspend fun addDeviceForUser(
        @Header("Authorization") bearer: String,
        @Query("UserId") userId: String
    ): Response<ElectronicCard>

    @GET("api/v1/AdminPanel/electronic-card/unavailable")
    suspend fun getUnavailableDevices(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<DeviceListResponse>

    @GET("api/v1/AdminPanel/electronic-card/available")
    suspend fun getAvailableDevices(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<DeviceListResponse>

    @GET("api/v1/AdminPanel/electronic-card/with-error")
    suspend fun getErrorDevices(
        @Header("Authorization") token: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<DeviceListResponse>
}