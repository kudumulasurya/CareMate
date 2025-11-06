// File: ApiService.kt
package com.example.medicare

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("chat")
    fun getAiResponse(@Body request: ChatRequest): Call<ChatResponse>

    @GET("sessions")
    fun getSessions(): Call<AllSessionsResponse>

    @GET("session/{session_id}")
    fun loadSessionHistory(@Path("session_id") sessionId: String): Call<HistoryResponse>

    @POST("new-chat")
    fun newChat(): Call<Map<String, String>>

    @DELETE("session/{session_id}")
    fun deleteSession(@Path("session_id") sessionId: String): Call<Map<String, String>>
}
