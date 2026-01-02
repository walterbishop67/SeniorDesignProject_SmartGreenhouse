package com.group22.smartgreenhouse.data.api

import com.group22.smartgreenhouse.data.model.AuthRequest
import com.group22.smartgreenhouse.data.model.AuthResponse
import com.group22.smartgreenhouse.data.model.ChangePasswordRequest
import com.group22.smartgreenhouse.data.model.ChangePasswordResponse
import com.group22.smartgreenhouse.data.model.ForgotPasswordRequest
import com.group22.smartgreenhouse.data.model.ForgotPasswordResponse
import com.group22.smartgreenhouse.data.model.RegisterRequest
import com.group22.smartgreenhouse.data.model.RegisterResponse
import com.group22.smartgreenhouse.data.model.ResetPasswordRequest
import com.group22.smartgreenhouse.data.model.ResetPasswordResponse
import com.group22.smartgreenhouse.data.model.UserBasicInfo
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/Account/authenticate")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/Account/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>


    @POST("api/Account/change-password")
    suspend fun changePassword(
        @Header("Authorization") bearer: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @GET("/api/Account/get-user-basic-info")
    suspend fun getUserBasicInfo(
        @Header("Authorization") auth: String
    ): UserBasicInfo

    @POST("api/Account/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/Account/reset-password")
    suspend fun resetPassword(
        @Header("Authorization") token: String,
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
}