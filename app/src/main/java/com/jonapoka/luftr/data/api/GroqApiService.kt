package com.jonapoka.luftr.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class GroqRequest(
    val messages: List<GroqMessage>,
    val model: String = "llama3-8b-8192",
    val temperature: Float = 1f,
    val max_tokens: Int = 1024
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage,
    @SerializedName("finish_reason")
    val finishReason: String
)

interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    suspend fun createChatCompletion(
        @retrofit2.http.Header("Authorization") authorization: String,
        @Body request: GroqRequest
    ): GroqResponse
}
