package com.group22.smartgreenhouse.data.api

import com.group22.smartgreenhouse.data.model.AgriProductPriceResponse
import com.group22.smartgreenhouse.data.model.Municipality
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MunicipalityApi {
    @GET("api/Municipality")
    suspend fun getMunicipalities(
        @Header("Authorization") token: String
    ): Response<List<Municipality>>

    @GET("api/v1/AgriProductsPrices/prices")
    suspend fun getAgriProductPrices(
        @Query("municipalityId") municipalityId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Header("Authorization") token: String
    ): Response<AgriProductPriceResponse>
}