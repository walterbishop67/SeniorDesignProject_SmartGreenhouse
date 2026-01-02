package com.group22.smartgreenhouse.data.api

import com.group22.smartgreenhouse.data.model.DeleteResponse
import com.group22.smartgreenhouse.data.model.ElectronicCard
import com.group22.smartgreenhouse.data.model.GreenhouseRequest
import com.group22.smartgreenhouse.data.model.GreenhouseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GreenhouseApi {
    @POST("api/v1/Greenhouse")
    suspend fun createGreenhouse(
        @Body greenhouse: GreenhouseRequest,
        @Header("Authorization") token: String
    ): Response<Void> // or Response<GreenhouseResponse> if there's a response body

    @GET("api/v1/Greenhouse")
    suspend fun getGreenhouses(
        @Header("Authorization") token: String
    ): Response<List<GreenhouseResponse>>

    @GET("api/v1/ElectronicCard/electronic-card/get-available-electronic-card-by-user-id")
    suspend fun getAvailableElectronicCards(@Header("Authorization") token: String): List<ElectronicCard>

    @GET("api/v1/ElectronicCard/by-greenhouse/{greenhouseId}")
    suspend fun getGreenhouseDetails(
        @Header("Authorization") token: String,
        @Path("greenhouseId") greenhouseId: String
    ): Response<List<ElectronicCard>>

    @DELETE("api/v1/Greenhouse/{greenhouseId}")
    suspend fun deleteGreenhouse(
        @Path("greenhouseId") greenhouseId: String,
        @Header("Authorization") token: String
    ): Response<DeleteResponse>

}