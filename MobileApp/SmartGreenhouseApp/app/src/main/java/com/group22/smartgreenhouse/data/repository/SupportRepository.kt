package com.group22.smartgreenhouse.data.repository

import com.group22.smartgreenhouse.data.api.SupportMessageApi
import com.group22.smartgreenhouse.data.model.SupportMessage
import com.group22.smartgreenhouse.data.model.SupportMessageRequest
import com.group22.smartgreenhouse.data.model.SupportMessageResponse
import com.group22.smartgreenhouse.data.model.SupportMessageResponseRequest
import com.group22.smartgreenhouse.util.SessionManager
import retrofit2.Response

class SupportRepository(private val api: SupportMessageApi) {
    suspend fun sendMessage(token: String, request: SupportMessageRequest): Result<Int> {
        return try {
            val response = api.sendSupportMessage("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: -1)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSupportMessages(
        onlyUnopened: Boolean = false,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ): Result<SupportMessageResponse> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getSupportMessages("Bearer $token", onlyUnopened, pageNumber, pageSize)

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

    suspend fun getSupportMessage(messageId: Int): Result<SupportMessage> {
        if (messageId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid message ID"))
        }
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getSupportMessage("Bearer $token", messageId)

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

    suspend fun submitResponse(messageId: Int, responseText: String): Result<Unit> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val request = SupportMessageResponseRequest(
                id = messageId,
                messageResponse = responseText
            )
            val response = api.submitResponse("Bearer $token",messageId, request)

            if (response.isSuccessful) {
                Result.success(Unit)
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

    suspend fun getSupportMessagesWithStatus(
        onlyUnopened: Boolean = false,
        pageNumber: Int = 1,
        pageSize: Int = 10
    ): Result<List<SupportMessage>> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))

            // First get the basic list
            val listResponse = api.getSupportMessages("Bearer $token", onlyUnopened, pageNumber, pageSize)

            if (!listResponse.isSuccessful) {
                return handleErrorResponse(listResponse)
            }

            val messages = listResponse.body()?.data ?: return Result.failure(Exception("Empty response"))

            // Fetch details for each message to get response status
            val detailedMessages = messages.map { basicMessage ->
                api.getSupportMessage("Bearer $token", basicMessage.id).body() ?: basicMessage
            }

            Result.success(detailedMessages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleErrorResponse(response: Response<*>): Result<List<SupportMessage>> {
        return when (response.code()) {
            401 -> {
                SessionManager.clearSession()
                Result.failure(Exception("Session expired"))
            }
            else -> Result.failure(
                Exception(response.errorBody()?.string() ?: "Unknown error")
            )
        }
    }

    // SupportRepository.kt
    suspend fun getUserSupportMessages(): Result<List<SupportMessage>> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.getUserSupportMessages("Bearer $token")

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

    suspend fun deleteUserSupportMessage(messageId: Int): Result<Unit> {
        return try {
            val token = SessionManager.jwtToken ?: return Result.failure(Exception("Not authenticated"))
            val response = api.deleteUserSupportMessage("Bearer $token", messageId)
            if (response.isSuccessful) {
                Result.success(Unit)
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

}