package com.group22.smartgreenhouse.data.api

import android.content.Context
import com.group22.smartgreenhouse.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    private const val BASE_URL = "https://10.0.2.2:9001/"
    private var retrofit: Retrofit? = null

    fun getAuthApi(context: Context): AuthApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(context))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(AuthApi::class.java)
    }

    private fun getSafeOkHttpClient(context: Context): OkHttpClient {
        // Load certificate from raw resources
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val inputStream = context.resources.openRawResource(R.raw.my_cert)
        val certificate = certificateFactory.generateCertificate(inputStream)
        inputStream.close()

        // Create KeyStore containing our certificate
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("ca", certificate)
        }

        // Create TrustManager that trusts our KeyStore
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(keyStore)
        }

        // Get the first X509TrustManager
        val trustManager = trustManagerFactory.trustManagers.first {
            it is X509TrustManager
        } as X509TrustManager

        // Create SSLContext
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManager), SecureRandom())
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { hostname, session ->
                hostname == "10.0.2.2" || hostname == "localhost"
            }
            .connectTimeout(30, TimeUnit.SECONDS) // Add timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Add timeout
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Add logging
            })
            .build()

    }

    fun getGreenhouseApi(context: Context): GreenhouseApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(context))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(GreenhouseApi::class.java)
    }

    fun getSupportApi(context: Context): SupportMessageApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(SupportMessageApi::class.java)
    }

    fun getAccountApi(context: Context): AuthApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(AuthApi::class.java)
    }

    fun getAdminApi(ctx: Context): AdminApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(ctx))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(AdminApi::class.java)
    }

    fun getMunicipalityApi(context: Context): MunicipalityApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getSafeOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(MunicipalityApi::class.java)
    }
}