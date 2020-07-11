package com.rsf.sms_reader

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface StatsService {
    @Headers("Content-Type: application/json")
    @POST("/send_message")
    fun post(@Body body: String): Call<RegResponse>
}

data class RegResponse(
        @SerializedName("success")
        val successCode: Int,
        @SerializedName("token")
        val token: String
)