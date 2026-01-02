package com.group22.smartgreenhouse.data.repository

import android.content.Context
import android.util.Log
import com.group22.smartgreenhouse.data.api.RetrofitClient
import com.group22.smartgreenhouse.data.model.AuthRequest
import com.group22.smartgreenhouse.data.model.AuthResponse
import com.group22.smartgreenhouse.data.model.ChangePasswordRequest
import com.group22.smartgreenhouse.data.model.ErrorResponse
import com.group22.smartgreenhouse.data.model.ForgotPasswordRequest
import com.group22.smartgreenhouse.data.model.ForgotPasswordResponse
import com.group22.smartgreenhouse.data.model.RegisterRequest
import com.group22.smartgreenhouse.data.model.RegisterResponse
import com.group22.smartgreenhouse.data.model.ResetPasswordRequest
import com.group22.smartgreenhouse.data.model.ResetPasswordResponse
import com.group22.smartgreenhouse.data.model.UserBasicInfo
import kotlinx.serialization.json.Json
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(private val context: Context) {

    private val api by lazy { RetrofitClient.getAuthApi(context) }
    private val json = Json { ignoreUnknownKeys = true } // Configure JSON parser

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(AuthRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorResponse(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception(parseErrorResponse(e.message)))
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        userName: String,
        password: String,
        confirmPassword: String
    ): Result<RegisterResponse> {
        return try {
            val response = api.register(
                RegisterRequest(
                    firstName,
                    lastName,
                    email,
                    userName,
                    password,
                    confirmPassword
                )
            )

            if (response.isSuccessful) {
                val message = response.body() ?: "Registration successful"
                val registerResponse = RegisterResponse(
                    message = message,
                    errors = null,
                    confirmationUrl = null
                )
                Result.success(registerResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorResponse(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorResponse(errorBody)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("RegisterError", "Exception during registration", e)
            Result.failure(Exception(e.message ?: "Network or unexpected error"))
        }
    }

    /*
        private fun parseErrorResponse(errorBody: String?): String {
            return try {
                errorBody?.let {
                    json.decodeFromString<ErrorResponse>(it).getUserFriendlyMessage()
                } ?: "Registration failed"
            } catch (e: Exception) {
                "Registration failed (invalid error format)"
            }
        }


     */

    private fun parseErrorResponse(errorBody: String?): String {
        return try {
            errorBody?.let { body ->
                try {
                    val jsonObject = JSONObject(body)

                    // Handle invalid credentials format
                    if (jsonObject.has("Message")) {
                        return jsonObject.getString("Message")
                    }

                    // Handle other error formats...
                    if (jsonObject.has("success") && !jsonObject.getBoolean("success")) {
                        return jsonObject.optString("message", "Operation failed")
                    }

                    if (jsonObject.has("errors")) {
                        val errorsObj = jsonObject.getJSONObject("errors")
                        if (errorsObj.length() > 0) {
                            val firstErrorKey = errorsObj.keys().next()
                            val errorArray = errorsObj.getJSONArray(firstErrorKey)
                            return errorArray.getString(0)
                        }
                    }

                    jsonObject.optString("message",
                        jsonObject.optString("title",
                            jsonObject.optString("detail", "Request failed")))
                } catch (e: JSONException) {
                    body
                }
            } ?: "Unknown error occurred"
        } catch (e: Exception) {
            "Failed to process error response"
        }
    }

    suspend fun changePassword(
        token: String,
        current: String,
        newPass: String,
        confirm: String
    ): Result<String /*message*/> {
        return try {
            val res = api.changePassword(
                "Bearer $token",
                ChangePasswordRequest(current, newPass, confirm)
            )
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.message)
            } else {
                Result.failure(Exception(res.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBasicInfo(token: String): Result<UserBasicInfo> {
        return try {
            val response = api.getUserBasicInfo("Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<ForgotPasswordResponse> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(authToken: String, request: ResetPasswordRequest): Result<ResetPasswordResponse> {
        return try {
            val response = api.resetPassword(authToken, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response from server"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorResponse(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseErrorResponse(e.response()?.errorBody()?.string())))
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }
}
