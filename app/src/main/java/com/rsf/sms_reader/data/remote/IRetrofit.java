package com.rsf.sms_reader.data.remote;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface IRetrofit {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("/send_message")
    Call<JsonObject> postRawJSON(@Body JsonObject jsonObject);
}
