package com.group22.smartgreenhouse.data.api

import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.data.model.SupportMessageRequest
import com.group22.smartgreenhouse.data.model.SupportMessageResponse
import com.group22.smartgreenhouse.data.model.SupportMessageResponseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SupportMessageApi {
    @POST("api/v1/UserSupportMessage")
    suspend fun sendSupportMessage(
        @Header("Authorization") token: String,
        @Body request: SupportMessageRequest
    ): Response<Int>

    // Admin panel - listing all support messages
    @GET("api/v1/UserSupportMessage/all")
    suspend fun getSupportMessages(
        @Header("Authorization") token: String,
        @Query("OnlyUnopened") onlyUnopened: Boolean = false,
        @Query("PageNumber") pageNumber: Int = 1,
        @Query("PageSize") pageSize: Int = 10
    ): Response<SupportMessageResponse>

    // Get single support message details
    @GET("api/v1/UserSupportMessage/{id}")
    suspend fun getSupportMessage(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<SupportMessage>

    // Submit response to a support message
    @PUT("api/v1/UserSupportMessage/{id}")
    suspend fun submitResponse(
        @Header("Authorization") token: String,
        @Path("id") id: Int,  // ID in URL path
        @Body requestBody: SupportMessageResponseRequest  // Raw JSON string as body
    ): Response<Unit>

    // Get user support messages
    @GET("api/v1/UserSupportMessage/user")
    suspend fun getUserSupportMessages(
        @Header("Authorization") token: String
    ): Response<List<SupportMessage>>

    // Delete suppotr message by id
    @DELETE("api/v1/UserSupportMessage/{id}")
    suspend fun deleteUserSupportMessage(
        @Header("Authorization") token: String,
        @Path("id") messageId: Int
    ): Response<Unit>

}