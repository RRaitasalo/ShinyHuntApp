package com.example.shinyhuntapp.data.network

import com.example.shinyhuntapp.data.local.PokemonDetails
import com.example.shinyhuntapp.data.local.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 1025,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET
    suspend fun getPokemonDetailsByUrl(
        @Url url: String
    ): PokemonDetails



}