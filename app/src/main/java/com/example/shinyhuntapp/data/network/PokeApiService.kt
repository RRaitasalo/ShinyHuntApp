package com.example.shinyhuntapp.data.network

import com.example.shinyhuntapp.data.local.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse
}