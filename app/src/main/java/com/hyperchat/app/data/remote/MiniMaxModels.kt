package com.hyperchat.app.data.remote

import retrofit2.http.*

interface MiniMaxApiService {

    @POST("text/chatcompletion_v2")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @POST("v1/chat/completions")
    suspend fun chatCompletionV1(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 2048
)

data class Message(
    val role: String,
    val content: String
)

data class MessageContent(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)

data class ChatCompletionResponse(
    val id: String?,
    val choices: List<Choice>?,
    val usage: Usage?,
    val base_resp: BaseResp?
)

data class Choice(
    val index: Int,
    val message: ResponseMessage,
    val finish_reason: String?
)

data class ResponseMessage(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)

data class BaseResp(
    val status_code: Int?,
    val status_text: String?
)
