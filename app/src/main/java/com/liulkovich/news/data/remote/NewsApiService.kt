package com.liulkovich.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything?apiKey=179e49438a6849adbd12ffa98c82d747")
    suspend fun loadArticles(
        @Query("q") topic: String
    ): NewsResponseDto
}