package ru.ksart.potatohandbook.model.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface Api {
    @GET
    suspend fun getFile(
        @Url url: String
    ): ResponseBody
}
